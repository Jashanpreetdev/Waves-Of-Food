package com.techyexamplelogin.foodorderingapp.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.database.*
import com.techyexamplelogin.foodorderingapp.MenuBottomSheetFragment
import com.techyexamplelogin.foodorderingapp.R
import com.techyexamplelogin.foodorderingapp.adapter.MenuAdapter
import com.techyexamplelogin.foodorderingapp.databinding.FragmentHomeBinding
import com.techyexamplelogin.foodorderingapp.model.MenuItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val menuItems = mutableListOf<MenuItem>()
    private lateinit var adapter: MenuAdapter

    companion object {
        private val cachedMenu = mutableListOf<MenuItem>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MenuAdapter(menuItems, requireContext())

        binding.PopulerRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            adapter = this@HomeFragment.adapter
        }

        retrieveAndDisplayPopularItems()
        setupSlider()

        binding.viewAllMenu.setOnClickListener {
            MenuBottomSheetFragment().show(parentFragmentManager, "Menu")
        }
    }

    private fun retrieveAndDisplayPopularItems() {

        // 🚀 instant load from cache
        if (cachedMenu.isNotEmpty()) {
            menuItems.addAll(cachedMenu)
            adapter.notifyDataSetChanged()
            return
        }

        val foodRef = FirebaseDatabase.getInstance().reference.child("menu")

        foodRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!isAdded || _binding == null) return

                menuItems.clear()
                cachedMenu.clear()

                for (child in snapshot.children) {
                    val item = child.getValue(MenuItem::class.java)
                    item?.let {
                        menuItems.add(it)
                        cachedMenu.add(it)
                    }
                }

                menuItems.shuffle()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupSlider() {

        val imageList = arrayListOf(
            SlideModel(R.drawable.banner1, ScaleTypes.FIT),
            SlideModel(R.drawable.banner2, ScaleTypes.FIT),
            SlideModel(R.drawable.banner3, ScaleTypes.FIT)
        )

        binding.imageSlider.setImageList(imageList)

        // ✅ IMPORTANT (this makes it slide)
        binding.imageSlider.startSliding(1000) // 3 sec interval
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
