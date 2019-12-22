package com.quikliq.quikliquser.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.*
import com.quikliq.quikliquser.imagePicker.PickerBuilder
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ProfileFragment : Fragment(), View.OnClickListener {
    private var edit_BT: Button? = null
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var parent_profile: LinearLayout? = null
    private var userName_profile_TV: TextView? = null
    private var age_verification_TV: TextView? = null
    private var user_image_IV: ImageView? = null
    private var order_history_TV: TextView? = null
    private var change_password_TV: TextView? = null
    private var contact_us_TV: TextView? = null
    private var toolbar_title: TextView? = null
    private var sign_out_Tv: TextView? = null
    private var outputUri: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        parent_profile = view.findViewById(R.id.parent_profile)
        userName_profile_TV = view.findViewById(R.id.userName_profile_TV)
        user_image_IV = view.findViewById(R.id.user_image_IV)
        utility = Utility()
        pd = ProgressDialog(activity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        order_history_TV = view.findViewById(R.id.order_history_TV)
        edit_BT = view.findViewById(R.id.edit_BT)
        sign_out_Tv = view.findViewById(R.id.sign_out_Tv)
        change_password_TV = view.findViewById(R.id.change_password_TV)
        age_verification_TV = view.findViewById(R.id.age_verification_TV)
        contact_us_TV = view.findViewById(R.id.contact_us_TV)
        toolbar_title = view.findViewById(R.id.toolbar_title)
        toolbar_title!!.text = "Account"
        edit_BT?.setOnClickListener(this)
        contact_us_TV?.setOnClickListener(this)
        order_history_TV?.setOnClickListener(this)
        change_password_TV?.setOnClickListener(this)
        sign_out_Tv?.setOnClickListener(this)
        if(Prefs.getInt("IsAgeVerify",0) == 1){
            age_verification_TV!!.text = "Age Veirifed"
        }else{
            age_verification_TV?.setOnClickListener(this)

        }
        profileApiCall()
        return view
    }

    override fun onResume() {
        super.onResume()
        userName_profile_TV!!.text = Prefs.getString("FirstName", "") + " " + Prefs.getString("LastName", "")
        utility!!.loadImageWithLoader(
            activity,
            Prefs.getString(
                "profileimage", ""
            ),
            user_image_IV
        )
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.edit_BT -> startActivity(Intent(activity, EditProfileActivity::class.java))
            R.id.order_history_TV -> startActivity(Intent(activity, OrderHistory::class.java))
            R.id.change_password_TV -> startActivity(Intent(activity, ChangePasswordActivity::class.java))
            R.id.contact_us_TV -> startActivity(Intent(activity, ContactUsActivity::class.java))
            R.id.about_Tv -> Log.d("about", "about")
            R.id.sign_out_Tv -> logoutApiCall()
            R.id.age_verification_TV -> if ((ContextCompat.checkSelfPermission(
                    this.activity!!,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    this.activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                    this.activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                methodRequiresPermissions()
            } else {
                pictureSelectionDialog()
            }
        }
    }


    /*    used for rquesting the permissions in OS version Marshmallow or higher*/
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun methodRequiresPermissions() = runWithPermissions(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        options = quickPermissionsOption
    ) {
        pictureSelectionDialog()

    }

    /**methodRequiresPermissions
     * this will be called when permission is denied once or more time. Handle it your way
     */
    private fun rationaleCallback(req: QuickPermissionsRequest) {
        AlertDialog.Builder(this.activity!!)
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
        AlertDialog.Builder(this.activity!!)
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


    private fun profileApiCall() {
        if (utility!!.isConnectingToInternet(activity)) {
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
                                userName_profile_TV!!.text =
                                    jsonObject.optJSONObject("data").optString("FirstName") + " " + jsonObject.optJSONObject(
                                        "data"
                                    ).optString("LastName")

                                utility!!.loadImageWithLoader(
                                    activity,
                                    jsonObject.optJSONObject("data").optString("profileimage"),
                                    user_image_IV
                                )


                                Prefs.putString(
                                    "profileimage",
                                    jsonObject.optJSONObject("data").optString("profileimage")
                                )
                                Prefs.putString("FirstName", jsonObject.optJSONObject("data").optString("FirstName"))
                                Prefs.putString("LastName", jsonObject.optJSONObject("data").optString("LastName"))
                                Prefs.putString("Mobile", jsonObject.optJSONObject("data").optString("Mobile"))
                                Prefs.putString("Email", jsonObject.optJSONObject("data").optString("Email"))
                                Prefs.putString(
                                    "BusinesNname",
                                    jsonObject.optJSONObject("data").optString("BusinesNname")
                                )
                                Prefs.putString("BankName", jsonObject.optJSONObject("data").optString("BankName"))
                                Prefs.putString(
                                    "AccountNumber",
                                    jsonObject.optJSONObject("data").optString("AccountNumber")
                                )
                                Prefs.putString("IFSC", jsonObject.optJSONObject("data").optString("IFSC"))
                                Prefs.putString("Address", jsonObject.optJSONObject("data").optString("Address"))
                                Prefs.putString("UserType", jsonObject.optJSONObject("data").optString("UserType"))
                                Prefs.putString("userid", jsonObject.optJSONObject("data").optString("userid"))
                                Prefs.putInt("IsAgeVerify",jsonObject.optJSONObject("data").optInt("IsAgeVerify"))
                                if(Prefs.getInt("IsAgeVerify",0) == 1){
                                    age_verification_TV!!.text = "Age Veirifed"
                                }
                            } else {
                                utility!!.linear_snackbar(
                                    parent_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_profile!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_profile!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun logoutApiCall() {
        if (utility!!.isConnectingToInternet(activity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.Logout(
                Prefs.getString("userid", "")
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
                                    parent_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                                Prefs.clear()
                                startActivity(Intent(activity, LoginActivity::class.java))
                                finishAffinity(activity!!)
                            } else {
                                utility!!.linear_snackbar(
                                    parent_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        utility!!.linear_snackbar(
                            parent_profile!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_profile!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_profile!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun pictureSelectionDialog() {
        val cameraIV: ImageView
        val galleryIV: ImageView
        val dialog = Dialog(this.activity!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.profile_picture_dailog)
        cameraIV = dialog.findViewById<View>(R.id.cameraIV) as ImageView
        galleryIV = dialog.findViewById<View>(R.id.galleryIV) as ImageView
        galleryIV.setOnClickListener {
            PickerBuilder(this.activity!!, PickerBuilder.SELECT_FROM_GALLERY)
                .setOnImageReceivedListener { imageUri ->
                    outputUri = imageUri
                    uploadIdProofApiCall()
                }
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener { }
                .start()


            dialog.dismiss()
        }
        cameraIV.setOnClickListener {
            PickerBuilder(this.activity!!, PickerBuilder.SELECT_FROM_CAMERA)
                .setOnImageReceivedListener { imageUri ->
                    outputUri = imageUri
                    uploadIdProofApiCall()
                }
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener { }
                .start()

            dialog.dismiss()
        }
        dialog.show()
    }


    private fun uploadIdProofApiCall() {


        if (utility!!.isConnectingToInternet(activity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.UploadIdProof(
                this.activity!!,
                Prefs.getString("userid", ""),
                outputUri, "driver"
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
                                    parent_profile!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            } else {
                                utility!!.linear_snackbar(
                                    parent_profile,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        utility!!.linear_snackbar(
                            parent_profile,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.linear_snackbar(
                        parent_profile,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.linear_snackbar(
                parent_profile,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

}
