package com.quikliq.quikliquser.app

import android.app.Application
import android.content.ContextWrapper
import android.os.StrictMode
import com.google.firebase.FirebaseApp
import com.quikliq.quikliquser.utilities.Prefs


class QuikLikUser : Application() {

    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        FirebaseApp.initializeApp(this)
        Prefs.Builder()
            .setContext(this@QuikLikUser)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}
