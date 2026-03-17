package com.techyexamplelogin.foodorderingapp.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.techyexamplelogin.foodorderingapp.RecentOrderItems
import com.techyexamplelogin.foodorderingapp.adapter.BuyAgainAdapter
import com.techyexamplelogin.foodorderingapp.databinding.FragmentHistoryBinding
import com.techyexamplelogin.foodorderingapp.model.OrderDetails

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var buyAgainAdapter: BuyAgainAdapter

    private var userId: String = ""
    private var listOfOrderItem: ArrayList<OrderDetails> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        retrieveBuyHistory()

        binding.recentbuyitem.setOnClickListener {
            seeItemsRecentBuy()
        }

        binding.receivedButton.setOnClickListener {
            updateOrderStatus()
        }

        return binding.root
    }

    // Update paymentReceived when user clicks Received
    private fun updateOrderStatus() {

        if (listOfOrderItem.isEmpty()) return

        val pushKey = listOfOrderItem[0].itemPushKey ?: return

        val completeOrderReference =
            database.reference.child("CompletedOrder").child(pushKey)

        completeOrderReference.child("paymentReceived").setValue(true)
    }

    // Open recent order activity
    private fun seeItemsRecentBuy() {

        if (listOfOrderItem.isNotEmpty()) {

            val intent = Intent(requireContext(), RecentOrderItems::class.java)

            intent.putExtra("RecentBuyOrderItem", listOfOrderItem)

            startActivity(intent)
        }
    }

    // Retrieve user buy history
    private fun retrieveBuyHistory() {

        binding.recentbuyitem.visibility = View.INVISIBLE

        userId = auth.currentUser?.uid ?: return

        val buyHistoryReference =
            database.reference.child("user").child(userId).child("BuyHistory")

        val sortingQuery = buyHistoryReference.orderByChild("currentTime")

        // Real-time updates when admin changes orderAccepted
        sortingQuery.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                listOfOrderItem.clear()

                for (buySnapshot in snapshot.children) {

                    val order =
                        buySnapshot.getValue(OrderDetails::class.java)

                    order?.let {
                        listOfOrderItem.add(it)
                    }
                }

                listOfOrderItem.reverse()

                if (listOfOrderItem.isNotEmpty()) {

                    setDataInRecentBuyItem()
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Display most recent order
    private fun setDataInRecentBuyItem() {

        binding.recentbuyitem.visibility = View.VISIBLE

        val recentOrderItem = listOfOrderItem.firstOrNull() ?: return

        binding.buyAgainFoodName.text =
            recentOrderItem.foodNames?.firstOrNull() ?: ""

        binding.buyAgainFoodPrice.text =
            recentOrderItem.foodPrices?.firstOrNull() ?: ""

        val image = recentOrderItem.foodImages?.firstOrNull() ?: ""

        Glide.with(requireContext())
            .load(Uri.parse(image))
            .into(binding.buyAgainFoodImage)

        val isAccepted = recentOrderItem.orderAccepted

        if (isAccepted) {

            binding.orderStatus.setCardBackgroundColor(Color.GREEN)

            binding.receivedButton.visibility = View.VISIBLE

        } else {

            binding.orderStatus.setCardBackgroundColor(Color.GRAY)

            binding.receivedButton.visibility = View.GONE
        }
    }

    // RecyclerView for previous orders
    private fun setPreviousBuyItemsRecyclerView() {

        val foodNames = mutableListOf<String>()
        val foodPrices = mutableListOf<String>()
        val foodImages = mutableListOf<String>()

        for (i in 1 until listOfOrderItem.size) {

            listOfOrderItem[i].foodNames?.firstOrNull()?.let {
                foodNames.add(it)
            }

            listOfOrderItem[i].foodPrices?.firstOrNull()?.let {
                foodPrices.add(it)
            }

            listOfOrderItem[i].foodImages?.firstOrNull()?.let {
                foodImages.add(it)
            }
        }

        binding.BuyAgainRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        buyAgainAdapter = BuyAgainAdapter(
            foodNames,
            foodPrices,
            foodImages,
            requireContext()
        )

        binding.BuyAgainRecyclerView.adapter = buyAgainAdapter
    }


}
