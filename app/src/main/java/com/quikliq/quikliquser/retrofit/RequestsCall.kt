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
import java.io.File
import java.util.ArrayList

class RequestsCall {


    fun login(mobile: String, password: String, device_type : String, device_token : String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.login("UserLogin",mobile, password, device_type, device_token)
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

    fun saveAdditionalDetail(devicetype: String, firstname: String, lastname : String, mobile : String, email : String, password : String, businessname : String, bankname : String, accountnumber : String, ifsc : String, address : String, usertype : String, devicetoken : String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.saveAdditionalDetail("SaveAdditionalDetail",devicetype, firstname, lastname, mobile, email, password, businessname, bankname, accountnumber, ifsc, address,usertype,devicetoken)
    }

    fun profile(userID: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.profile("UserProfile",userID)
    }

    fun AddProduct(
        context: Context,
        userid: String,
        category: String,
        productname: String,
        price: String,
        quantity: String,
        description: String,
        image: Uri?
    ): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        val imageCompressor = ImageCompressor()
        val path = imageCompressor.compressImage(context, image.toString())
        val fbody = RequestBody.create(MediaType.parse("multipart/form-data"), File(path))
        val body = MultipartBody.Part.createFormData("image",  File(path).name, fbody)
        val method1 = RequestBody.create(MediaType.parse("text/plain"), "AddProduct")
        val user_id = RequestBody.create(MediaType.parse("text/plain"), userid)
        val cat = RequestBody.create(MediaType.parse("text/plain"), category)
        val product_name = RequestBody.create(MediaType.parse("text/plain"), productname)
        val cost = RequestBody.create(MediaType.parse("text/plain"), price)
        val qty = RequestBody.create(MediaType.parse("text/plain"), quantity)
        val desc = RequestBody.create(MediaType.parse("text/plain"), description)

        return api.AddProduct(method1,user_id,cat,product_name,cost,qty,desc,body)
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

    fun GetAllProduct(userid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.GetAllProduct("GetAllProduct",userid)
    }

    fun ProductDetail(userid: String, productid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.ProductDetail("ProductDetail",userid, productid)
    }

    fun DeleteProduct(userid: String,productyid: String): Call<JsonObject> {
        val apiCall = ApiCall()
        val api = apiCall.apiCall().create(ApiHelper::class.java)
        return api.DeleteProduct("DeleteProduct",userid,productyid)
    }

}
