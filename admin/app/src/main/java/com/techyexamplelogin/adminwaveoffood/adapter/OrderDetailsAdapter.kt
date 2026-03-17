package com.techyexamplelogin.adminwaveoffood.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techyexamplelogin.adminwaveoffood.databinding.OrderdetailItemBinding

class OrderDetailsAdapter(

    private var foodNames: ArrayList<String>,
    private var foodImages: ArrayList<String>,
    private var foodQuantities: ArrayList<Int>,
    private var foodPrices: ArrayList<String>

) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailsViewHolder {

        val binding = OrderdetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderDetailsViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return minOf(
            foodNames.size,
            foodImages.size,
            foodQuantities.size,
            foodPrices.size
        )
    }

    inner class OrderDetailsViewHolder(
        private val binding: OrderdetailItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            val safePosition = bindingAdapterPosition
            if (safePosition == RecyclerView.NO_POSITION) return

            binding.foodName.text = foodNames[safePosition]

            binding.foodQuantity.text =
                foodQuantities[safePosition].toString()

            binding.foodPrice.text = foodPrices[safePosition]

            val imageUrl = foodImages[safePosition]

            if (imageUrl.isNotEmpty()) {
                Glide.with(binding.root)
                    .load(Uri.parse(imageUrl))
                    .into(binding.foodImage)
            }
        }
    }
}