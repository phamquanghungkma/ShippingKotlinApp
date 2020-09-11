package com.tofukma.shippingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tofukma.shippingapp.R
import com.tofukma.shippingapp.model.IntroSlide

class IntroSilderAdapter(private val introSlides: List<IntroSlide>) : RecyclerView.Adapter<IntroSilderAdapter.IntroSlideViewHolder>() {

    inner  class IntroSlideViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val textTitle  = view.findViewById<TextView>(R.id.intro_title)
        private  val imageIntro = view.findViewById<ImageView>(R.id.intro_image)

        fun bind(introSlide: IntroSlide){
            textTitle.text = introSlide.titleIntro
            imageIntro.setImageResource(introSlide.imageIntro)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.intro_screen,parent,false)
        )
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }


}