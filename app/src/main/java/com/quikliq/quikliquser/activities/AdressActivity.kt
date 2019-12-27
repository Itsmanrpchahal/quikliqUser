package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.constants.Constant
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_adress.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdressActivity : AppCompatActivity(), View.OnClickListener {
    private var utility: Utility? = null
    private var progressDialog: ProgressDialog? = null
    private var phone_number: String? = null
    private var first_name: String? = null
    private var last_name : String? = null
    private var registerEmail : String? = null
    private var address : String? = null
    private var password_ET : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adress)
        utility = Utility()
        progressDialog = ProgressDialog(this@AdressActivity)
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        progressDialog!!.isIndeterminate = true
        progressDialog!!.setCancelable(false)
        first_name = intent.getStringExtra("FirstName")
        last_name = intent.getStringExtra("LastName")
        password_ET = intent.getStringExtra("password_ET")
        registerEmail = intent.getStringExtra("Email")
        phone_number = intent.getStringExtra("phone_number")
        address = intent.getStringExtra("address")
        nextScreen.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.nextScreen -> checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            cityName.text.isEmpty() -> {
                cityName.requestFocus()
                cityName.error = getString(R.string.txt_Error_required)
            }
            stateName.text.isEmpty() -> {
                stateName.requestFocus()
                stateName.error = getString(R.string.txt_Error_required)
            }
            zipcode.text.isEmpty() -> {
                zipcode.requestFocus()
                zipcode.error = getString(R.string.txt_Error_required)
            }
            else -> saveAdditonalDetailApiCall()
        }
    }

    private fun saveAdditonalDetailApiCall() {
        if (utility!!.isConnectingToInternet(this@AdressActivity)) {
            progressDialog!!.show()
            progressDialog!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.saveAdditionalDetail(
                "1",
                first_name!!,
                last_name!!,
                phone_number!!,
                registerEmail!!,
                password_ET!!,
                address!!,
                "2",
                Prefs.getString(Constant.FCM_TOKEN, ""),
                cityName.text.toString(),
                stateName.text.toString(),
                zipcode.text.toString()
            ).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressDialog!!.dismiss()
                    if (response.isSuccessful) {
                        val responsedata = response.body().toString()
                        Log.d("response", response.body().toString())
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                Prefs.putString("userid", jsonObject.optJSONObject("data").optString("userid"))
                                Prefs.putString("FirstName", jsonObject.optJSONObject("data").optString("FirstName"))
                                Prefs.putInt("IsAgeVerify",jsonObject.optJSONObject("data").optInt("IsAgeVerify"))
                                Prefs.putString("LastName", jsonObject.optJSONObject("data").optString("LastName"))
                                Prefs.putString("Mobile", jsonObject.optJSONObject("data").optString("Mobile"))
                                Prefs.putString("Email", jsonObject.optJSONObject("data").optString("Email"))
                                Prefs.putString("Address", jsonObject.optJSONObject("data").optString("Address"))
                                Prefs.putString("UserType", jsonObject.optJSONObject("data").optString("UserType"))
                                Prefs.putString(
                                    "profileimage",
                                    jsonObject.optJSONObject("data").optString("profileimage")
                                )
                                Prefs.putBoolean(Constant.IS_LOGGED_IN, true)
                                startActivity(
                                    Intent(this@AdressActivity, HomeActivity::class.java)
                                )
                                finish()
                            } else {
                                if(progressDialog!!.isShowing){
                                    progressDialog!!.dismiss()
                                }
                                utility!!.relative_snackbar(
                                    parent_payment!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        if(progressDialog!!.isShowing){
                            progressDialog!!.dismiss()
                        }
                        utility!!.relative_snackbar(
                            parent_payment!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressDialog!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_payment!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_payment!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }
    private fun hideKeyboard() {
        val imm = this@AdressActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}
