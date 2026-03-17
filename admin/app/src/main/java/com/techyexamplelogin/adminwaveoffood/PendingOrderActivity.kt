package com.techyexamplelogin.adminwaveoffood

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.techyexamplelogin.adminwaveoffood.adapter.PendingOrderAdapter
import com.techyexamplelogin.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.techyexamplelogin.adminwaveoffood.model.OrderDetails

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {

    private lateinit var binding: ActivityPendingOrderBinding

    private val listOfName = mutableListOf<String>()
    private val listOfTotalPrice = mutableListOf<String>()
    private val listOfImageFirstFoodOrder = mutableListOf<String>()
    private val listOfOrderItem = ArrayList<OrderDetails>()

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("OrderDetails")

        binding.pendingOrderRecyclerView.layoutManager =
            LinearLayoutManager(this)

        binding.backButton.setOnClickListener { finish() }

        getOrdersDetails()
    }

    private fun getOrdersDetails() {

        databaseOrderDetails
            .orderByChild("currentTime")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    listOfOrderItem.clear()

                    for (orderSnapshot in snapshot.children) {

                        val order =
                            orderSnapshot.getValue(OrderDetails::class.java)

                        order?.let { listOfOrderItem.add(it) }
                    }

                    listOfOrderItem.reverse()

                    prepareRecyclerData()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun prepareRecyclerData() {

        listOfName.clear()
        listOfTotalPrice.clear()
        listOfImageFirstFoodOrder.clear()

        for (order in listOfOrderItem) {

            listOfName.add(order.userName ?: "Unknown")

            listOfTotalPrice.add(order.totalPrice?.toString() ?: "0")

            val image = order.foodImages.firstOrNull() ?: ""
            listOfImageFirstFoodOrder.add(image)
        }

        setAdapter()
    }

    private fun setAdapter() {

        val adapter = PendingOrderAdapter(
            listOfName,
            listOfTotalPrice,
            listOfImageFirstFoodOrder,
            this,
            this
        )

        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {

        if (position == RecyclerView.NO_POSITION ||
            position >= listOfOrderItem.size
        ) return

        val intent = Intent(this, OrderDetailsActivity::class.java)

        intent.putExtra("UserOrderDetails", listOfOrderItem[position])

        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {

        val pushKey = listOfOrderItem[position].itemPushKey ?: return

        database.reference
            .child("OrderDetails")
            .child(pushKey)
            .child("orderAccepted")
            .setValue(true)

        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchClickListener(position: Int) {

        val pushKey = listOfOrderItem[position].itemPushKey ?: return

        val dispatchReference =
            database.reference.child("CompletedOrder").child(pushKey)

        dispatchReference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {

                deleteThisItemFromOrderDetails(pushKey)
            }
    }

    private fun deleteThisItemFromOrderDetails(pushKey: String) {

        database.reference
            .child("OrderDetails")
            .child(pushKey)
            .removeValue()
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Order is Dispatched",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Order is not Dispatched",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {

        val userId = listOfOrderItem[position].userUid ?: return
        val pushKey = listOfOrderItem[position].itemPushKey ?: return

        val buyHistoryReference =
            database.reference
                .child("user")
                .child(userId)
                .child("BuyHistory")
                .child(pushKey)

        buyHistoryReference.child("orderAccepted").setValue(true)

        databaseOrderDetails
            .child(pushKey)
            .child("orderAccepted")
            .setValue(true)
    }

}
