package com.quikliq.quikliquser.activities

import android.app.ProgressDialog
import android.content.Context
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
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var utility: Utility? = null
    private var pd: ProgressDialog? = null
    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        toolbar_title!!.text = "Change Password"
        utility = Utility()
        pd = ProgressDialog(this@ChangePasswordActivity)
        pd!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        pd!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        pd!!.isIndeterminate = true
        pd!!.setCancelable(false)
        send_btn_IV.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.send_btn_IV -> checkValidation()
        }
    }

    private fun checkValidation() {
        when {
            old_password_ET!!.text.isEmpty() -> {
                old_password_ET!!.requestFocus()
                old_password_ET!!.error = getString(R.string.txt_Error_required)
            }
            new_password_ET!!.text.isEmpty() -> {
                new_password_ET!!.requestFocus()
                new_password_ET!!.error = getString(R.string.txt_Error_required)
            }

            new_password_ET!!.text.length < 6 -> {
                new_password_ET!!.requestFocus()
                new_password_ET!!.error = getString(R.string.error_password)
            }

            confirm_password_ET!!.text.isEmpty() -> {
                confirm_password_ET!!.requestFocus()
                confirm_password_ET!!.error = getString(R.string.txt_Error_required)
            }


            else -> if (new_password_ET!!.text.toString() == confirm_password_ET!!.text.toString()) {
                changepasswordApiCall()
            } else {
                confirm_password_ET!!.requestFocus()
                confirm_password_ET!!.error = getString(R.string.pass_same)

            }
        }
    }

    private fun changepasswordApiCall() {
        hideKeyboard()
        if (utility!!.isConnectingToInternet(this@ChangePasswordActivity)) {
            pd!!.show()
            pd!!.setContentView(R.layout.loading)
            val requestsCall = RequestsCall()
            requestsCall.ChangePassword(
                Prefs.getString("userid", ""),
                old_password_ET!!.text.toString(),
                new_password_ET!!.text.toString(),
                confirm_password_ET!!.text.toString()
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
                                old_password_ET!!.setText("")
                                new_password_ET!!.setText("")
                                confirm_password_ET!!.setText("")
                                utility!!.relative_snackbar(
                                    parent_pass_change!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            } else {
                                utility!!.relative_snackbar(
                                    parent_pass_change!!,
                                    jsonObject.optString("message"),
                                    getString(R.string.close_up)
                                )
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                    } else {
                        utility!!.relative_snackbar(
                            parent_pass_change!!,
                            response.message(),
                            getString(R.string.close_up)
                        )
                    }

                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    pd!!.dismiss()
                    utility!!.relative_snackbar(
                        parent_pass_change!!,
                        getString(R.string.no_internet_connectivity),
                        getString(R.string.close_up)
                    )
                }
            })
        } else {
            utility!!.relative_snackbar(
                parent_pass_change!!,
                getString(R.string.no_internet_connectivity),
                getString(R.string.close_up)
            )
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this@ChangePasswordActivity.currentFocus!!.windowToken, 0)
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
