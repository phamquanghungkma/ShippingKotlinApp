package com.tofukma.shippingapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tofukma.shippingapp.Adapter.MyShippingOrderAdapter
import com.tofukma.shippingapp.R
import com.tofukma.shippingapp.common.Common
import com.tofukma.shippingapp.model.ShippingOrderModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    var recycler_order:RecyclerView?=null
    var layoutAnimationController : LayoutAnimationController?=null
    var adapter:MyShippingOrderAdapter?=null
    
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        initView(root)
        homeViewModel!!.messageError.observe(viewLifecycleOwner, Observer { s:String->Toast.makeText(context,s,Toast.LENGTH_SHORT).show() })
        homeViewModel!!.getOrderMOdelMutableLiveData(Common.currentShipperUser!!.phone!!)
            .observe(viewLifecycleOwner, Observer { shippingOrderModel:List<ShippingOrderModel> ->
                adapter = MyShippingOrderAdapter(context!!,shippingOrderModel)
                recycler_order!!.adapter = adapter
                recycler_order!!.layoutAnimation = layoutAnimationController
            })

        return root
    }

    private fun initView(root: View?) {
        recycler_order = root!!.findViewById(R.id.recycler_order) as  RecyclerView
        recycler_order!!.setHasFixedSize(true)
        recycler_order!!.layoutManager = LinearLayoutManager(context)
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(context,R.anim.layout_item_from_left)
    }
}