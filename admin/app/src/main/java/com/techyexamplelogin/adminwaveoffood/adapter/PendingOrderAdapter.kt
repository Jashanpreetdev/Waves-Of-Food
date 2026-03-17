package com.techyexamplelogin.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.techyexamplelogin.adminwaveoffood.databinding.PendingOrdersItemBinding

class PendingOrderAdapter : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder> {

    private val customerNames: MutableList<String>
    private val quantity: MutableList<String>
    private val foodImage: MutableList<String>
    private val context: Context
    private val itemClicked: OnItemClicked

    constructor(
        customerNames: MutableList<String>,
        quantity: MutableList<String>,
        foodImage: MutableList<String>,
        context: Context,
        itemClicked: OnItemClicked
    ) : super() {
        this.customerNames = customerNames
        this.quantity = quantity
        this.foodImage = foodImage
        this.context = context
        this.itemClicked = itemClicked
    }

    interface OnItemClicked {
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingOrderViewHolder {

        val binding = PendingOrdersItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PendingOrderViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(
        private val binding: PendingOrdersItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var isAccepted = false

        fun bind(position: Int) {

            binding.customerName.text = customerNames[position]

            binding.pendingOrderQuantity.text = quantity[position]

            val uri = Uri.parse(foodImage[position])

            Glide.with(context)
                .load(uri)
                .into(binding.orderFoodImage)

            binding.orderedAcceptButton.text =
                if (isAccepted) "Dispatch" else "Accept"

            binding.orderedAcceptButton.setOnClickListener {

                val currentPosition = bindingAdapterPosition

                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                if (!isAccepted) {

                    isAccepted = true
                    binding.orderedAcceptButton.text = "Dispatch"

                    Toast.makeText(
                        context,
                        "Order Accepted",
                        Toast.LENGTH_SHORT
                    ).show()
                    itemClicked.onItemAcceptClickListener(position)

                } else {

                    customerNames.removeAt(currentPosition)
                    quantity.removeAt(currentPosition)
                    foodImage.removeAt(currentPosition)

                    notifyItemRemoved(currentPosition)

                    Toast.makeText(
                        context,
                        "Order Dispatched",
                        Toast.LENGTH_SHORT
                    ).show()
                    itemClicked.onItemDispatchClickListener(position)
                }
            }

            binding.root.setOnClickListener {

                val currentPosition = bindingAdapterPosition

                if (currentPosition != RecyclerView.NO_POSITION) {
                    itemClicked.onItemClickListener(currentPosition)
                }
            }
        }
    }
}