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
import kotlinx.android.synthetic.main.activity__register.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var otp: String? = null
    private var phone_number: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__register)
        utility = Utility()
        pd = ProgressDialog(this@RegisterActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        otp = intent.getStringExtra("otp")
        phone_number = intent.getStringExtra("mobile")
        signUp_TV.setOnClickListener(this)
        nextScreen.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.signUp_TV -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.nextScreen -> checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            firstName.text.isEmpty() -> {
                firstName.requestFocus()
                firstName.error = getString(R.string.txt_Error_required)
            }
            lastName.text.isEmpty() -> {
                lastName.requestFocus()
                lastName.error = getString(R.string.txt_Error_required)
            }
            !utility!!.isValidEmail(registerEmail!!.text.toString().trim { it <= ' ' }) -> {
                registerEmail.requestFocus()
                registerEmail.error = getString(R.string.Invalid_email)
            }
            address.text.isEmpty() -> {
                address.requestFocus()
                address.error = getString(R.string.txt_Error_required)
            }
            password_ET.text.length < 6 -> {
                password_ET.requestFocus()
                password_ET.error = getString(R.string.error_password)
            }
            else -> signupApiCall(
                firstName.text.toString(),
                lastName.text.toString(),
                registerEmail!!.text.toString(),
                password_ET.text.toString()
            )
        }
    }

    private fun signupApiCall(
        firstName: String,
        lastName: String,
        registerEmail: String,
        password_ET: String
    ) {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this@RegisterActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.signup(firstName, lastName, registerEmail, password_ET).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                saveAdditonalDetailApiCall(firstName, lastName, registerEmail, password_ET)
                            } else {
                                utility!!.relative_snackbar(
                                    parent_signup!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        pd!!.dismiss()
                        utility!!.relative_snackbar(
                            parent_signup!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_signup!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_signup!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun hideKeyboard() {
        val imm = this@RegisterActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    private fun saveAdditonalDetailApiCall(
        firstName: String,
        lastName: String,
        registerEmail: String,
        password: String
    ) {
        if (utility!!.isConnectingToInternet(this@RegisterActivity)) {
            val requestsCall = RequestsCall()
            requestsCall.saveAdditionalDetail(
                "1",
                firstName,
                lastName,
                phone_number!!,
                registerEmail,
                password,
                address.text.toString(),
                "2",
                 Prefs.getString(Constant.FCM_TOKEN, "")
            ).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        val responsedata = response.body().toString()
                        Log.d("response", response.body().toString())
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                Prefs.putString("userid", jsonObject.optJSONObject("data").optString("userid"))
                                Prefs.putString("FirstName", jsonObject.optJSONObject("data").optString("FirstName"))
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
                                    Intent(this@RegisterActivity, HomeActivity::class.java)
                                )
                                finish()
                            } else {
                                utility!!.relative_snackbar(
                                    parent_signup!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        utility!!.relative_snackbar(
                            parent_signup!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_signup!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_signup!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }
}
