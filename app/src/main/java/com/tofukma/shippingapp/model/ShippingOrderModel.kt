package com.tofukma.shippingapp.model

class ShippingOrderModel {
    val restaurantKey: String ?= null
    var key:String?=null
    var shipperPhone:String?=null
    var shipperName:String?=null
    var currentLat  = 0.0
    var currentLng =0.0
    var orderModel:OrderModel?=null
    var isStartTrip=false
    var estimateTime : String = "UNKNOWN"


}