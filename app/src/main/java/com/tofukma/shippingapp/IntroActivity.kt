package com.tofukma.shippingapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.tofukma.shippingapp.adapter.IntroSilderAdapter
import com.tofukma.shippingapp.model.IntroSlide
import kotlinx.android.synthetic.main.activity_intro.*


class IntroActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
            introSliderViewPager.adapter = introSliderAdapter
            runIntroScreen()


//        if(isFirstOpen()){
//            // check neu la lan dau luanch app thi se chay introduce screen con k thi thoi
//            introSliderViewPager.adapter = introSliderAdapter
//            runIntroScreen()
//            Toast.makeText(this,"lần đầu tiên bạn sử dụng ứng dụng ",Toast.LENGTH_LONG).show()
//        }
//        else {
////            Intent(applicationContext,MainActivity::class.java).also{
////                startActivity(it)
////            }
//            Toast.makeText(this,"lần thứ hai bạn sử dụng ứng dụng ",Toast.LENGTH_LONG).show()
//
//
//        }


    }



    // check if the first time app is launched
    fun isFirstOpen(): Boolean {
        val pref = this.getPreferences(MODE_PRIVATE)
        val isFirst = pref.getBoolean("key", true)
        with(pref.edit()){
            putBoolean("key", false)
            commit()
        }
        return isFirst
    }

    private fun runIntroScreen() {
        setUpIndicator()
        setCurrentIndicator(0)
        introSliderViewPager!!.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        btn_next!!.setOnClickListener {
            if(introSliderViewPager!!.currentItem + 1 < introSliderAdapter.itemCount){
                introSliderViewPager!!.currentItem += 1
            } else {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }

        textSkipIntro.setOnClickListener {
            Intent(applicationContext,MainActivity::class.java).also {
                startActivity(it)
            }

        }
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