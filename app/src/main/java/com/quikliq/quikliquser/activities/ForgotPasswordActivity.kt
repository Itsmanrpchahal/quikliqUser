package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.utilities.Utility
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard
import kotlinx.android.synthetic.main.activity__forgot_password.*
import kotlinx.android.synthetic.main.activity__mobile_number.*
import kotlinx.android.synthetic.main.activity__mobile_number.nextScreen
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ForgotPasswordActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener,
    View.OnClickListener {

    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private lateinit var ccp : CountryCodePicker
    private var countryCode:String = "91"
    private var countryName:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__forgot_password)
        utility = Utility()
        ccp = findViewById(R.id.ccp)
        ccp!!.setOnCountryChangeListener(this)

        //to set default country code as India

        ccp!!.setDefaultCountryUsingNameCode("IN")

        pd = ProgressDialog(this)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        nextScreen.setOnClickListener(this)
    }

    override fun onCountrySelected() {
        countryCode=ccp.selectedCountryCode
        countryName=ccp.selectedCountryName
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.nextScreen -> checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            forgot_Email_ET.text.isEmpty() -> {
                forgot_Email_ET.requestFocus()
                forgot_Email_ET.error = getString(R.string.txt_Error_required)
            }

            else -> forgetApiCall(forgot_Email_ET.text.toString(),countryCode)
        }
    }

    private fun forgetApiCall(toString: String, countryCode: String) {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.ForgetPassword("ForgetPassword",toString,countryCode,"2").enqueue(object : retrofit2.Callback<JsonObject>
            {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("response", response.body().toString())
                        val responsedata = response.body().toString()
                        val jsonObject = JSONObject(responsedata)
                       val status = jsonObject.optBoolean("status")
                        if (jsonObject.optBoolean("status")) {
                            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))

                            Toast.makeText(this@ForgotPasswordActivity,jsonObject.optString("message"),Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@ForgotPasswordActivity,jsonObject.optString("message"),Toast.LENGTH_LONG).show() }
                    }else{
                        utility!!.relative_snackbar(
                            parent_mobile!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_mobile!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })

        }else {
            utility!!.relative_snackbar(
                parent_mobile!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}
