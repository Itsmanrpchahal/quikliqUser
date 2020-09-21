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
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.interfaces.PaymentTokenInterface
import com.quikliq.quikliquser.payment.DependencyHandler
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import com.stripe.android.PaymentConfiguration
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.content_checkout.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckoutActivityKotlin : AppCompatActivity(), PaymentTokenInterface {
    private var utility: Utility? = null
    private var mDependencyHandler: DependencyHandler? = null
    private var toolbar: Toolbar? = null
    private var orderId:String =""
    private var count:Int= 0
    var pd: ProgressDialog? = null
    private var nointernet: RelativeLayout? = null
    private var screendata: LinearLayout? = null
    var notC = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        utility = Utility()
        pd = ProgressDialog(this)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)

        toolbar = findViewById(R.id.toolbar)
        nointernet = findViewById(R.id.nointernet)
        screendata = findViewById(R.id.screendata)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title!!.text = "Final Order Payment"

        mDependencyHandler = DependencyHandler(
            this,
            findViewById(R.id.cardInputWidget),
            findViewById(R.id.listview)
        )
        payButton.setOnClickListener {
            count++
            if(count == 2) {
                pd!!.show()
                pd!!.setContentView(R.layout.loading)
            }
            mDependencyHandler!!.attachIntentServiceTokenController(this, payButton)
        }
        orderId = intent.getStringExtra("orderid")
        orderIdTV.text = "Order Id : " + intent.getStringExtra("orderid")
        priceTV.text = "Total Price : " + intent.getStringExtra("total_price")
        paymentTokenInterface = this
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

    override fun onDestroy() {
        super.onDestroy()
        mDependencyHandler!!.clearReferences()
    }

    companion object {
        var paymentTokenInterface: PaymentTokenInterface? = null
    }

    override fun PaymentToken(payment_token: String) {
        makePaymentApiCall(payment_token, orderId)
    }

    private fun makePaymentApiCall(token: String, orderID: String) {
        if (utility!!.isConnectingToInternet(this)) {
            val requestsCall = RequestsCall()
            requestsCall.makepayment(Prefs.getString("userid", ""), token, orderID).enqueue(object :
                Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {

                                startActivity(
                                    Intent(
                                        this@CheckoutActivityKotlin,
                                        OrderHistory::class.java
                                    ).putExtra("order_success", true)
                                )
                                    finish()
                            } else {
                                utility!!.linear_snackbar(
                                    findViewById(android.R.id.content)!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        utility!!.linear_snackbar(
                            findViewById(android.R.id.content),
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        findViewById(android.R.id.content)!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                findViewById(android.R.id.content)!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }
}
