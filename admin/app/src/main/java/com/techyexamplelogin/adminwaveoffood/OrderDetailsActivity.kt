package com.techyexamplelogin.adminwaveoffood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.techyexamplelogin.adminwaveoffood.adapter.OrderDetailsAdapter
import com.techyexamplelogin.adminwaveoffood.databinding.ActivityOrderDetailsBinding
import com.techyexamplelogin.adminwaveoffood.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {

    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        getDataFromIntent()
    }

    private fun getDataFromIntent() {

        val orderDetails =
            intent.getSerializableExtra("UserOrderDetails") as? OrderDetails
                ?: return

        binding.name.text = orderDetails.userName ?: ""
        binding.address.text = orderDetails.address ?: ""
        binding.phone.text = orderDetails.phoneNumber ?: ""

        binding.totalPay.text = orderDetails.totalPrice?.toString() ?: "0"

        foodNames = orderDetails.foodNames
        foodImages = orderDetails.foodImages
        foodQuantity = orderDetails.foodQuantities
        foodPrices = orderDetails.foodPrices

        setAdapter()
    }

    private fun setAdapter() {

        binding.orderDetailsRecyclerView.layoutManager =
            LinearLayoutManager(this)

        val adapter = OrderDetailsAdapter(
            foodNames,
            foodImages,
            foodQuantity,
            foodPrices
        )

        binding.orderDetailsRecyclerView.adapter = adapter
    }
}