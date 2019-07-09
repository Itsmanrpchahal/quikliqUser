package com.quikliq.quikliquser.activities

import android.annotation.SuppressLint
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
import com.quikliq.quikliquser.adapters.HistoryItemAdapter
import com.quikliq.quikliquser.interfaces.CancelOrder
import com.quikliq.quikliquser.models.HistoryModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class OrderSummaryActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var historyModel: HistoryModel? = null
    private var historyItemAdapter: HistoryItemAdapter? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title!!.text = "Order Summary"
        utility = Utility()
        pd = ProgressDialog(this@OrderSummaryActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        historyModel = intent.extras!!.get("order") as HistoryModel
        providernameTV.text = historyModel!!.providername
        statusTV.text = "This order with "+historyModel!!.providername+" "+ when {
            historyModel!!.order_status!! == "0" -> " is Pending"
            historyModel!!.order_status!! == "1" -> " is Preparing"
            historyModel!!.order_status!! == "2" -> " is Ready to Delivery"
            historyModel!!.order_status!! == "3" -> " was Delivered"
            historyModel!!.order_status!! == "4" -> " is Reject"
            historyModel!!.order_status!! == "5" -> " is Canceled by User"
            else -> ""
        }
        totalpriceTV.text = "$"+historyModel!!.totalprice
        ordernumTV.text = historyModel!!.orderid
        ordertimeTV.text = historyModel!!.datetime
        historyItemAdapter = HistoryItemAdapter( historyModel!!.products,historyModel!!.quantity,historyModel!!.price)
        val mLayoutManager = LinearLayoutManager(this@OrderSummaryActivity.applicationContext)
        itemsRV!!.layoutManager = mLayoutManager
        itemsRV!!.adapter = historyItemAdapter
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }



}
