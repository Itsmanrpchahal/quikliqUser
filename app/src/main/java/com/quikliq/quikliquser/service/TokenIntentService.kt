package com.quikliq.quikliquser.service

import android.app.Activity
import android.app.IntentService
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.CheckoutActivityKotlin
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.exception.StripeException
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * An [IntentService] subclass for handling the creation of a [Token] from
 * input [Card] information.
 */
class TokenIntentService : IntentService("TokenIntentService") {



    override fun onHandleIntent(intent: Intent?) {
        var errorMessage: String? = null
        var token: Token? = null
        if (intent != null) {
            val cardNumber = intent.getStringExtra(EXTRA_CARD_NUMBER)
            val month = intent.extras!!.get(EXTRA_MONTH) as Int?
            val year = intent.extras!!.get(EXTRA_YEAR) as Int?
            val cvc = intent.getStringExtra(EXTRA_CVC)

            val card = Card(cardNumber, month, year, cvc)

            val stripe = Stripe(this)
            try {
                token = stripe.createTokenSynchronous(
                    card,
                    PaymentConfiguration.getInstance().publishableKey
                )
            } catch (stripeEx: StripeException) {
                errorMessage = stripeEx.localizedMessage
            }

        }

        val localIntent = Intent(TOKEN_ACTION)
        if (token != null) {
            localIntent.putExtra(STRIPE_CARD_LAST_FOUR, token.card.last4)
            localIntent.putExtra(STRIPE_CARD_TOKEN_ID, token.id)
            Log.d("token", token.id)
            CheckoutActivityKotlin.paymentTokenInterface!!.PaymentToken(token.id)
        }

        if (errorMessage != null) {
            localIntent.putExtra(STRIPE_ERROR_MESSAGE, errorMessage)
        }

        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }

    companion object {

        val TOKEN_ACTION = "com.stripe.example.service.tokenAction"
        val STRIPE_CARD_LAST_FOUR = "com.stripe.example.service.cardLastFour"
        val STRIPE_CARD_TOKEN_ID = "com.stripe.example.service.cardTokenId"
        val STRIPE_ERROR_MESSAGE = "com.stripe.example.service.errorMessage"

        private val EXTRA_CARD_NUMBER = "com.stripe.example.service.extra.cardNumber"
        private val EXTRA_MONTH = "com.stripe.example.service.extra.month"
        private val EXTRA_YEAR = "com.stripe.example.service.extra.year"
        private val EXTRA_CVC = "com.stripe.example.service.extra.cvc"

        fun createTokenIntent(
            launchingActivity: Activity,
            cardNumber: String?,
            month: Int?,
            year: Int?,
            cvc: String?
        ): Intent {
            return Intent(launchingActivity, TokenIntentService::class.java)
                .putExtra(EXTRA_CARD_NUMBER, cardNumber)
                .putExtra(EXTRA_MONTH, month)
                .putExtra(EXTRA_YEAR, year)
                .putExtra(EXTRA_CVC, cvc)
        }
    }


}
