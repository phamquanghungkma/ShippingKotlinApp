package com.tofukma.shippingapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tofukma.shippingapp.callback.IShippingOrderCallbackListener
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.model.ShippingOrderModel

class HomeViewModel : ViewModel(), IShippingOrderCallbackListener {

   private val orderModelMultableLiveData:MutableLiveData<List<ShippingOrderModel>>
    val messageError:MutableLiveData<String>
    private val listener:IShippingOrderCallbackListener
    init {
        orderModelMultableLiveData = MutableLiveData()
        messageError = MutableLiveData()
        listener = this
    }
    fun getOrderMOdelMutableLiveData(shipperPhone:String):MutableLiveData<List<ShippingOrderModel>>{
        loadOrderByShipper(shipperPhone)
        return orderModelMultableLiveData
    }

    private fun loadOrderByShipper(shipperPhone: String) {
val tempList : MutableList<ShippingOrderModel> = ArrayList()
        val orderRef  = FirebaseDatabase.getInstance()
            .getReference(Common.SHIPPING_ORDER_REF)
            .orderByChild("shipperPhone")
            .equalTo(Common.currentShipperUser!!.phone)
        orderRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
               listener.onShippingOrderLoadFailed(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
               for (itemSnapshot in p0.children){
                   val shippingOrder = itemSnapshot.getValue(ShippingOrderModel::class.java!!)
                   tempList.add(shippingOrder!!)
               }
                listener.onShippingOrderLoadSuccess(tempList)
               }


        })
    }

    override fun onShippingOrderLoadSuccess(shippingOrders: List<ShippingOrderModel>) {
        orderModelMultableLiveData.value = shippingOrders
    }

    override fun onShippingOrderLoadFailed(message: String) {
        messageError.value = message
    }
}