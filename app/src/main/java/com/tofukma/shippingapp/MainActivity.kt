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
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.model.ShipperUserModel
import dmax.dialog.SpotsDialog
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
         init()
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
                if(user != null){
                    checkServerUseFromFirebase(user)

                }
                else {

                    phoneLogin()
                }
            }

        }
    }

    private fun checkServerUseFromFirebase(user: FirebaseUser) {
        dialog!!.show()
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
                                gotoHomeActivity(userModel)
                            }
                            else
                            {
                                dialog!!.dismiss()
                                Toast.makeText(this@MainActivity,"You must be allowed from Admin to access this app ",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            dialog!!.dismiss()
                            showRegisterDialog(user)
                        }
                    }


                })
    }

    private fun gotoHomeActivity(userModel: ShipperUserModel) {
        dialog!!.dismiss()
        Common.currentShipperUser = userModel
        startActivity(Intent(this,HomeActivity::class.java))
        finish()

    }

    private fun showRegisterDialog(user: FirebaseUser) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Register")
        builder.setMessage("Please fill information \n Admin will accept your account kit ")

        val itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);
        val edt_name = itemView.findViewById<View>(R.id.edt_name) as EditText
        val edt_phone = itemView.findViewById<View>(R.id.edt_phone) as EditText

        // set data
        edt_phone.setText(user.phoneNumber)

        builder.setNegativeButton("CANCEL", {dialogInterface, i -> dialogInterface.dismiss() })
                .setPositiveButton("REGISTER", { _,_ ->
                    if(TextUtils.isEmpty(edt_name.text)){
                        Toast.makeText(this,"Please enter your name ",Toast.LENGTH_SHORT).show();
                        return@setPositiveButton
                    }
                    val shipperUserModel =  ShipperUserModel();
                    shipperUserModel.uid = user.uid
                    shipperUserModel.name = edt_name.text.toString()
                    shipperUserModel.phone = edt_phone.text.toString()
                    shipperUserModel.isActive = false // default fail, we must active by manual on Firebase

                    dialog!!.show()
                    serverRef!!.child(shipperUserModel.uid!!).setValue(shipperUserModel)
                            .addOnFailureListener{e -> dialog!!.dismiss()
                                Toast.makeText(this,""+e.message,Toast.LENGTH_SHORT).show()
                            }
                            .addOnCompleteListener {
                                _ -> dialog!!.dismiss()
                                Toast.makeText(this,"Register success ! Admin will check and active user soon",Toast.LENGTH_SHORT).show()

                            }

                })

        builder.setView(itemView)

        val registerDialog = builder.create()
        registerDialog.show()
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