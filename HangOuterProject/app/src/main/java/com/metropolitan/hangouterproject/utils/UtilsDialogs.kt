package com.metropolitan.hangouterproject.utils

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import com.metropolitan.hangouterproject.R
import dmax.dialog.SpotsDialog

object UtilsDialogs {

    @JvmStatic
    fun setProgressDialog(preferences: SharedPreferences, context: Context): AlertDialog {
        return if (!preferences.getBoolean("themeKey", false)) {
            setProgressDialog(context, R.style.ProgressDialogLightTheme, false)
        } else {
            setProgressDialog(context, R.style.ProgressDialogDarkTheme, false)
        }
    }

    // Helper method for building progressDialog
    private fun setProgressDialog(context: Context, styleId: Int, cancelable: Boolean): AlertDialog {
        return SpotsDialog.Builder()
            .setContext(context)
            .setTheme(styleId)
            .setCancelable(cancelable)
            .build().apply {
                show()
            }
    }
}