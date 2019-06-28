package com.quikliq.quikliquser.fragment

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.adapters.ProvidersAdapter
import com.quikliq.quikliquser.models.ProviderModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    private var layout_manager: RecyclerView.LayoutManager? = null
    private var providersRV: RecyclerView? = null
    private var toolbar_title: TextView? = null
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var parent_f_home: RelativeLayout? = null
    private var providersList: ArrayList<ProviderModel>? = null
    private var no_dataRL: RelativeLayout? = null
    private var providersAdapter: ProvidersAdapter? = null

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
        providersRV = view.findViewById(R.id.providersRV)
        no_dataRL = view.findViewById(R.id.no_dataRL)
        toolbar_title = view.findViewById(R.id.toolbar_title)
        toolbar_title!!.text = "Order"
        parent_f_home = view.findViewById(R.id.parent_f_home)
        providersApiCall()
        return view
    }


    companion object {
        var TAG = HomeFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
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
