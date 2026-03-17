package com.techyexamplelogin.adminwaveoffood.model

import java.io.Serializable

data class OrderDetails(

    var userUid: String? = null,
    var userName: String? = null,
    var foodNames: ArrayList<String> = arrayListOf(),
    var foodImages: ArrayList<String> = arrayListOf(),
    var foodPrices: ArrayList<String> = arrayListOf(),
    var foodQuantities: ArrayList<Int> = arrayListOf(),
    var address: String? = null,
    var totalPrice: Int? = null,
    var phoneNumber: String? = null,
    var orderAccepted: Boolean = false,
    var paymentReceived: Boolean = false,
    var itemPushKey: String? = null,
    var currentTime: Long = 0

) : Serializable