package com.techyexamplelogin.foodorderingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techyexamplelogin.foodorderingapp.adapter.NotificationAdapter
import com.techyexamplelogin.foodorderingapp.databinding.FragmentNotificationBottomBinding


class Notification_Bottom_Fragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        val notifications= listOf("Your Order has been Cancelled Successfully","Order has been taken by the driver","Congrats your order Placed")
        val notificationImages= listOf(R.drawable.sademoji,R.drawable.truck,R.drawable.congrats)
        val adapter=NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )
        binding.notificationRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter=adapter
        return binding.root
    }

    companion object {

    }
}