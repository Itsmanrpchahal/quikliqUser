package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.adapters.HistoryAdapter
import com.quikliq.quikliquser.interfaces.CancelOrder
import com.quikliq.quikliquser.models.HistoryModel
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_order_history2.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.quikliq.quikliquser.utilities.Prefs

class OrderHistory : AppCompatActivity(), CancelOrder {
    private var toolbar: Toolbar? = null
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var historyModelArraylist: ArrayList<HistoryModel>?= null
    private var products: ArrayList<String>? = null
    private var price: ArrayList<String>? = null
    private var quantity: ArrayList<String>? = null
    private var items: ArrayList<String>? = null
    private var historyAdapter:HistoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history2)
        cancelOrder = this
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title!!.text = "Order History"
        utility = Utility()
        pd = ProgressDialog(this@OrderHistory)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        orderHistoryApiCall()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }



    private fun orderHistoryApiCall() {
        if (utility!!.isConnectingToInternet(this@OrderHistory)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.OrderHistory(Prefs.getString("userid", "")).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                historyModelArraylist = ArrayList()
                                for (i in 0 until jsonObject.optJSONArray("data").length()) {
                                    val jsonObject1 = jsonObject.optJSONArray("data").optJSONObject(i)
                                    val historyModel = HistoryModel()
                                    historyModel.orderid = jsonObject1.optString("orderid")
                                    historyModel.providername = jsonObject1.optString("providername")
                                    historyModel.totalprice = jsonObject1.optString("totalprice")
                                    historyModel.image = jsonObject1.optString("image")
                                    historyModel.order_status = jsonObject1.optString("status")
                                    historyModel.datetime = jsonObject1.optString("datetime")

                                    products = ArrayList()
                                    for (j in 0 until jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("products").length()){
                                        products!!.add(jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("products").optString(j))
                                    }
                                    historyModel.products = products

                                    price = ArrayList()
                                    for (k in 0 until jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("price").length()){
                                        price!!.add(jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("price").optString(k))
                                    }
                                    historyModel.price = price

                                    quantity = ArrayList()
                                    for (l in 0 until jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("quantity").length()){
                                        quantity!!.add(jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("quantity").optString(l))
                                    }
                                    historyModel.quantity = quantity

                                    items = ArrayList()
                                    for (z in 0 until jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("products").length()){
                                        items!!.add(jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("quantity").optString(z)+" × "+jsonObject.optJSONArray("data").optJSONObject(i).optJSONArray("products").optString(z))
                                    }
                                    historyModel.items = items

                                    historyModelArraylist!!.add(historyModel)
                                }

                                if(historyModelArraylist!!.isNotEmpty()){
                                    historyAdapter = HistoryAdapter(this@OrderHistory, historyModelArraylist!!)
                                    val mLayoutManager = LinearLayoutManager(this@OrderHistory.applicationContext)
                                    historyRV!!.layoutManager = mLayoutManager
                                    historyRV!!.adapter = historyAdapter
                                }

                            } else {
                                utility!!.linear_snackbar(
                                    parent_order_history!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }else{
                        utility!!.linear_snackbar(
                            parent_order_history!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_order_history!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_order_history!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun orderHistoryApiCall(order_id:String) {
        if (utility!!.isConnectingToInternet(this@OrderHistory)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.CancelOrder(Prefs.getString("userid", ""),order_id).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                finish()
                                overridePendingTransition(0, 0)
                                startActivity(intent)
                                overridePendingTransition(0, 0)
                            } else {
                                utility!!.linear_snackbar(
                                    parent_order_history!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }else{
                        utility!!.linear_snackbar(
                            parent_order_history!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_order_history!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_order_history!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    companion object{
        var cancelOrder:CancelOrder?= null
    }

    override fun cancel(order_id: String) {
        orderHistoryApiCall(order_id)
    }
}
