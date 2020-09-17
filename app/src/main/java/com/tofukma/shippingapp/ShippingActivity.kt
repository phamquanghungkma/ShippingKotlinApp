package com.tofukma.shippingapp

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.firebase.ui.auth.data.model.Resource
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.common.LatLngInterpolator
import com.tofukma.shippingapp.common.MarkerAnimation
import com.tofukma.shippingapp.model.ShippingOrderModel
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_shipping.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat

class ShippingActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest:LocationRequest
    private lateinit var locationCallback:LocationCallback

    private  var shipperMarker: Marker ?= null
    private var shippingOrderModel : ShippingOrderModel ?= null

    var isInit = false
    var previousLocation : Location ?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping)

        buildLocaltionRequest()
        buildLocationCallback()
        setShippingOrderModel()

        Dexter.withActivity(this).withPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener{
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment

                    mapFragment.getMapAsync(this@ShippingActivity)
                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@ShippingActivity)
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@ShippingActivity,"Bạn phải câp quyền ",Toast.LENGTH_LONG).show()
                }

            }).check()


    }

    private fun setShippingOrderModel() {
        Paper.init(this)
        val data = Paper.book().read<String>(Common.SHIPPING_DATA)
        if(!TextUtils.isEmpty(data)){
            shippingOrderModel = Gson().fromJson<ShippingOrderModel>(data,object:TypeToken<ShippingOrderModel>(){

            }.type)
            if(shippingOrderModel != null){
                Common.setPanStringColor("Tên:",shippingOrderModel!!.orderModel!!.userName,txt_name,
                Color.parseColor("#333639")
                    )
                Common.setPanStringColor("Địa chỉ:",shippingOrderModel!!.orderModel!!.shippingAddress,
                    txt_address,Color.parseColor("#673ab7")
                    )
                Common.setPanStringColor("Mã :",shippingOrderModel!!.orderModel!!.key,
                    txt_order_number,Color.parseColor("#795548")
                )

                txt_date!!.text = StringBuilder().append(SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(
                    shippingOrderModel!!.orderModel!!.createDate
                ))
//                Glide.with(this).load(shippingOrderModel!!.orderModel!!.carItemList!![0].foodImage)
//                    .into(img_food_image)
                    Glide.with(this).load(R.drawable.photo).into(img_food_image)
//                Log.d("Bill",shippingOrderModel!!.orderModel!!.carItemList!![1].foodName.toString())

            }

        }else {


        }
    }

    private fun buildLocationCallback() {
        locationCallback = object:LocationCallback(){
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                val locationShipper = LatLng(p0!!.lastLocation.latitude,p0!!.lastLocation.longitude)
                if(shipperMarker == null){
                    val height = 80
                    val width = 80
                    val bitmapDrawable = ContextCompat.getDrawable(this@ShippingActivity,R.drawable.food_delivery)
                    val b = bitmapDrawable!!.toBitmap()
                    val smallMarker = Bitmap.createScaledBitmap(b,width,height,false)
                    shipperMarker = mMap!!.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .position(locationShipper)
                        .title("Vị trí của bạn ")
                    )
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(locationShipper,17f))

                }else {
                    shipperMarker!!.position = locationShipper

                }
                if (isInit && previousLocation != null)
                {
                    val previousLocationLatLng = LatLng(previousLocation!!.latitude,previousLocation!!.longitude)
                    MarkerAnimation.animateMarkerToGB(shipperMarker!!,locationShipper,LatLngInterpolator.Spherical())
                    shipperMarker!!.rotation = Common.getBearing(previousLocationLatLng,locationShipper)
                    mMap!!.animateCamera(CameraUpdateFactory.newLatLng(locationShipper))

                    previousLocation = p0.lastLocation

                }
                if(!isInit){
                    isInit = true
                    previousLocation = p0.lastLocation
                }
            }
        }
    }

    private fun buildLocaltionRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.setInterval(15000); // sec
        locationRequest.setFastestInterval(10000); // 10 sec
        locationRequest.setSmallestDisplacement(20f)


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isZoomControlsEnabled = true
        try {

            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.uber_light_with_label))
            if(!success){
                Log.d("ToFuKMA","Failed to load map style")
            }
        } catch (ex:Resources.NotFoundException){
            Log.d("ToFuKMA","Not Found json string for map style")

        }
    }

    override fun onDestroy() {

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }
}