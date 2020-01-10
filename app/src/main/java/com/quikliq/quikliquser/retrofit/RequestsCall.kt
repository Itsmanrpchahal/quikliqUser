package retrofit


import android.content.Context
import android.net.Uri
import com.google.gson.JsonObject
import com.quikliq.quikliquser.interfaces.ApiHelper
import com.quikliq.quikliquser.utilities.ImageCompressor
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import java.io.File
import java.util.ArrayList

class RequestsCall {


    fun login(mobile: String, password: String, device_type : String, device_token : String, usertype : String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.login("UserLogin",mobile, password, device_type, device_token,usertype)
    }

    fun signup(first_name: String, last_name: String, email : String, password : String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.signup("Signup",first_name, last_name, email, password)
    }

    fun mobile(mobile_number: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.mobile("NewOtp",mobile_number)
    }

    fun saveAdditionalDetail(devicetype: String, firstname: String, lastname : String, mobile : String, email : String, password : String, address : String, usertype : String, devicetoken : String,city:String,state:String,zip:String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.saveAdditionalDetail("SaveAdditionalDetail",devicetype, firstname, lastname, mobile, email, password, address,usertype,devicetoken,city,state,zip)
    }

    fun profile(userID: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.profile("UserProfile",userID)
    }

    fun UpdateProfile(
        context: Context,
        userid: String,
        firstname: String,
        lastname: String,
        email: String,
        image: Uri?
    ): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        val imageCompressor = ImageCompressor()
        val path = imageCompressor.compressImage(context, image.toString())
        val fbody = RequestBody.create(MediaType.parse("multipart/form-data"), File(path))
        val body = MultipartBody.Part.createFormData("image",  File(path).name, fbody)
        val method1 = RequestBody.create(MediaType.parse("text/plain"), "UpdateProfile")
        val user_id = RequestBody.create(MediaType.parse("text/plain"), userid)
        val first = RequestBody.create(MediaType.parse("text/plain"), firstname)
        val l_name = RequestBody.create(MediaType.parse("text/plain"), lastname)
        val email_id = RequestBody.create(MediaType.parse("text/plain"), email)

        return api.UpdateProfile(method1,user_id,first,l_name,email_id,body)
    }



    fun ContactUs(userid: String, email: String, subject : String, message : String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.ContactUs("ContactUs",userid, email, subject, message)
    }

    fun Logout(userid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.Logout("Logout",userid)
    }
    fun ChangePassword(userid: String, oldpassword: String, newpassword : String, confirmpassword : String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.ChangePassword("ChangePassword",userid,oldpassword,newpassword,confirmpassword)
    }


    fun UpdateProfileWithoutImage(userid: String,firstname:String,lastname:String,email:String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.UpdateProfileWithoutImage("UpdateProfile",userid,firstname,lastname,email)
    }

    fun GetAllProviders(userid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.GetAllProviders("ListProvider",userid)
    }

    fun GetProviderProduct(userid: String, providerid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.GetProviderProduct("GetProviderProduct",userid, providerid)
    }

    fun DeleteProduct(userid: String,productyid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.DeleteProduct("DeleteProduct",userid,productyid)
    }
    fun UpdateLatLong(userid: String,latitude: Double,longitude: Double): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.UpdateLatLong("UpdateLatLong",userid, latitude, longitude)
    }

    fun AddToCart(userid: String, providerid: String, productid: String, quantity: String, productprice: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.AddToCart("AddToCart",userid, providerid, productid, quantity, productprice)
    }

    fun GetCartList(userid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.GetCartList("GetCartList",userid)
    }

    fun ClearCart(userid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.ClearCart("ClearCart",userid)
    }


    fun RemoveCartItems(userid: String, productid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.RemoveCartItems("RemoveCartItems",userid,productid)
    }

    fun UpdateCartQuantity(userid: String, productid: String,productprice: String,quantity: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.UpdateCartQuantity("UpdateCartQuantity",userid,productprice,productid,quantity)
    }

    fun PlaceOrder(userid: String, providerid: String,latitude: String,longitude: String,address:String,note:String,paymentstatus:String,city:String,state:String,zip:String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.PlaceOrder("PlaceOrder",userid,providerid,latitude,longitude,address,note,paymentstatus,city,state,zip)
    }


    fun OrderHistory(userID: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.OrderHistory("OrderHistory",userID)
    }

    fun CancelOrder(userID: String,order_id:String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.CancelOrder("CancelOrder",userID,order_id)
    }



    fun UploadIdProof(
        context: Context,
        userid: String,
        image: Uri?,
        id_type: String
    ): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        val imageCompressor = ImageCompressor()
        val path = imageCompressor.compressImage(context, image.toString())
        val fbody = RequestBody.create(MediaType.parse("multipart/form-data"), File(path))
        val body = MultipartBody.Part.createFormData("image",  File(path).name, fbody)
        val method1 = RequestBody.create(MediaType.parse("text/plain"), "UploadIdProof")
        val user_id = RequestBody.create(MediaType.parse("text/plain"), userid)
        val idtype = RequestBody.create(MediaType.parse("text/plain"), id_type)
        return api.UpoadIdProof(method1,user_id,body,idtype)
    }

    fun makepayment(userID: String,token:String,orederID:String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.makepayment("MakePayment",userID,token,orederID)
    }
}
