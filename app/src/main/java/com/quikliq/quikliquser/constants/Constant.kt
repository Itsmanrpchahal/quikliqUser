package com.quikliq.quikliquser.constants


import java.util.ArrayList


object Constant {
    val BASE_URL = "https://professionaler.com/weed/api/index.php/"    //production
    val GPS_REQUEST = 101

    val IS_LOGGED_IN = "is_logged_in"


    val ACTION_CODE = "action_code"
    val FCM_TOKEN = "fcm_token"
    val HAS_BADGE = "has_badge"

    var LOCATION:String? = null
    var lat:Double?=0.00
    var lng:Double?=0.00
    var provider_id:String?=null
}
