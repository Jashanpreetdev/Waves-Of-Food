package com.techyexamplelogin.foodorderingapp.model

import java.io.Serializable

data class OrderDetails(

    var userUid: String? = null,
    var userName: String? = null,
    var foodNames: ArrayList<String>? = null,
    var foodImages: ArrayList<String>? = null,
    var foodPrices: ArrayList<String>? = null,
    var foodQuantities: ArrayList<Int>? = null,
    var address: String? = null,
    var totalPrice: Int? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var itemPushKey: String? = null,
    var currentTime: Long = 0

) : Serializable