package com.quikliq.quikliquser.activities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.constants.Constant
import com.quikliq.quikliquser.constants.Constant.lat
import com.quikliq.quikliquser.constants.Constant.lng
import com.quikliq.quikliquser.utilities.GpsUtils
import com.quikliq.quikliquser.utilities.Prefs
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    private var mLocationRequest: LocationRequest? = null
    private val INTERVAL = (1000 * 10).toLong()
    private val FASTEST_INTERVAL = (1000 * 5).toLong()
    private lateinit var locationCallback: LocationCallback
    private var update = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__splash)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Prefs.putString(Constant.FCM_TOKEN, newToken)
        }

        methodRequiresPermissions()
        checkForGps()
        createLocationRequest()
        if (ContextCompat.checkSelfPermission(
                this@SplashActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun startLocationUpdates() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, permissions, 0)
                return
            }
        }

        fusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            locationCallback,
            null
        )
    }


    /**
     * used for rquesting the permissions in OS version Marshmallow or higher
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun methodRequiresPermissions() = runWithPermissions(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        options = quickPermissionsOption
    ) {

        //        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
//        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            telephonyManager!!.imei
//        } else {
//            telephonyManager!!.deviceId
//        }
//        Prefs.putString(Constant.DEVICE_UNIQUE_ID, deviceId)

        Handler().postDelayed({
            if (Prefs.getBoolean(Constant.IS_LOGGED_IN, false)) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }, 3000)
    }


    /**methodRequiresPermissions
     * this will be called when permission is denied once or more time. Handle it your way
     */
    private fun rationaleCallback(req: QuickPermissionsRequest) {
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("This is the custom rationale dialog. Please allow us to proceed " + "asking for permissions again, or cancel to end the permission flow.")
            .setPositiveButton("Go Ahead") { _, _ -> req.proceed() }
            .setNegativeButton("cancel") { _, _ -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    /**
     * this will be called when some/all permissions required by the method are permanently
    denied. Handle it your way.
     */
    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage(
                "This is the custom permissions permanently denied dialog. " +
                        "Please open app settings to open app settings for allowing permissions, " +
                        "or cancel to end the permission flow."
            )
            .setPositiveButton("App Settings") { _, _ -> req.openAppSettings() }
            .setNegativeButton("Cancel") { _, _ -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    /**
     * used for permission callbacks
     */
    private val quickPermissionsOption = QuickPermissionsOptions(
        rationaleMessage = "Custom rational message",
        permanentlyDeniedMessage = "Custom permanently denied message",
        rationaleMethod = { rationaleCallback(it) },
        permanentDeniedMethod = { permissionsPermanentlyDenied(it) }
    )

    /**
     * check gps is on or not and display dialog
     */
    private fun checkForGps() {
        GpsUtils(this).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
            }

        })
    }

    /**
     * creating location request
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = INTERVAL
        mLocationRequest?.fastestInterval = FASTEST_INTERVAL
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    lat = location.latitude
                    lng = location.longitude
                    if (!update)
                        apiLocation(location)
                }
            }
        }

    }


    fun apiLocation(location: Location) {
        val requestsCall = RequestsCall()
        requestsCall.UpdateLatLong(Prefs.getString("userid", ""), location.latitude, location.longitude)
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
                                update = true
                            } else {
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {

                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                }
            })
    }

}
