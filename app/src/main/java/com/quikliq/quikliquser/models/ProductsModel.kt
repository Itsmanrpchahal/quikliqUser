package com.quikliq.quikliquser.models

import java.io.Serializable


class ProductsModel : Serializable {
    var id: String? = null
    var userid: String? = null
    var category: String? = null
    var product_name: String? = null
    var description: String? = null
    var price: Int = 0
    var quantity: Int = 0
    var image: String? = null
    var isactive: Int = 0
}