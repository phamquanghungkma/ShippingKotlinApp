package com.tofukma.shippingapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.model.RestaurantModel
import com.tofukma.shippingapp.model.ShipperUserModel
import dmax.dialog.SpotsDialog
import io.paperdb.Paper
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        //Delete data
        Paper.init(this)
//        Paper.book().delete(Common.TRIP_START)
//        Paper.book().delete(Common.SHIPPING_DATA)
    }

    private var firebaseAuth: FirebaseAuth?= null
    private var listener:FirebaseAuth.AuthStateListener ?= null
    private var dialog:android.app.AlertDialog ?= null
    private var serverRef: DatabaseReference?=null
    private var providers : List<AuthUI.IdpConfig> ?= null

    companion object {

        private val APP_REQUEST_CODE = 7171

    }


    override fun onStart() {
        super.onStart()
        firebaseAuth!!.addAuthStateListener(listener!!)
    }

    override fun onStop() {
        firebaseAuth!!.removeAuthStateListener(listener!!)
        super.onStop()

    }
    private fun init() {
        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.PhoneBuilder().build())

        serverRef = FirebaseDatabase.getInstance().getReference(Common.SHIPPER_REF)

        firebaseAuth = FirebaseAuth.getInstance()
        dialog = SpotsDialog.Builder().setContext(this).setCancelable(false).build()
        listener = object: FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                val user = firebaseAuth.currentUser
                if(user != null){ // neu co user r
                    Paper.init(this@MainActivity)
                    val jsonEncode = Paper.book().read<String>(Common.RESTAURANT_SAVE)
                    val restaurantModel = Gson().fromJson<RestaurantModel>(jsonEncode,object:TypeToken<RestaurantModel>(){}.type)
                    if(restaurantModel != null)
                        checkServerUseFromFirebase(user,restaurantModel!!)
                    else {
                        startActivity(Intent(this@MainActivity,RestaurantActivity::class.java))
                        finish()
                    }
                }
                else {
                    phoneLogin()
                }
            }

        }
    }

    private fun checkServerUseFromFirebase(user: FirebaseUser,restaurantModel: RestaurantModel) {
        dialog!!.show()
        //init server ref
        serverRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
            .child(restaurantModel.uid).child(Common.SHIPPER_REF)
        serverRef!!.child(user.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onCancelled(error: DatabaseError) {
                        dialog!!.dismiss()
                        Toast.makeText(this@MainActivity,""+error.message,Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(snapShot: DataSnapshot) {
                        if (snapShot.exists()){
                            val userModel = snapShot.getValue(ShipperUserModel::class.java)
                            if(userModel!!.isActive){
                                gotoHomeActivity(userModel,restaurantModel)
                            }
                            else
                            {
                                dialog!!.dismiss()
                                Toast.makeText(this@MainActivity,"You must be allowed from Admin to access this app ",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                })
    }

    private fun gotoHomeActivity(userModel: ShipperUserModel,restaurantModel: RestaurantModel) {
        dialog!!.dismiss()
        Common.currentRestaurant = restaurantModel
        Common.currentShipperUser = userModel
        startActivity(Intent(this,HomeActivity::class.java))
        finish()

    }



    private fun phoneLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers!!).build(),APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == APP_REQUEST_CODE){

//            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val user = FirebaseAuth.getInstance().currentUser
            }
            else {
                Toast.makeText(this,"Failed to sign in ",Toast.LENGTH_SHORT).show()
            }


        }
    }
}