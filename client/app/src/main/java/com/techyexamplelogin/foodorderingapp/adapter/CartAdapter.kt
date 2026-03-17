package com.techyexamplelogin.foodorderingapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.techyexamplelogin.foodorderingapp.databinding.CartItemBinding

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private val cartDescriptions: MutableList<String>,
    private val cartImages: MutableList<String>,
    private val cartQuantity: MutableList<Int>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val databaseRef: DatabaseReference

    init {
        val userId = auth.currentUser?.uid ?: ""
        databaseRef = FirebaseDatabase.getInstance()
            .reference.child("user")
            .child(userId)
            .child("CartItems")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding =
            CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    //get updated quantity
    fun getUpdatedItemsQuantities(): MutableList<Int>{
        val itemQuantity=mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {

            binding.cartFoodName.text = cartItems[position]
            binding.cartItemPrice.text = cartItemPrices[position]
            binding.catItemQuantity.text = cartQuantity[position].toString()

            val uri = Uri.parse(cartImages[position])
            Glide.with(context).load(uri).into(binding.cartImage)

            binding.plusbutton.setOnClickListener {
                increaseQuantity()
            }

            binding.minusbutton.setOnClickListener {
                decreaseQuantity()
            }

            binding.deletebutton.setOnClickListener {
                deleteItem()
            }
        }

        private fun increaseQuantity() {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return

            if (cartQuantity[position] < 10) {
                cartQuantity[position]++
                notifyItemChanged(position)
            }
        }

        private fun decreaseQuantity() {
            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return

            if (cartQuantity[position] > 1) {
                cartQuantity[position]--
                notifyItemChanged(position)
            }
        }

        private fun deleteItem() {

            val position = adapterPosition
            if (position == RecyclerView.NO_POSITION) return

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val keyList = snapshot.children.mapNotNull { it.key }

                    if (position >= keyList.size) return

                    val key = keyList[position]

                    databaseRef.child(key).removeValue()
                        .addOnSuccessListener {

                            if (position < cartItems.size) {

                                cartItems.removeAt(position)
                                cartItemPrices.removeAt(position)
                                cartDescriptions.removeAt(position)
                                cartImages.removeAt(position)
                                cartQuantity.removeAt(position)

                                notifyDataSetChanged()  // safest option
                            }

                            Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error deleting item", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
