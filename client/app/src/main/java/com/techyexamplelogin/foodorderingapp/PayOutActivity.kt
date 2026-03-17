package com.techyexamplelogin.foodorderingapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.techyexamplelogin.foodorderingapp.databinding.ActivityPayOutBinding
import com.techyexamplelogin.foodorderingapp.model.OrderDetails

class PayOutActivity : AppCompatActivity() {

    lateinit var binding: ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String

    private var totalAmount: Int = 0

    private lateinit var foodItemName: ArrayList<String>
    private lateinit var foodItemPrice: ArrayList<String>
    private lateinit var foodItemImage: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>

    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        setUserData()

        // Receive data from intent safely
        foodItemName =
            intent.getStringArrayListExtra("FoodItemName") ?: arrayListOf()

        foodItemPrice =
            intent.getStringArrayListExtra("FoodItemPrice") ?: arrayListOf()

        foodItemImage =
            intent.getStringArrayListExtra("FoodItemImage") ?: arrayListOf()

        foodItemQuantities =
            intent.getIntegerArrayListExtra("FoodItemQuantities") ?: arrayListOf()

        totalAmount = calculateTotalAmount()

        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText("$totalAmount$")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.PlaceMyOrder.setOnClickListener {

            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please Enter All The Details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
    }

    private fun calculateTotalAmount(): Int {

        var total = 0

        for (i in foodItemPrice.indices) {

            val cleanPrice = foodItemPrice[i]
                .replace("$", "")
                .replace("/-", "")
                .trim()

            val priceInt = cleanPrice.toIntOrNull() ?: 0
            val quantity = foodItemQuantities[i]

            total += priceInt * quantity
        }

        return total
    }

    private fun placeOrder() {

        userId = auth.currentUser?.uid ?: return

        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key

        val orderDetails = OrderDetails().apply {
            userUid = userId
            userName = name
            foodNames = foodItemName
            foodPrices = foodItemPrice
            foodImages = foodItemImage
            foodQuantities = foodItemQuantities
            address = this@PayOutActivity.address
            totalPrice = totalAmount
            phoneNumber = phone
            currentTime = time
            this.itemPushKey = itemPushKey
            orderAccepted = false
            paymentReceived = false
        }

        databaseReference.child("OrderDetails")
            .child(itemPushKey!!)
            .setValue(orderDetails)
            .addOnSuccessListener {

                val bottomSheetDialog = CongratsBottomSheet()
                bottomSheetDialog.show(supportFragmentManager, "Congrats")

                removeItemFromCart()
                addOrderToHistory(orderDetails)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed To Order", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user")
            .child(userId)
            .child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails)
    }

    private fun removeItemFromCart() {
        databaseReference.child("user")
            .child(userId)
            .child("CartItems")
            .removeValue()
    }

    private fun setUserData() {

        val user = auth.currentUser ?: return
        val userReference = databaseReference.child("user").child(user.uid)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val names = snapshot.child("name").getValue(String::class.java) ?: ""
                val addresses = snapshot.child("address").getValue(String::class.java) ?: ""
                val phones = snapshot.child("phone").getValue(String::class.java) ?: ""

                binding.name.setText(names)
                binding.address.setText(addresses)
                binding.phone.setText(phones)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PayOutActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
