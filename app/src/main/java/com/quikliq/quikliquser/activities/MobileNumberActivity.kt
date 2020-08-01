package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity__mobile_number.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MobileNumberActivity : AppCompatActivity(),View.OnClickListener,
    CountryCodePicker.OnCountryChangeListener {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private lateinit var ccp : CountryCodePicker
    private var countryCode:String?="91"
    private var countryName:String?=null
    private var nointernet: RelativeLayout? = null
    private var screendata: RelativeLayout? = null
    var notC = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__mobile_number)
        utility = Utility()
        ccp = findViewById(R.id.ccp)
        nointernet = findViewById(R.id.nointernet)
        screendata = findViewById(R.id.screendata)

        ccp!!.setOnCountryChangeListener(this)

        //to set default country code as India

        ccp!!.setDefaultCountryUsingNameCode("IN")
        pd = ProgressDialog(this@MobileNumberActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        nextScreen.setOnClickListener(this)
    }

    //Check Internet Connection
    private var broadcastReceiver : BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val notConnected = p1!!.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false)

            if (notConnected)
            {
                nointernet?.visibility = View.VISIBLE
                screendata?.visibility = View.GONE
                notC = "1"
            }else{
                nointernet?.visibility = View.GONE
                screendata?.visibility = View.VISIBLE
                notC = "0"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(notC.equals("1"))
        {
            finishAffinity()
        }
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.nextScreen -> checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            mobile_ET.text.isEmpty() -> {
                mobile_ET.requestFocus()
                mobile_ET.error = getString(R.string.txt_Error_required)
            }

            else -> otpApiCall(mobile_ET.text.toString())
        }
    }


    private fun otpApiCall(mobile_number: String) {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this@MobileNumberActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.mobile("NewOtp",mobile_number,countryCode).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("response",response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                startActivity(Intent(this@MobileNumberActivity, OtpActivity::class.java).putExtra("otp",jsonObject.optString("data")).putExtra("mobile",mobile_number))
                            }else{ utility!!.relative_snackbar(parent_mobile!!, jsonObject.optString("message"), getString(R.string.close_up)) }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

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
        } else {
            utility!!.relative_snackbar(
                parent_mobile!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun hideKeyboard() {
        val imm = this@MobileNumberActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    override fun onCountrySelected() {
        countryCode=ccp.selectedCountryCode
        countryName=ccp.selectedCountryName
    }

}
