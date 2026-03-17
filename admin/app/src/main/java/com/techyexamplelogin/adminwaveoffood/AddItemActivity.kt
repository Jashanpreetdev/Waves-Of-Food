package com.techyexamplelogin.adminwaveoffood

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.techyexamplelogin.adminwaveoffood.databinding.ActivityAddItemBinding
import com.techyexamplelogin.adminwaveoffood.model.AllMenu

class AddItemActivity : AppCompatActivity() {

    // Food Item Details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private var foodImageUri: Uri? = null

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Pick image
        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Add item button
        binding.AddItemButton.setOnClickListener {

            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredient.text.toString().trim()

            if (foodName.isBlank() || foodPrice.isBlank() ||
                foodDescription.isBlank() || foodIngredient.isBlank()
            ) {
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (foodImageUri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageToCloudinary()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    // ================= CLOUDINARY IMAGE UPLOAD =================

    private fun uploadImageToCloudinary() {

        MediaManager.get().upload(foodImageUri!!)
            .unsigned("menu_upload") // 🔴 replace with your preset
            .option("folder", "menu_images")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String?) {
                    Toast.makeText(this@AddItemActivity, "Uploading image...", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url") as String
                    Toast.makeText(
                        this@AddItemActivity,
                        "Image added successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    uploadDataToFirebase(imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(this@AddItemActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    // ================= FIREBASE DATABASE UPLOAD =================

    private fun uploadDataToFirebase(imageUrl: String) {

        val menuRef = database.getReference("menu")
        val newItemKey = menuRef.push().key ?: return

        val newItem = AllMenu(
            foodName = foodName,
            foodPrice = foodPrice,
            foodDescription = foodDescription,
            foodIngredient = foodIngredient,
            foodImage = imageUrl
        )

        menuRef.child(newItemKey)
            .setValue(newItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
            }
    }

    // ================= IMAGE PICKER =================

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                foodImageUri = it
                binding.selectedImage.setImageURI(it)
            }
        }
}
