package com.techyexamplelogin.foodorderingapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.techyexamplelogin.foodorderingapp.R
import com.techyexamplelogin.foodorderingapp.adapter.BuyAgainAdapter
import com.techyexamplelogin.foodorderingapp.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {
        private lateinit var binding:FragmentHistoryBinding
        private lateinit var buyAgainAdapter: BuyAgainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHistoryBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView(){
        val buyAgainFoodName= arrayListOf("Food 2","Food 2","Food 3")
        val buyAgainFoodNPrice= arrayListOf("$10","$0","$30")
        val buyAgainFoodImage= arrayListOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3)
        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName, buyAgainFoodNPrice, buyAgainFoodImage)
        binding.BuyAgainRecyclerView.adapter= buyAgainAdapter
        binding.BuyAgainRecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }

    companion object {

            }
}
