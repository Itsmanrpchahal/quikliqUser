package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    override fun show(item: Int, price: String) {
        if(item == 1){
            itemsTV!!.text = ""+item+" item"
        }else{
            itemsTV!!.text = ""+item+" items"
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

    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var toolbar: Toolbar? = null
    private var productsList: ArrayList<ProductsModel>? = null
    private var productsAdapter: ProductsAdapter? = null
    private var cartRL: RelativeLayout? = null
    private var itemsTV: TextView? = null
    private var priceTV: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        toolbar = findViewById(R.id.toolbar)
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
        cartRL = findViewById(R.id.cartRL)
        val providerModel = intent.extras!!.get("provider") as ProviderModel
        utility!!.loadImageWithLoader(this@ProductsActivity, providerModel.picture, providerIV!!)
        providerNameTV.text = providerModel.name
        toolbar_title!!.text = providerModel.name
        distance_TV.text = providerModel.distance
        if (providerModel.Status == 1) {
            status_tv.setTextColor(ContextCompat.getColor(this@ProductsActivity, R.color.green_open))
            status_tv.text = "OPEN"
        } else {
            status_tv.setTextColor(ContextCompat.getColor(this@ProductsActivity, R.color.red_close))
            status_tv.text = "CLOSED"
        }
        time_TV.text = providerModel.opentime + " - " + providerModel.closetime
        providersApiCall(providerModel.provider_id!!)
        cartRL!!.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cartRL -> {
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
                                    } else {
//                                    no_dataRL!!.visibility = View.VISIBLE

                                    }


                                } else {
                                    utility!!.relative_snackbar(
                                        parent_products!!,
                                        jsonObject.optString("message"),
                                        getString(R.string.close_up)
                                    )
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

}
