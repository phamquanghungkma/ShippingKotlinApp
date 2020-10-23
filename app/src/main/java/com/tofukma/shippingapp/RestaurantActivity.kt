package com.tofukma.shippingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.tofukma.shippingapp.Adapter.MyRestaurantAdapter
import com.tofukma.shippingapp.Eventbus.RestrantSelectEvent
import com.tofukma.shippingapp.callback.IRestaurantCallbackListener
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.model.RestaurantModel
import com.tofukma.shippingapp.model.ShipperUserModel
import io.paperdb.Paper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.sql.DatabaseMetaData

class RestaurantActivity : AppCompatActivity(), IRestaurantCallbackListener {

    lateinit var recycler_restaurant:RecyclerView
    lateinit var dialog:AlertDialog
    lateinit var  layoutAnimationController: LayoutAnimationController
     var adapter:MyRestaurantAdapter ?= null;
    var serverRef:DatabaseReference ?= null
    lateinit var listener: IRestaurantCallbackListener





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)


        initViews()
        loadRestaurantFromServer()
    }

    private fun loadRestaurantFromServer() {
        dialog.show()

        val restaurantModels = ArrayList<RestaurantModel>()
        val restaurantRef = FirebaseDatabase.getInstance()
            .getReference(Common.RESTAURANT_REF)
        restaurantRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                listener.onRestaurantLoadFaild(error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(restaurantSnapshot in snapshot.children)
                    {
                        val restaurantModel = restaurantSnapshot.getValue(RestaurantModel::class.java)
                        restaurantModel!!.uid = restaurantSnapshot.key!!
                        restaurantModels.add(restaurantModel!!)
                    }
                    if(restaurantModels.size > 0)
                        listener.onRestaurantLoadSuccess(restaurantModels)
                    else
                        listener.onRestaurantLoadFaild("Danh sách nhà hàng rỗng")

                } else {
                    listener.onRestaurantLoadFaild(" Danh sách nhà hàng không tìm thấy ")

                }
            }

        })
    }

    private fun initViews() {

        listener = this

        dialog = AlertDialog.Builder(this).setCancelable(false)
            .setMessage("Please wait ... ").create();
        dialog.show()
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(this,R.anim.layout_item_from_left);
        val layoutManager = LinearLayoutManager(this);
        layoutManager.orientation = RecyclerView.VERTICAL
        recycler_restaurant = findViewById(R.id.recycler_restaurant)
        recycler_restaurant.layoutManager = layoutManager
        recycler_restaurant.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))

    }

    override fun onRestaurantLoadSuccess(restaurantList: List<RestaurantModel>) {
        dialog.dismiss()
        adapter = MyRestaurantAdapter(this,restaurantList)
        recycler_restaurant.adapter = adapter!!
        recycler_restaurant.layoutAnimation = layoutAnimationController


    }

    override fun onRestaurantLoadFaild(message: String) {
            Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)

        super.onStop()

    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun  onRestaurantSelectEvent(restaurantSelectEvent: RestrantSelectEvent){
        if(restaurantSelectEvent != null){

            val user = FirebaseAuth.getInstance().currentUser
            if(user != null ){
                checkServerUserFromServer(user,restaurantSelectEvent.restaurantModel)
            }
        }


    }

    private fun checkServerUserFromServer(user: FirebaseUser, restaurantModel: RestaurantModel) {
        dialog.show()
        serverRef = FirebaseDatabase.getInstance()
            .getReference(Common.RESTAURANT_REF)
            .child(restaurantModel.uid)
            .child(Common.SHIPPER_REF)

        serverRef!!.child(user.uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                    dialog.dismiss()
                Toast.makeText(this@RestaurantActivity,error.message,Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
              if(snapshot.exists()){
                  val userModel = snapshot.getValue(ShipperUserModel::class.java)
                  if(userModel!!.isActive)
                      gotoHomeActivity(userModel,restaurantModel)
                  else{
                      dialog.dismiss()
                        Toast.makeText(this@RestaurantActivity,"Bạn phải acitive ",Toast.LENGTH_LONG).show()
                  }
              }
                else {
                  dialog.dismiss()
                  showRegisterDialog(user,restaurantModel.uid)
              }
            }


        })
    }

    private fun showRegisterDialog(user: FirebaseUser, uid: String) {

        val builder = AlertDialog.Builder(this@RestaurantActivity)
        builder.setTitle(" Getingt Started")
        builder.setMessage("Create an account to continued ")
        builder.setIcon(R.drawable.logo)

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);
        val edt_name = itemView.findViewById<View>(R.id.edt_name) as EditText
        val edt_phone = itemView.findViewById<View>(R.id.edt_phone) as EditText


        // set data
        edt_phone.setText(user.phoneNumber)

        builder.setNegativeButton("CANCEL", {dialogInterface, i -> dialogInterface.dismiss() })
            .setPositiveButton("REGISTER", { _,_ ->
                if(TextUtils.isEmpty(edt_name.text)){
                    Toast.makeText(this@RestaurantActivity,"Please enter your name ",Toast.LENGTH_SHORT).show();
                    return@setPositiveButton
                }
                val shipperUserModel =  ShipperUserModel();
                shipperUserModel.uid = user.uid
                shipperUserModel.name = edt_name.text.toString()
                shipperUserModel.phone = edt_phone.text.toString()
                shipperUserModel.isActive = false // default fail, we must active by manual on Firebase

                dialog!!.show()

                // Init server Ref
                serverRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF).child(uid)
                    .child(Common.SHIPPER_REF)
                serverRef!!.child(shipperUserModel.uid!!).setValue(shipperUserModel)
                    .addOnFailureListener{e -> dialog!!.dismiss()
                        Toast.makeText(this@RestaurantActivity,""+e.message,Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                            _ -> dialog!!.dismiss()
                        Toast.makeText(this@RestaurantActivity,"Register success ! Admin will check and active user soon",Toast.LENGTH_SHORT).show()

                    }

            })

        builder.setView(itemView)

        val registerDialog = builder.create()
        registerDialog.show()
    }

    private fun gotoHomeActivity(userModel: ShipperUserModel,restaurantModel: RestaurantModel) {
            dialog.dismiss()
        Common.currentShipperUser = userModel;
        val jsonEncode = Gson().toJson(restaurantModel)

        Paper.init(this)
        Paper.book().write(Common.RESTAURANT_REF,jsonEncode)
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }
}