package com.tofukma.shippingapp.common

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.os.Build
import android.os.SystemClock
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import java.util.logging.Handler

class MarkerAnimation {
    companion object{
        fun animateMarkerToGB(marker:Marker,finalPosition:LatLng, latLngInterpolator:LatLngInterpolator)
        {
            val startPosition = marker.position
            var handler = android.os.Handler()
            var start = SystemClock.uptimeMillis()
            val interpolator = AccelerateDecelerateInterpolator()
            val durationInMs = 3000

            handler.post(object :Runnable{
                var elapsed = 0L
                var t = 0L
                var v = 0f


                override fun run() {
                    elapsed = SystemClock.uptimeMillis() - start
                    t = elapsed/durationInMs
                    v = interpolator.getInterpolation(t.toFloat())

                    marker.position = latLngInterpolator.interpolate(v,startPosition,finalPosition)

                    // repeat till progress is complete
                    if(t<1 ){
                        handler.postDelayed(this,16)

                    }
                }

            })



        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        fun animateMarkerToHC(marker: Marker,finalPosition: LatLng,latLngInterpolator: LatLngInterpolator)
        {
            val startPostion = marker.position
            val valueAnimator = ValueAnimator()

            valueAnimator.addUpdateListener {
                var v  = it.animatedFraction
                var newPosition = latLngInterpolator.interpolate(v,startPostion,finalPosition)
                marker.position = newPosition

            }
            valueAnimator.setFloatValues(0f,1f)
            valueAnimator.duration = 3000
            valueAnimator.start()

        }


    }



}