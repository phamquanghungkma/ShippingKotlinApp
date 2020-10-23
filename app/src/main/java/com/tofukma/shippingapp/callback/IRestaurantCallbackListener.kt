package com.tofukma.shippingapp.callback

import com.tofukma.shippingapp.model.RestaurantModel

interface IRestaurantCallbackListener {
    fun onRestaurantLoadSuccess(restaurantList: List<RestaurantModel>)
    fun onRestaurantLoadFaild(message:String)

}

