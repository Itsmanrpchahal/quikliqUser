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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.adapters.CartListAdapter
import com.quikliq.quikliquser.models.CartProductModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartActivity : AppCompatActivity() {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var toolbar: Toolbar? = null
    private var parent_cart: RelativeLayout? = null
    private var toolbar_title: TextView? = null
    private var cartListAdapter: CartListAdapter? = null
    private var cartproductList: ArrayList<CartProductModel>? = null
    private var cartRV: RecyclerView?= null
    private var no_dataRL: RelativeLayout? = null
    private var titleTV: TextView? = null
    private var msgTV: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        no_dataRL = findViewById(R.id.no_dataRL)
        titleTV = findViewById(R.id.titleTV)
        msgTV = findViewById(R.id.msgTV)
        parent_cart = findViewById(R.id.parent_cart)
        toolbar_title = findViewById(R.id.toolbar_title)
        toolbar_title!!.text = "Cart"
        utility = Utility()
        pd = ProgressDialog(this@CartActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        cartRV = findViewById(R.id.cartRV)
        CartApiCall()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private fun CartApiCall() {
        if (utility!!.isConnectingToInternet(this@CartActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.GetCartList(Prefs.getString("userid", ""))
                .enqueue(object :
                    Callback<JsonObject> {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful) {
                            pd!!.dismiss()
                            Log.d("responsedata", response.body().toString())
                            val responsedata = response.body().toString()
                            try {
                                val jsonObject = JSONObject(responsedata)
                                if (jsonObject.optBoolean("status")) {
                                    cartproductList = ArrayList()
                                    val data_arry = jsonObject.optJSONObject("data").optJSONArray("cartlist")
                                    for (i in 0 until data_arry.length()) {
                                        val jsonObject1 = data_arry.optJSONObject(i)
                                        val cartProductModel = CartProductModel()
                                        cartProductModel.id = jsonObject1.optString("id")
                                        cartProductModel.userid = jsonObject1.optString("userid")
                                        cartProductModel.providerid = jsonObject1.optString("providerid")
                                        cartProductModel.productid = jsonObject1.optString("productid")
                                        cartProductModel.quantity = jsonObject1.optString("quantity")
                                        cartProductModel.productprice = jsonObject1.optString("productprice")
                                        cartProductModel.totalprice = jsonObject1.optString("totalprice")
                                        cartProductModel.providername = jsonObject1.optString("providername")
                                        cartProductModel.isactive = jsonObject1.optString("isactive")
                                        cartProductModel.productname = jsonObject1.optString("productname")
                                        cartProductModel.productimage = jsonObject1.optString("productimage")
                                        cartproductList!!.add(cartProductModel)
                                    }
                                    if (cartproductList!!.isNotEmpty()) {
                                        Log.d("Size products", "" + cartproductList!!.size)
                                        cartListAdapter = CartListAdapter(this@CartActivity, cartproductList!!)
                                        val mLayoutManager = LinearLayoutManager(this@CartActivity.applicationContext)
                                        cartRV!!.layoutManager = mLayoutManager
                                        cartRV!!.adapter = cartListAdapter
                                    } else {
//                                    no_dataRL!!.visibility = View.VISIBLE

                                    }
                                } else {
                                    cartRV!!.visibility = View.GONE
                                    no_dataRL!!.visibility = View.VISIBLE
                                    titleTV!!.text = "Cart Is Empty"
                                    msgTV!!.text = "Please Add Products TO Order"
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        pd!!.dismiss()
                        utility!!.relative_snackbar(
                            parent_cart!!,
                            getString(R.string.no_internet_connectivity),
                            getString(R.string.close_up)
                        )
                    }
                })
        } else {
            utility!!.relative_snackbar(
                parent_cart!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

}
