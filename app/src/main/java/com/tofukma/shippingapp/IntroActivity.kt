package com.tofukma.shippingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.tofukma.shippingapp.adapter.IntroSilderAdapter
import com.tofukma.shippingapp.model.IntroSlide
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {

    var introSliderViewPager : ViewPager2 ?= null
    var buttonNext : Button ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        init()


        introSliderViewPager!!.adapter = introSliderAdapter
        setUpIndicator()
        setCurrentIndicator(0)
        introSliderViewPager!!.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        buttonNext!!.setOnClickListener {
            if(introSliderViewPager!!.currentItem + 1 < introSliderAdapter.itemCount){
                introSliderViewPager!!.currentItem += 1
            } else {
                Intent(applicationContext,MainActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        textSkipIntro.setOnClickListener {
            Intent(applicationContext,MainActivity::class.java).also {
                startActivity(it)
            }

        }

    }

     fun init() {
         introSliderViewPager = findViewById(R.id.introSliderViewPager)
        buttonNext  = findViewById(R.id.btn_next)
    }


    private  val introSliderAdapter = IntroSilderAdapter(
        listOf(
            IntroSlide("Book & Order All Products at Any Time",
                R.drawable.pic1
                ),
            IntroSlide("Your Package in Our safe Hands",
                R.drawable.pic2
            ),
            IntroSlide("Committed Delivery on Time",
                R.drawable.pic3
            ),
            IntroSlide("Faster & Safest Delivery",
                R.drawable.pic4
            )

        )
    )

    private fun setUpIndicator(){
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for (i in indicators.indices){

            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext,R.drawable.indicator_inactive)
                )
                this?.layoutParams = layoutParams

            }
            indicatorsContainer.addView(indicators[i])

        }


    }
    private fun setCurrentIndicator(index:Int){

        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount){
            val imageView  = indicatorsContainer.get(i) as ImageView
            if(i == index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )

            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )


            }


        }
    }





}