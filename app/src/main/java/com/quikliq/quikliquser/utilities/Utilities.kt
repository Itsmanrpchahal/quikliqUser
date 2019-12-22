package com.quikliq.quikliquser.utilities

import android.graphics.Rect
import android.view.View

object Utilities {

    fun isSoftKeyboardOpen(rootView: View): Boolean {
        val r = Rect()
        rootView.getWindowVisibleDisplayFrame(r)
        val screenHeight = rootView.rootView.height
        val keypadHeight = screenHeight - r.bottom
        return keypadHeight > screenHeight * 0.15
    }
}
