package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.constants.Constant.LOCATION
import com.quikliq.quikliquser.constants.Constant.lat
import com.quikliq.quikliquser.constants.Constant.lng
import com.quikliq.quikliquser.constants.Constant.provider_id
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_confirm_order.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmOrderActivity : AppCompatActivity(), OnMapReadyCallback {
    private var myMarker: Marker? = null
    private var toolbar: Toolbar? = null
    private var pd: ProgressDialog? = null
    private var utility: Utility? = null
    private var toolbar_title: TextView? = null
    private var placeorderRL: RelativeLayout? = null
    private var addressET: EditText? = null
    private var noteET: EditText? = null
    private var parent_confirm: RelativeLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)
        initViews()
    }

    private fun initViews() {
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        utility = Utility()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title = findViewById(R.id.toolbar_title)
        placeorderRL = findViewById(R.id.placeorderRL)
        locationTV!!.text = LOCATION
        toolbar_title!!.text = "Place Order"
        noteET = findViewById(R.id.noteET)
        parent_confirm = findViewById(R.id.parent_confirm)
        addressET = findViewById(R.id.addressET)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        pd = ProgressDialog(this@ConfirmOrderActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        placeorderRL!!.setOnClickListener {
            checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            addressET!!.text.isEmpty() -> {
                addressET!!.requestFocus()
                addressET!!.error = getString(R.string.txt_Error_required)
            }
            noteET!!.text.isEmpty() -> {
                noteET!!.requestFocus()
                noteET!!.error = getString(R.string.txt_Error_required)
            }

            else -> orderAPiCall()
        }
    }

    private fun orderAPiCall() {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this@ConfirmOrderActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.PlaceOrder(
                Prefs.getString("userid", ""),
                provider_id!!,
                lat.toString(),
                lng.toString(),
                addressET!!.text.toString() + " " + LOCATION,
                noteET!!.text.toString(),
                "1"
            ).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)

                            if (jsonObject.optBoolean("status")) {
                                utility!!.relative_snackbar(
                                    parent_confirm!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                                startActivity(Intent(this@ConfirmOrderActivity, OrderHistory::class.java).putExtra("order_success",true))
                                finish()
                            } else {
                                utility!!.relative_snackbar(
                                    parent_confirm!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        utility!!.relative_snackbar(
                            parent_confirm!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_confirm!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_confirm!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun hideKeyboard() {
        val imm = this@ConfirmOrderActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onMapReady(p0: GoogleMap?) {
        p0!!.uiSettings.isScrollGesturesEnabled = false
        p0.uiSettings.isZoomControlsEnabled = false
        p0.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, R.raw.style_json
            )
        )
        myMarker = p0.addMarker(
            MarkerOptions()
                .position(
                    LatLng(
                        lat!!,
                        lng!!
                    )
                )
                .title("Order Place")
        )
        p0.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!, lng!!), 15f))
    }
}
