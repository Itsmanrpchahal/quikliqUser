package com.quikliq.quikliquser.models


import java.io.Serializable

class HistoryModel : Serializable {
    var status: Boolean? = null
    var message: String? = null
    var orderid: String? = null
    var providername: String? = null
    var totalprice: String? = null
    var image: String? = null
    var order_status: String? = null
    var datetime : String? = null
    var products: ArrayList<String>? = null
    var price: ArrayList<String>? = null
    var quantity: ArrayList<String>? = null
    var items: ArrayList<String>? = null
}