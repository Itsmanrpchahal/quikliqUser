package com.quikliq.quikliquser.app

import android.app.Application
import android.content.ContextWrapper
import android.os.StrictMode
import com.quikliq.quikliquser.utilities.Prefs


class QuikLikUser : Application() {

    override fun onCreate() {
        super.onCreate()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        Prefs.Builder()
            .setContext(this@QuikLikUser)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}
