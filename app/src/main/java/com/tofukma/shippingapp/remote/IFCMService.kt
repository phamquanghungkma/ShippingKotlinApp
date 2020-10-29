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
        "Authorization:key=AAAAYfeAido:APA91bFnVrG7V9La7xWp2qP_Nb8_UEYTWCax2nigY2nemx4RDt995vw-Q-EWLiwv7jDhl0J7mc5-u-fSjqQhonSlyZ6xj14mi1z23LgQBDj-zhtjWSTzBgOYAOWT1wVobEMvv5cxCKmZ"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData): Observable<FCMResponse>


}