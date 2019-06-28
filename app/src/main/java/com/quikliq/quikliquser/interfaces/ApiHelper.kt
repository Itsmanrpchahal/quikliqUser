package com.quikliq.quikliquser.interfaces

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiHelper {

    @FormUrlEncoded
    @POST(".")
    fun login(@Field("method") UserLogin: String ,@Field("mobile") mobile: String, @Field("password") password: String, @Field("devicetype") devicetype: String, @Field("devicetoken") devicetoken: String,@Field("usetype") usertype: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun signup(@Field("method") Signup: String ,@Field("fname") fname: String, @Field("lname") lname: String, @Field("email") email: String, @Field("password") password: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun mobile(@Field("method") NewOtp: String ,@Field("mobile") mobile: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun saveAdditionalDetail(@Field("method") SaveAdditionalDetail: String ,@Field("devicetype") devicetype: String, @Field("firstname") firstname: String, @Field("lastname") lastname: String, @Field("mobile") mobile: String, @Field("email") email: String, @Field("password") password: String, @Field("address") address: String, @Field("usertype") usertype: String, @Field("devicetoken") devicetoken: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun profile(@Field("method") UserLogin: String ,@Field("userid") userid: String): Call<JsonObject>

    @Multipart
    @POST(".")
    fun AddProduct(@Part("method") AddProduct: RequestBody, @Part("userid") userid: RequestBody, @Part("category") category: RequestBody, @Part("productname") productname: RequestBody, @Part("price") price: RequestBody, @Part("quantity") quantity: RequestBody, @Part("description") description: RequestBody, @Part files: MultipartBody.Part): Call<JsonObject>

    @Multipart
    @POST(".")
    fun UpdateProfile(@Part("method") UpdateProfile: RequestBody ,@Part("userid") userid: RequestBody ,@Part("firstname") firstname: RequestBody ,@Part("lastname") lastname: RequestBody ,@Part("email") email: RequestBody, @Part files: MultipartBody.Part): Call<JsonObject>


    @FormUrlEncoded
    @POST(".")
    fun UpdateProfileWithoutImage(@Field("method") UpdateProfile: String ,@Field("userid") userid: String ,@Field("firstname") firstname: String ,@Field("lastname") lastname: String ,@Field("email") email: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun ContactUs(@Field("method") ContactUs: String ,@Field("userid") userid: String, @Field("email") email: String, @Field("subject") subject: String, @Field("message") message: String): Call<JsonObject>


    @FormUrlEncoded
    @POST(".")
    fun Logout(@Field("method") Logout: String ,@Field("userid") userid: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun ChangePassword(@Field("method") ChangePassword: String ,@Field("userid") userid: String,@Field("oldpassword") oldpassword: String,@Field("newpassword") newpassword: String,@Field("confirmpassword") confirmpassword: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun GetAllProviders(@Field("method") GetAllProduct: String ,@Field("userid") userid: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun GetProviderProduct(@Field("method") GetProviderProduct: String ,@Field("userid") userid: String, @Field("providerid") providerid: String): Call<JsonObject>


    @FormUrlEncoded
    @POST(".")
    fun DeleteProduct(@Field("method") DeleteProduct: String ,@Field("userid") userid: String, @Field("productyid") productyid: String): Call<JsonObject>

    @FormUrlEncoded
    @POST(".")
    fun UpdateLatLong(@Field("method") UpdateLatLong: String ,@Field("userid") userid: String, @Field("latitude") latitude: Double, @Field("longitude") longitude: Double): Call<JsonObject>


}
