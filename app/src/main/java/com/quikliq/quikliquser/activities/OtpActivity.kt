package com.quikliq.quikliquser.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity__otp.*

class OtpActivity : AppCompatActivity(), View.OnClickListener {
    private var utility: Utility? = null
    private var otp: String? = null
    private var phone_number: String? = null
    private var nointernet: RelativeLayout? = null
    private var screendata: RelativeLayout? = null
    var notC = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__otp)
        nointernet = findViewById(R.id.nointernet)
        screendata = findViewById(R.id.screendata)
        utility = Utility()
        utility!!.relative_snackbar(
            parent_otp!!,
            "OTP sent to " + intent.getStringExtra("mobile"),
            getString(R.string.close_up)
        )
        otp = intent.getStringExtra("otp")
        phone_number = intent.getStringExtra("mobile")
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
        hideKeyboard()
        when {
            otp_ET.text.isEmpty() -> {
                otp_ET.requestFocus()
                otp_ET.error = getString(R.string.txt_Error_required)
            }
            else -> when (otp_ET.text.toString()) {
                otp -> startActivity(Intent(this@OtpActivity, RegisterActivity::class.java).putExtra("mobile",phone_number).putExtra("otp",otp))
                else ->  utility!!.relative_snackbar(parent_otp!!, "Wrong OTP", getString(R.string.close_up))
            }
        }
    }


    private fun hideKeyboard() {
        val imm = this@OtpActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

}
