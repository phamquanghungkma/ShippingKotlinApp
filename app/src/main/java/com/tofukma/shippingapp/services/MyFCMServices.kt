package com.tofukma.shippingapp.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tofukma.shippingapp.common.Common
import java.util.*

class MyFCMServices : FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Common.updateToken(this,p0,false,true)// shipper = true vì ở app shipper
        // Khi một thiết bị cài đặt ứng dụng thì nó sẽ tạo ra một device_token, ta sẽ gửi device_token đó lên Firebase
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val dataRev = remoteMessage.data
        if(dataRev != null ){
            Common.showNotification(this,
                Random().nextInt(),dataRev[Common.NOTI_TITLE],dataRev[Common.NOTI_CONTENT],null)
        }
    }


}