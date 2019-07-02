package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.adapters.CartListAdapter
import com.quikliq.quikliquser.constants.Constant
import com.quikliq.quikliquser.constants.Constant.provider_id
import com.quikliq.quikliquser.interfaces.RemoveItemInterface
import com.quikliq.quikliquser.interfaces.UpdateQuantityInterface
import com.quikliq.quikliquser.models.CartProductModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CartActivity : AppCompatActivity(), RemoveItemInterface, UpdateQuantityInterface, View.OnClickListener {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var toolbar: Toolbar? = null
    private var parent_cart: LinearLayout? = null
    private var toolbar_title: TextView? = null
    private var cartListAdapter: CartListAdapter? = null
    private var cartproductList: ArrayList<CartProductModel>? = null
    private var cartRV: RecyclerView? = null
    private var no_dataRL: RelativeLayout? = null
    private var titleTV: TextView? = null
    private var msgTV: TextView? = null
    private var locationTV: TextView? = null
    private var orderRL: RelativeLayout? = null
    private var totalpriceTV: TextView? = null
    private var ammountRL: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        no_dataRL = findViewById(R.id.no_dataRL)
        ammountRL = findViewById(R.id.ammountRL)
        titleTV = findViewById(R.id.titleTV)
        msgTV = findViewById(R.id.msgTV)
        titleTV!!.text = "Cart Is Empty"
        msgTV!!.text = "Please Add Products TO Order"
        totalpriceTV = findViewById(R.id.totalpriceTV)
        parent_cart = findViewById(R.id.parent_cart)
        orderRL = findViewById(R.id.orderRL)
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
        locationTV = findViewById(R.id.locationTV)
        locationTV!!.text = Constant.LOCATION
        CartApiCall()
        removeItemInterface = this
        updateQuantityInterface = this
        orderRL!!.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.orderRL -> startActivity(Intent(this@CartActivity, ConfirmOrderActivity::class.java))
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
                                        provider_id = jsonObject1.optString("providerid")
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
                                    totalpriceTV!!.text =
                                        "$" + jsonObject.optJSONObject("data").optString("totalamount")

                                    if (cartproductList!!.isNotEmpty()) {
                                        Log.d("Size products", "" + cartproductList!!.size)
                                        cartListAdapter = CartListAdapter(this@CartActivity, cartproductList!!)
                                        val mLayoutManager = LinearLayoutManager(this@CartActivity.applicationContext)
                                        cartRV!!.layoutManager = mLayoutManager
                                        cartRV!!.adapter = cartListAdapter
                                        parent_cart!!.visibility = View.VISIBLE
                                        no_dataRL!!.visibility = View.GONE
                                    }else{
                                        no_dataRL!!.visibility = View.VISIBLE
                                        parent_cart!!.visibility = View.GONE
                                        titleTV!!.text = "Cart Is Empty"
                                        msgTV!!.text = "Please Add Products TO Order"
                                    }
                                } else {
                                    no_dataRL!!.visibility = View.VISIBLE
                                    parent_cart!!.visibility = View.GONE
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


    private fun RemoveCartItemsApiCall(cart_id: String) {
        if (utility!!.isConnectingToInternet(this@CartActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.RemoveCartItems(Prefs.getString("userid", ""), cart_id)
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
                                    totalpriceTV!!.text =
                                        "$" + jsonObject.optJSONObject("data").optString("totalamount")

                                    if (cartproductList!!.isNotEmpty() && cartListAdapter != null) {
                                        cartListAdapter!!.updateData(cartproductList!!)
                                        cartListAdapter!!.notifyDataSetChanged()
                                        parent_cart!!.visibility = View.VISIBLE
                                        no_dataRL!!.visibility = View.GONE
                                    }else{
                                        no_dataRL!!.visibility = View.VISIBLE
                                        parent_cart!!.visibility = View.GONE
                                        titleTV!!.text = "Cart Is Empty"
                                        msgTV!!.text = "Please Add Products TO Order"
                                    }
                                } else {
                                    parent_cart!!.visibility = View.GONE
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

    companion object {
        var removeItemInterface: RemoveItemInterface? = null
        var updateQuantityInterface: UpdateQuantityInterface? = null
    }

    override fun remove(card_id: String) {
        RemoveCartItemsApiCall(card_id)
    }

    private fun updateItemsApiCall(cart_id: String, price: String, quantity: String) {
        if (utility!!.isConnectingToInternet(this@CartActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.UpdateCartQuantity(Prefs.getString("userid", ""), cart_id, price, quantity)
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
                                    totalpriceTV!!.text = "$" + jsonObject.optJSONObject("data").optString("totalamount")

                                    if (cartproductList!!.isNotEmpty() && cartListAdapter != null) {
                                        cartListAdapter!!.updateData(cartproductList!!)
                                        cartListAdapter!!.notifyDataSetChanged()
                                        parent_cart!!.visibility = View.VISIBLE
                                        no_dataRL!!.visibility = View.GONE
                                    }else{
                                        no_dataRL!!.visibility = View.VISIBLE
                                        parent_cart!!.visibility = View.GONE
                                        titleTV!!.text = "Cart Is Empty"
                                        msgTV!!.text = "Please Add Products TO Order"
                                    }
                                } else {
                                    parent_cart!!.visibility = View.GONE
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

    override fun update(card_id: String, price: String, quantity: String) {
        updateItemsApiCall(card_id, price, quantity)
    }

}
