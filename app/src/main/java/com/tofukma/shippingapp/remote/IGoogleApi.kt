package com.tofukma.shippingapp.remote

import io.reactivex.Observable
import io.reactivex.Observer
import retrofit2.http.GET
import retrofit2.http.Query

interface IGoogleApi {
    @GET("map/api/directions/json")
    fun getDirections(
        @Query("mode") mode:String?,
        @Query("transit_routing_preference") transit_routing:String?,
        @Query("origin") origin:String?,
        @Query("destination") destination:String?,
        @Query("key") key:String?):Observable<String?>?
}