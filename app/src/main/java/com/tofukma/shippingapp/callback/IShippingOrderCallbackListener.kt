package com.tofukma.shippingapp.callback

import com.tofukma.shippingapp.model.ShippingOrderModel

interface IShippingOrderCallbackListener {
    fun onShippingOrderLoadSuccess(shippingOrders:List<ShippingOrderModel>)
    fun onShippingOrderLoadFailed(message:String)
}