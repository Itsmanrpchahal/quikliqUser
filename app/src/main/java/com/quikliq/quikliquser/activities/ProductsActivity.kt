package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.adapters.ProductsAdapter
import com.quikliq.quikliquser.dataProcessing.OrderProcessing
import com.quikliq.quikliquser.interfaces.ShowCart
import com.quikliq.quikliquser.interfaces.UpdateItemPrice
import com.quikliq.quikliquser.models.ProductsModel
import com.quikliq.quikliquser.models.ProviderModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ProductsActivity : AppCompatActivity(), ShowCart, View.OnClickListener, UpdateItemPrice {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var toolbar: Toolbar? = null
    private var productsList: ArrayList<ProductsModel>? = null
    private var productsAdapter: ProductsAdapter? = null
    private var cartRL: RelativeLayout? = null
    private var itemsTV: TextView? = null
    private var priceTV: TextView? = null
    private var order = java.util.ArrayList<OrderProcessing>()
    private var providerModel: ProviderModel? = null
    private var no_dataRL: RelativeLayout? = null
    private var titleTV: TextView? = null
    private var msgTV: TextView? = null
    private var count: Int = 0
    private var nointernet: RelativeLayout? = null
    private var screendata: RelativeLayout? = null
    var notC = "0"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        toolbar = findViewById(R.id.toolbar)
        nointernet = findViewById(R.id.nointernet)
        screendata = findViewById(R.id.screendata)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        showcart = this
        updateItemPrice = this
        utility = Utility()
        itemsTV = findViewById(R.id.itemsTV)
        priceTV = findViewById(R.id.priceTV)
        pd = ProgressDialog(this@ProductsActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        no_dataRL = findViewById(R.id.no_dataRL)
        titleTV = findViewById(R.id.titleTV)
        msgTV = findViewById(R.id.msgTV)
        cartRL = findViewById(R.id.cartRL)
        providerModel = intent.extras!!.get("provider") as ProviderModel
        utility!!.loadImageWithLoader(this@ProductsActivity, providerModel!!.picture, providerIV!!)
        providerNameTV.text = providerModel!!.name
        toolbar_title!!.text = providerModel!!.name
        distance_TV.text = providerModel!!.distance
        if (providerModel!!.Status == 1) {
            status_tv.setTextColor(ContextCompat.getColor(this@ProductsActivity, R.color.green_open))
            status_tv.text = "OPEN"
        } else {
            status_tv.setTextColor(ContextCompat.getColor(this@ProductsActivity, R.color.red_close))
            status_tv.text = "CLOSED"
        }
        time_TV.text = providerModel!!.opentime + " - " + providerModel!!.closetime

        cartRL!!.setOnClickListener(this)
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
                providersApiCall(providerModel!!.provider_id!!)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(notC.equals("1"))
        {
            finishAffinity()
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


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cartRL -> {
                order.clear()
                for (key in menuData!!.keys) {
                    println("value:$key")
                    System.out.println("value:" + menuData!![key])
                    order.add(menuData!![key]!!)
                }
                for (i in 0 until order.size) {
                    addCartApiCall(providerModel!!.provider_id!!, order[i].ItemID, order[i].ItemCount, order[i].Price)
                }
            }
        }
    }

    private fun providersApiCall(provider_id: String) {
        if (utility!!.isConnectingToInternet(this@ProductsActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.GetProviderProduct(Prefs.getString("userid", ""), provider_id)
                .enqueue(object : Callback<JsonObject> {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        pd!!.dismiss()
                        if (response.isSuccessful) {
                            Log.d("responsedata", response.body().toString())
                            val responsedata = response.body().toString()
                            try {
                                val jsonObject = JSONObject(responsedata)

                                if (jsonObject.optBoolean("status")) {
                                    productsList = ArrayList()
                                    val data_arry = jsonObject.optJSONObject("data").optJSONArray("Allproducts")
                                    for (i in 0 until data_arry.length()) {
                                        val jsonObject1 = data_arry.optJSONObject(i)
                                        val productsModel = ProductsModel()
                                        productsModel.id = jsonObject1.optString("id")
                                        productsModel.userid = jsonObject1.optString("userid")
                                        productsModel.category = jsonObject1.optString("category")
                                        productsModel.product_name = jsonObject1.optString("product_name")
                                        productsModel.description = jsonObject1.optString("description")
                                        productsModel.price = jsonObject1.optString("price")
                                        productsModel.quantity = jsonObject1.optString("adress")
                                        productsModel.image = jsonObject1.optString("image")
                                        productsModel.isactive = jsonObject1.optString("isactive")
                                        productsModel.business_name = jsonObject1.optString("business_name")
                                        productsModel.business_address = jsonObject1.optString("business_address")
                                        productsList!!.add(productsModel)
                                    }
                                    if (productsList!!.isNotEmpty()) {
                                        Log.d("Size products", "" + productsList!!.size)
                                        productsAdapter = ProductsAdapter(this@ProductsActivity, productsList!!)
                                        val mLayoutManager =
                                            LinearLayoutManager(this@ProductsActivity.applicationContext)
                                        providersRV!!.layoutManager = mLayoutManager
                                        providersRV!!.adapter = productsAdapter
                                    }


                                } else {
                                    providersRV.visibility = View.GONE
                                    no_dataRL!!.visibility = View.VISIBLE
                                    titleTV!!.text = "Products Not Available"
                                    msgTV!!.text = "Provider Did Not Add Products Yet"
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        } else {
                            utility!!.relative_snackbar(
                                parent_products!!,
                                response.message(),
                                getString(R.string.close_up)
                            )
                        }

                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        pd!!.dismiss()
                        utility!!.relative_snackbar(
                            parent_products!!,
                            getString(R.string.no_internet_connectivity),
                            getString(R.string.close_up)
                        )
                    }
                })
        } else {
            utility!!.relative_snackbar(
                parent_products!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    companion object {
        var menuData: MutableMap<String, OrderProcessing>? = HashMap<String, OrderProcessing>()
        var showcart: ShowCart? = null
        var updateItemPrice: UpdateItemPrice? = null
    }

    override fun show(item: Int, price: String) {
        if (item == 1) {
            itemsTV!!.text = "$item item"
        } else {
            itemsTV!!.text = "$item items"
        }
        priceTV!!.text = "$$price"
    }

    override fun show(i: Boolean) {
        if (i) {
            cartRL!!.visibility = View.VISIBLE
        } else {
            cartRL!!.visibility = View.GONE
        }
    }

    private fun addCartApiCall(providerid: String, productid: String, quantity: String, productprice: String) {
        if (utility!!.isConnectingToInternet(this@ProductsActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.AddToCart(Prefs.getString("userid", ""), providerid, productid, quantity, productprice)
                .enqueue(object :
                    Callback<JsonObject> {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            Log.d("responsedata", response.body().toString())
                            val responsedata = response.body().toString()
                            try {
                                val jsonObject = JSONObject(responsedata)
                                if (jsonObject.optBoolean("status")) {

                                    count = count + 1
                                    if (order.size == count) {
                                        pd!!.dismiss()
                                        utility!!.relative_snackbar(
                                            parent_products!!,
                                            "Added to Cart Successfully",
                                            getString(R.string.close_up)
                                        )
                                        startActivity(Intent(this@ProductsActivity, CartActivity::class.java))
                                    }
                                } else {
                                    pd!!.dismiss()

                                    if (jsonObject.has("isnextapi")) {
                                        if (jsonObject.optBoolean("isnextapi")) {
                                            clearCartDialog()
                                        }
                                    } else {
                                       utility!!.relative_snackbar(findViewById(android.R.id.content),
                                            jsonObject.optString("message"),
                                            getString(R.string.close_up)
                                        )
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        pd!!.dismiss()
                        utility!!.relative_snackbar(
                            parent_products!!,
                            getString(R.string.no_internet_connectivity),
                            getString(R.string.close_up)
                        )
                    }
                })
        } else {
            utility!!.relative_snackbar(
                parent_products!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }


    private fun clearCartDialog() {
        val builder = AlertDialog.Builder(this@ProductsActivity)

        builder.setTitle("Cart Contains Other Provider Products")
        builder.setMessage("For Order Product From Other Provider You have to Clear Cart. Do You Want To Clear")
        builder.setPositiveButton("YES") { _, _ ->
            clearCartApiCall()
        }
        builder.setNegativeButton("No") { _, _ ->
            // dialog.dismiss()
        }
        // Display a neutral button on alert dialog
//        builder.setNeutralButton("Cancel"){_,_ ->
//            Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
//        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun clearCartApiCall() {
        if (utility!!.isConnectingToInternet(this@ProductsActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.ClearCart(Prefs.getString("userid", "")).enqueue(object :
                Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {

                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)

                            if (jsonObject.optBoolean("status")) {
                                cartRL!!.performClick()
                            } else {
                                pd!!.dismiss()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {

                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_products!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_products!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if(menuData!!.isEmpty()){
            cartRL!!.visibility = View.GONE
        }else{
            cartRL!!.visibility = View.VISIBLE
        }
    }
}
