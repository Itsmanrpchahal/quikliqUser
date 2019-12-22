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
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import com.quikliq.quikliquser.constants.Constant
import kotlinx.android.synthetic.main.activity__login.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__login)
        utility = Utility()
        pd = ProgressDialog(this@LoginActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        forgotPasswordLayout.setOnClickListener(this)
        signUp_RL.setOnClickListener(this)
        nextScreen.setOnClickListener(this)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
            val newToken = instanceIdResult.token
            Prefs.putString(Constant.FCM_TOKEN, newToken)
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.forgotPasswordLayout -> startActivity(Intent(this, ForgotPasswordActivity::class.java))
            R.id.signUp_RL -> startActivity(Intent(this, MobileNumberActivity::class.java))
            R.id.nextScreen -> checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            loginUsername.text.isEmpty() -> {
                loginUsername.requestFocus()
                loginUsername.error = getString(R.string.txt_Error_required)
            }
            loginPassword.text.isEmpty() -> {
                loginPassword.requestFocus()
                loginPassword.error = getString(R.string.txt_Error_required)
            }
            loginPassword.text.length < 6 -> {
                loginPassword.requestFocus()
                loginPassword.error = getString(R.string.error_password)
            }
            else -> loginApiCall(loginUsername.text.toString(), loginPassword.text.toString())
        }
    }
    private fun loginApiCall(username: String, password: String) {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this@LoginActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.login(username, password, "1",  Prefs.getString(Constant.FCM_TOKEN, ""),"2").enqueue(object : Callback<JsonObject> {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pd!!.dismiss()
                    if (response.isSuccessful) {
                        Log.d("responsedata",response.body().toString())
                        val responsedata = response.body().toString()
                        try {
                            val jsonObject = JSONObject(responsedata)

                            if(jsonObject.optBoolean("status")){
                                Prefs.putString("profileimage",jsonObject.optJSONObject("data").optString("profileimage"))
                                Prefs.putString("FirstName",jsonObject.optJSONObject("data").optString("FirstName"))
                                Prefs.putString("LastName",jsonObject.optJSONObject("data").optString("LastName"))
                                Prefs.putString("Mobile",jsonObject.optJSONObject("data").optString("Mobile"))
                                Prefs.putString("Email", jsonObject.optJSONObject("data").optString("Email"))
                                Prefs.putString("BusinesNname",jsonObject.optJSONObject("data").optString("BusinesNname"))
                                Prefs.putString("BankName", jsonObject.optJSONObject("data").optString("BankName"))
                                Prefs.putString("AccountNumber",jsonObject.optJSONObject("data").optString("AccountNumber"))
                                Prefs.putString("IFSC",jsonObject.optJSONObject("data").optString("IFSC"))
                                Prefs.putString("Address",jsonObject.optJSONObject("data").optString("Address"))
                                Prefs.putString("UserType",jsonObject.optJSONObject("data").optString("UserType"))
                             Prefs.putString("userid",jsonObject.optJSONObject("data").optString("userid"))
                                Prefs.putInt("IsAgeVerify",jsonObject.optJSONObject("data").optInt("IsAgeVerify"))
                                Prefs.putBoolean(Constant.IS_LOGGED_IN, true)
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        HomeActivity::class.java
                                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                )
                                finish()
                            }else{
                                utility!!.relative_snackbar(parent_login!!, jsonObject.optString("message"), getString(R.string.close_up))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    }else{
                        utility!!.relative_snackbar(
                            parent_login!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(parent_login!!, getString(R.string.no_internet_connectivity), getString(R.string.close_up))
                }
            })
        } else {
            utility!!.relative_snackbar(parent_login!!, getString(R.string.no_internet_connectivity), getString(R.string.close_up))
        }
    }

    private fun hideKeyboard() {
        val imm = this@LoginActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}
