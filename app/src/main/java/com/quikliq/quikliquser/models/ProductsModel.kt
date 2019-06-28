package com.quikliq.quikliquser.models

import java.io.Serializable


class ProductsModel : Serializable {
    var id: String? = null
    var userid: String? = null
    var category: String? = null
    var product_name: String? = null
    var description: String? = null
    var price: String? = null
    var quantity: String? = null
    var image: String? = null
    var isactive: String? = null
    var category_name: String? = null
    var business_name: String? = null
    var business_address: String? = null
}