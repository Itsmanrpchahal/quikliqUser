package com.quikliq.quikliquser.models


import java.io.Serializable


class ProviderModel : Serializable {
    var provider_id: String? = null
    var name: String? = null
    var picture: String? = null
    var opentime: String? = null
    var closetime: String? = null
    var rating: Int = 0
    var lat: Double = 0.00
    var lng: Double? = 0.00
    var distance: String? = null
    var Status: Int? = null
    var adress: String? = null
}