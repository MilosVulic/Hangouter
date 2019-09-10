package com.metropolitan.hangouterproject.utils

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.AppConstants

object UtilsAuthentication {

    // Setting singIn activity
    @JvmStatic
    fun startSignInProccess(activity: Activity) {
        val providers = arrayListOf(
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(true)
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.my_app)
                .setTheme(R.style.BlackTheme)
                .setTosAndPrivacyPolicyUrls(
                    "https://example.com/terms.html",
                    "https://example.com/privacy.html"
                )
                .build(),
            AppConstants.SIGN_IN_REQUEST
        )
    }

    // Putting fetched and authenticated user to the preferences (which helps us later to reduce numbers of http calls)
    @JvmStatic
    fun putAccDetailsIntoStorage(name: String?, email: String?, urlImage: String?, context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.apply {
            putString("name", name)
            putString("email", email)
            putString("imageUrl", urlImage)
        }.apply()
    }

    // Putting fetched and authenticated user to the preferences (which helps us later to reduce numbers of http calls)
    @JvmStatic
    fun putFlagIntoStorage(showReserveButton: Boolean, context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("showReservation", showReserveButton).apply()
    }

    // Putting fetched and authenticated user to the preferences (which helps us later to reduce numbers of http calls)
    @JvmStatic
    fun putFlagRatingIntoStorage(showRatingButton: Boolean, context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean("showRating", showRatingButton).apply()
    }
}