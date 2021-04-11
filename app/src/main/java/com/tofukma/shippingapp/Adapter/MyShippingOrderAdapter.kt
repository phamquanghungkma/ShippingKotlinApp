package com.tofukma.shippingapp.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.tofukma.shippingapp.R
import com.tofukma.shippingapp.ShippingActivity
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.model.ShippingOrderModel
import io.paperdb.Paper
import java.lang.StringBuilder
import java.text.SimpleDateFormat

class MyShippingOrderAdapter(var context: Context,var shippingOrderModelList:List<ShippingOrderModel>) :RecyclerView.Adapter<MyShippingOrderAdapter.MyViewHolder>() {

   var simpleDateFormat:SimpleDateFormat
    init {
        simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        Paper.init(context)
    }



    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
       var txt_date:TextView
        var txt_order_address:TextView
        var txt_order_number:TextView
        var txt_payment:TextView
        var img_food:ImageView
        var btn_ship_now:MaterialButton
        init {
            txt_date = itemView.findViewById(R.id.txt_date) as TextView
            txt_order_address = itemView.findViewById(R.id.txt_order_address) as TextView
            txt_order_number = itemView.findViewById(R.id.txt_order_number) as TextView
            txt_payment = itemView.findViewById(R.id.txt_payment) as TextView
            img_food = itemView.findViewById(R.id.img_food) as ImageView
            btn_ship_now = itemView.findViewById(R.id.btn_ship_now) as MaterialButton
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val itemView = LayoutInflater.from(context).inflate(R.layout.layout_shipping_order,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return shippingOrderModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
Glide.with(context)
    .load(
        shippingOrderModelList.get(position)
            .orderModel!!.carItemList!![0].foodImage)
    .into(holder.img_food)
        holder.txt_date!!.text = StringBuilder(simpleDateFormat.format(shippingOrderModelList[position].orderModel!!.createDate))

   Common.setPanStringColor("Mã đơn: ",shippingOrderModelList[position].orderModel!!.key,
       holder.txt_order_number,Color.parseColor("#BA454A"))

    Common.setPanStringColor("Địa chỉ : ",shippingOrderModelList[position].orderModel!!.shippingAddress,
        holder.txt_order_address,Color.parseColor("#BA454A"))

    Common.setPanStringColor("Thanh toán: ",shippingOrderModelList[position].orderModel!!.transactionId,
        holder.txt_payment,Color.parseColor("#BA454A"))

        if(shippingOrderModelList[position].isStartTrip){
            holder.btn_ship_now.isEnabled = false
        }

        // Event, khi an vao nui ship now, sẽ chuyển sang màn hình mapp

        holder.btn_ship_now.setOnClickListener {

            // Write data
            Paper.book().write(Common.SHIPPING_DATA,Gson().toJson(shippingOrderModelList[position]))
            Log.d("vitri", shippingOrderModelList[position].currentLat.toString())
            context.startActivity(Intent(context,ShippingActivity::class.java))



        }
    }
}