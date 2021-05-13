package com.tofukma.shippingapp.remote

import com.tofukma.shippingapp.model.FCMResponse
import com.tofukma.shippingapp.model.FCMSendData
import io.reactivex.Observable

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.*

interface IFCMService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAYfeAido:APA91bHsJLO--30Mr0NahNvRLMwYFRAbDjN_4NYBBDm6FSgzhX1m6N_Sys9Vp_CS_UGvxBmu6ht53qu8S8ViogP_upT3oACpmDqz7QW1aWZsrnyKZyKmdpBgKzMoZzjNSXXvJ8JdZKEl"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData): Observable<FCMResponse>


}