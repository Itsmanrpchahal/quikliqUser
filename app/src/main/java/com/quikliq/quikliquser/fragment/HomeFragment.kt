package com.quikliq.quikliquser.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.CartActivity
import com.quikliq.quikliquser.activities.SplashActivity.Companion.createLocationRequest
import com.quikliq.quikliquser.adapters.ProvidersAdapter
import com.quikliq.quikliquser.constants.Constant.LOCATION
import com.quikliq.quikliquser.constants.Constant.lat
import com.quikliq.quikliquser.constants.Constant.lng
import com.quikliq.quikliquser.models.ProviderModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.layout_no_data.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private var layout_manager: RecyclerView.LayoutManager? = null
    private var providersRV: RecyclerView? = null
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var parent_f_home: RelativeLayout? = null
    private var providersList: ArrayList<ProviderModel>? = null
    private var no_dataRL: RelativeLayout? = null
    private var cartIV: ImageView? = null
    private var providersAdapter: ProvidersAdapter? = null
    private var geocoder: Geocoder? = null
    private var addressTV: TextView? = null
    private var addresses: List<Address>? = null
    private var searchET: EditText? = null
    private var clearBT: Button? = null
    private var searchedArraylist: ArrayList<ProviderModel>? = null
    private var timer: Timer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        layout_manager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        utility = Utility()
        pd = ProgressDialog(activity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        addressTV = view.findViewById(R.id.addressTV)
        providersRV = view.findViewById(R.id.providersRV)
        no_dataRL = view.findViewById(R.id.no_dataRL)
        parent_f_home = view.findViewById(R.id.parent_f_home)
        cartIV = view.findViewById(R.id.cartIV)
        searchET = view.findViewById(R.id.searchET)
        cartIV!!.visibility = View.VISIBLE
        cartIV!!.setOnClickListener {
            startActivity(Intent(activity, CartActivity::class.java))
        }
        geocoder = Geocoder(activity)
        addresses = geocoder!!.getFromLocation(
            lat!!,
            lng!!,
            1
        )
        clearBT = view.findViewById(R.id.clearBT)
        if(addresses != null && addresses!!.isNotEmpty()) {
            LOCATION = addresses!![0].getAddressLine(0)
            addressTV!!.text = addresses!![0].getAddressLine(0)
        }
        val handler = Handler()
        timer = Timer()
        val doTask: TimerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    try {
                        //Log.d("refresh", "done")
                        activity!!.runOnUiThread {
                            if(LOCATION == null){
                                if(lat == 0.00 && lng == 0.00){
                                    createLocationRequest()
                                }

                                geocoder = Geocoder(activity)
                                addresses = geocoder!!.getFromLocation(
                                    lat!!,
                                    lng!!,
                                    1
                                )
                                if(addresses != null && addresses!!.isNotEmpty()) {
                                    LOCATION = addresses!![0].getAddressLine(0)
                                    addressTV!!.text = addresses!![0].getAddressLine(0)
                                    timer!!.cancel()

                                }

                            }
                        }
                    } catch (e: Exception) {
                        // TODO Auto-generated catch block
                    }
                }
            }
        }
        timer!!.schedule(doTask, 0, 500)

        providersApiCall()
        clearBT!!.setOnClickListener {
            searchET!!.setText("")
            searchET!!.hint = "Search for providers"
        }

        searchET!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    clearBT!!.visibility = View.VISIBLE
                    searchByName(s.toString())
                } else {
                    clearBT!!.visibility = View.GONE
                    setDefaultData()
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    searchByName(s.toString())
                } else {
                    setDefaultData()
                }

            }
        })
        return view
    }

    fun setDefaultData() {
        no_dataRL!!.visibility = View.GONE
        providersRV!!.visibility = View.VISIBLE
        if (providersAdapter != null)
            providersAdapter = null
        if (providersList != null)
            if (providersList!!.size > 0) {
                providersAdapter = ProvidersAdapter(activity!!, providersList!!)
                val mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
                providersRV!!.layoutManager = mLayoutManager
                providersRV!!.adapter = providersAdapter
                providersAdapter!!.notifyDataSetChanged()

            }
    }

    fun searchByName(s: String) {
        searchedArraylist = ArrayList()
        if (providersList != null && providersList!!.size > 0)
            for (i in providersList!!.indices) {
                val providerModel = providersList!![i]
                if (providerModel.name!!.toLowerCase().contains(s.toLowerCase()))
                    searchedArraylist!!.add(providerModel)
            }
        if (providersAdapter != null)
            providersAdapter = null

        if (searchedArraylist!!.size > 0) {
            setSearchedData()
        } else {
            noMatchedData()
        }
    }


    private fun setSearchedData() {
        no_dataRL!!.visibility = View.GONE
        providersRV!!.visibility = View.VISIBLE
        providersAdapter = ProvidersAdapter(activity!!, searchedArraylist!!)
        val mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        providersRV!!.layoutManager = mLayoutManager
        providersRV!!.adapter = providersAdapter
    }

    private fun noMatchedData() {
        no_dataRL!!.visibility = View.VISIBLE
        providersRV!!.visibility = View.GONE
        sadIV!!.background = ContextCompat.getDrawable(activity!!, R.drawable.ic_search)
        titleTV!!.text = "No matched Provider"
        msgTV!!.visibility = View.GONE
    }

    private fun providersApiCall() {
        if (utility!!.isConnectingToInternet(activity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.GetAllProviders(Prefs.getString("userid", "")).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)

                            if (jsonObject.optBoolean("status")) {
                                providersList = ArrayList()
                                val data_arry = jsonObject.optJSONArray("data")
                                for (i in 0 until data_arry.length()) {
                                    val jsonObject1 = data_arry.optJSONObject(i)
                                    val providerModel = ProviderModel()
                                    providerModel.provider_id = jsonObject1.optString("provider_id")
                                    providerModel.name = jsonObject1.optString("name")
                                    providerModel.rating = jsonObject1.optInt("rating")
                                    providerModel.picture = jsonObject1.optString("picture")
                                    providerModel.opentime = jsonObject1.optString("opentime")
                                    providerModel.closetime = jsonObject1.optString("closetime")
                                    providerModel.adress = jsonObject1.optString("adress")
                                    providerModel.distance = jsonObject1.optString("distance")
                                    providerModel.Status = jsonObject1.optInt("Status")
                                    providerModel.lat = jsonObject1.optDouble("lat")
                                    providerModel.lng = jsonObject1.optDouble("lng")
                                    providersList!!.add(providerModel)
                                }
                                if (providersList!!.isNotEmpty()) {
                                    providersAdapter = ProvidersAdapter(activity!!, providersList!!)
                                    val mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
                                    providersRV!!.layoutManager = mLayoutManager
                                    providersRV!!.adapter = providersAdapter
                                } else {
                                    no_dataRL!!.visibility = View.VISIBLE

                                }


                            } else {
                                utility!!.relative_snackbar(
                                    parent_f_home!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        utility!!.relative_snackbar(
                            parent_f_home!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_f_home!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_f_home!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }
}
