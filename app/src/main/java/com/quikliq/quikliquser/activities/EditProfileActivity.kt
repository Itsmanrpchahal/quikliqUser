package com.quikliq.quikliquser.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.imagePicker.PickerBuilder
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class EditProfileActivity : AppCompatActivity(),View.OnClickListener {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var outputUri: Uri? = null
    private var profilePicChoosed: Boolean? = false
    private var toolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title!!.text = "Edit Profile"
        utility = Utility()
        pd = ProgressDialog(this@EditProfileActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        profileApiCall()
        edit_profile_image_IV!!.setOnClickListener(this)
        edit_profile_send_btn_IV!!.setOnClickListener(this)
    }


    private fun profileApiCall() {
        if (utility!!.isConnectingToInternet(this@EditProfileActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.profile(Prefs.getString("userid", "")).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                edit_first_name_ET!!.setText(jsonObject.optJSONObject("data").optString("FirstName"))
                                edit_last_name_ET!!.setText(jsonObject.optJSONObject("data").optString("LastName"))
                                edit_profile_email_TV!!.setText(jsonObject.optJSONObject("data").optString("Email"))
                                    utility!!.loadImageWithLoader(
                                        this@EditProfileActivity,
                                        jsonObject.optJSONObject("data").optString("profileimage"),
                                        business_image_IV
                                    )


                            } else {
                                utility!!.linear_snackbar(
                                    parent_edit_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }else{
                        utility!!.linear_snackbar(
                            parent_edit_profile!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_edit_profile!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_edit_profile!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.edit_profile_image_IV->{
                if ((ContextCompat.checkSelfPermission(
                        this@EditProfileActivity!!,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                        this@EditProfileActivity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                        this@EditProfileActivity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    methodRequiresPermissions()
                } else {
                    pictureSelectionDialog()
                }
            }
            R.id.edit_profile_send_btn_IV -> if(profilePicChoosed!!){
                when {
                    edit_first_name_ET!!.text.isEmpty() -> {
                        edit_first_name_ET!!.requestFocus()
                        edit_first_name_ET!!.error = getString(R.string.txt_Error_required)
                    }
                    edit_last_name_ET!!.text.isEmpty() -> {
                        edit_last_name_ET!!.requestFocus()
                        edit_last_name_ET!!.error = getString(R.string.txt_Error_required)
                    }
                    edit_profile_email_TV!!.text.isEmpty() -> {
                        edit_profile_email_TV!!.requestFocus()
                        edit_profile_email_TV!!.error = getString(R.string.txt_Error_required)
                    }
                    !utility!!.isValidEmail(edit_profile_email_TV!!.text.toString().trim { it <= ' ' }) -> {
                        edit_profile_email_TV!!.requestFocus()
                        edit_profile_email_TV!!.error = getString(R.string.Invalid_email)
                    }
                    else -> updateProfileApiCall()
                }

            }else{
                checkValidation()
            }
        }
    }

    private fun checkValidation() {
        when {
            edit_first_name_ET!!.text.isEmpty() -> {
                edit_first_name_ET!!.requestFocus()
                edit_first_name_ET!!.error = getString(R.string.txt_Error_required)
            }
            edit_last_name_ET!!.text.isEmpty() -> {
                edit_last_name_ET!!.requestFocus()
                edit_last_name_ET!!.error = getString(R.string.txt_Error_required)
            }
            edit_profile_email_TV!!.text.isEmpty() -> {
                edit_profile_email_TV!!.requestFocus()
                edit_profile_email_TV!!.error = getString(R.string.txt_Error_required)
            }
            !utility!!.isValidEmail(edit_profile_email_TV!!.text.toString().trim { it <= ' ' }) -> {
                edit_profile_email_TV!!.requestFocus()
                edit_profile_email_TV!!.error = getString(R.string.Invalid_email)
            }
            else -> updateWithoutImageApiCall()
        }
    }

    private fun pictureSelectionDialog() {
        val cameraIV: ImageView
        val galleryIV: ImageView
        val dialog = Dialog(this@EditProfileActivity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.profile_picture_dailog)
        cameraIV = dialog.findViewById<View>(R.id.cameraIV) as ImageView
        galleryIV = dialog.findViewById<View>(R.id.galleryIV) as ImageView
        galleryIV.setOnClickListener {
            PickerBuilder(this@EditProfileActivity!!, PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener { imageUri ->
                    outputUri = imageUri
                    try {
//                        val captureBmp = MediaStore.Images.Media.getBitmap(this@EditProfileActivity!!.contentResolver, imageUri)
//                        business_image_IV!!.setImageBitmap(captureBmp)
                        profilePicChoosed = true
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener { }
                .start()


            dialog.dismiss()
        }
        cameraIV.setOnClickListener {
            PickerBuilder(this@EditProfileActivity!!, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener { imageUri ->
                    outputUri = imageUri
                    try {
//                        val captureBmp = MediaStore.Images.Media.getBitmap(this@EditProfileActivity!!.contentResolver, imageUri)
//                        business_image_IV!!.setImageBitmap(captureBmp)
                        profilePicChoosed = true
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener { }
                .start()

            dialog.dismiss()
        }
        dialog.show()
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
        options = quickPermissionsOption
    ) {
        //        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
//        deviceId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            telephonyManager!!.imei
//        } else {
//            telephonyManager!!.deviceId
//        }
//        Prefs.putString(Constant.DEVICE_UNIQUE_ID, deviceId)
        pictureSelectionDialog()

    }

    /**methodRequiresPermissions
     * this will be called when permission is denied once or more time. Handle it your way
     */
    private fun rationaleCallback(req: QuickPermissionsRequest) {
        AlertDialog.Builder(this@EditProfileActivity!!)
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
        AlertDialog.Builder(this@EditProfileActivity!!)
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
    private fun updateWithoutImageApiCall() {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this@EditProfileActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.UpdateProfileWithoutImage(
                Prefs.getString("userid", ""),
                edit_first_name_ET!!.text.toString(),
                edit_last_name_ET!!.text.toString(),
                edit_profile_email_TV!!.text.toString()
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

                                utility!!.linear_snackbar(
                                    parent_edit_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            } else {
                                utility!!.linear_snackbar(
                                    parent_edit_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }else{
                        utility!!.linear_snackbar(
                            parent_edit_profile!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_edit_profile!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_edit_profile!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun hideKeyboard() {
        val imm = this@EditProfileActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this@EditProfileActivity!!.currentFocus!!.windowToken, 0)
    }

    private fun updateProfileApiCall() {
        if(edit_first_name_ET.hasFocus()){
            hideKeyboard()
        }else if (edit_last_name_ET.hasFocus()){
            hideKeyboard()
        }

        if (utility!!.isConnectingToInternet(this@EditProfileActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.UpdateProfile(
                this@EditProfileActivity!!,
                Prefs.getString("userid", ""),
                edit_first_name_ET!!.text.toString(),
                edit_last_name_ET!!.text.toString(),
                edit_profile_email_TV!!.text.toString(),
                outputUri
            ).enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("add_productresponsedata", response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)
                            if (jsonObject.optBoolean("status")) {
                                utility!!.linear_snackbar(
                                    parent_edit_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                                finish()
                                overridePendingTransition(0, 0)
                                startActivity(intent)
                                overridePendingTransition(0, 0)
                            } else {
                                utility!!.linear_snackbar(
                                    parent_edit_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }else{
                        utility!!.linear_snackbar(
                            parent_edit_profile!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_edit_profile!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_edit_profile!!,
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
}
