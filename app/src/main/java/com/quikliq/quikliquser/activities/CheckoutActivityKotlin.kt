package com.quikliq.quikliquser.activities

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.payment.DependencyHandler
import com.stripe.android.PaymentConfiguration
import com.stripe.android.view.CardInputWidget
import kotlinx.android.synthetic.main.content_checkout.*


class CheckoutActivityKotlin : AppCompatActivity() {
    private var mDependencyHandler: DependencyHandler? = null
    private val PUBLISHABLE_KEY = "pk_test_TYooMQauvdEDq54NiTphI7jx"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        PaymentConfiguration.init(PUBLISHABLE_KEY)
        mDependencyHandler = DependencyHandler(
            this,
            findViewById(R.id.cardInputWidget),
            findViewById(R.id.listview)
        )
        payButton.setOnClickListener {
            mDependencyHandler!!.attachIntentServiceTokenController(this, payButton)
        }

    }


    private fun displayAlert(activity: Activity?, title: String, message: String, restartDemo: Boolean = false) {
        if (activity == null) {
            return
        }
        runOnUiThread {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setMessage(message)
            if (restartDemo) {
                builder.setPositiveButton("Restart demo") { _, _ ->
                    val cardInputWidget =
                        findViewById<CardInputWidget>(R.id.cardInputWidget)
                    cardInputWidget.clear()
                }
            }
            else {
                builder.setPositiveButton("Ok", null)
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

     override fun onDestroy() {
        super.onDestroy()
        mDependencyHandler!!.clearReferences()
    }
}
