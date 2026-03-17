package com.techyexamplelogin.foodorderingapp.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.techyexamplelogin.foodorderingapp.PayOutActivity
import com.techyexamplelogin.foodorderingapp.adapter.CartAdapter
import com.techyexamplelogin.foodorderingapp.databinding.FragmentCartBinding
import com.techyexamplelogin.foodorderingapp.model.CartItems

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val foodNames = mutableListOf<String>()
    private val foodPrices = mutableListOf<String>()
    private val foodDescriptions = mutableListOf<String>()
    private val foodImagesUri = mutableListOf<String>()
    private val foodIngredients = mutableListOf<String>()
    private val quantity = mutableListOf<Int>()

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        retrieveCartItems()

        binding.proceedButton.setOnClickListener {

            if (foodNames.isEmpty()) {
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val foodQuantities = cartAdapter.getUpdatedItemsQuantities()

            orderNow(
                foodNames,
                foodPrices,
                foodDescriptions,
                foodImagesUri,
                foodIngredients,
                foodQuantities
            )
        }

        return binding.root
    }

    // 🔥 Directly pass existing cart data to checkout
    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {

        if (!isAdded || context == null) return

        val intent = Intent(requireContext(), PayOutActivity::class.java)

        intent.putStringArrayListExtra("FoodItemName", ArrayList(foodName))
        intent.putStringArrayListExtra("FoodItemPrice", ArrayList(foodPrice))
        intent.putStringArrayListExtra("FoodItemImage", ArrayList(foodImage))
        intent.putStringArrayListExtra("FoodItemDescription", ArrayList(foodDescription))
        intent.putStringArrayListExtra("FoodItemIngredient", ArrayList(foodIngredient))
        intent.putIntegerArrayListExtra("FoodItemQuantities", ArrayList(foodQuantities))

        startActivity(intent)
    }

    private fun retrieveCartItems() {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val foodReference = database.reference
            .child("user")
            .child(userId)
            .child("CartItems")

        foodReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!isAdded || _binding == null) return

                foodNames.clear()
                foodPrices.clear()
                foodDescriptions.clear()
                foodImagesUri.clear()
                foodIngredients.clear()
                quantity.clear()

                for (foodSnapshot in snapshot.children) {

                    val cartItem = foodSnapshot.getValue(CartItems::class.java)

                    cartItem?.foodName?.let { foodNames.add(it) }
                    cartItem?.foodPrice?.let { foodPrices.add(it) }
                    cartItem?.foodDescription?.let { foodDescriptions.add(it) }
                    cartItem?.foodImage?.let { foodImagesUri.add(it) }
                    cartItem?.foodQuantity?.let { quantity.add(it) }
                    cartItem?.foodIngredient?.let { foodIngredients.add(it) }
                }

                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(context, "Data not fetched", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setAdapter() {

        if (!isAdded || _binding == null) return

        cartAdapter = CartAdapter(
            requireContext(),
            foodNames,
            foodPrices,
            foodDescriptions,
            foodImagesUri,
            quantity
        )

        binding.cartRecyclerView.layoutManager =
            LinearLayoutManager(context)

        binding.cartRecyclerView.adapter = cartAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
