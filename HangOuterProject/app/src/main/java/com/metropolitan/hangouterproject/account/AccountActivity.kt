package com.metropolitan.hangouterproject.account

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.AppConstants
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.events.GlideApp
import com.metropolitan.hangouterproject.utils.*
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.app_bar_account.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import javax.inject.Inject


class AccountActivity : AppCompatActivity(), View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).getSettingsComponent().inject(this)
        UtilsNavigationStyle.setApplicationTheme(this, preferencesSettings.getBoolean("themeKey", false))
        setContentView(R.layout.activity_account)
        UtilsUIElementsStyle.setCardViewBackgroundStyle(
            preferencesSettings.getBoolean("themeKey", false),
            this,
            card_viewAccountEmail,
            card_viewNameAccount,
            card_viewAvatarAccount,
            card_viewAuthenticateAccount
        )
        setButtonStyle()
        setupNavigation()
        UtilsNavigationStyle.setStyleNavigationView(this, preferencesSettings.getBoolean("themeKey", false), nav_view)
        getUserInfo()
        setButtonText()
        buttonAuthenticate.setOnClickListener(this)
    }

    // This shows hamburger menu at the toolbar
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    // Handling navigation drawer consistence during the orientation change or something similar
    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    // Handling on click events in drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        UtilsNavigationFunctions.onNavigationItemSelected(item, this, drawer_layout)
        return true
    }

    // Closes drawer when back button is pressed instead of whole application
    override fun onBackPressed() {
        when (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            true -> drawer_layout.closeDrawer(GravityCompat.START)
            else -> super.onBackPressed()
        }
    }

    // Setting selected item in the navigation drawer menu
    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_item_account)
    }

    // Setting action on button click
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonAuthenticate -> signInOut()
        }
    }

    // Fetchig result from loggin activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.SIGN_IN_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                buttonAuthenticate.text = "Sign out"
                val user = FirebaseAuth.getInstance().currentUser
                UtilsAuthentication.putAccDetailsIntoStorage(
                    user?.displayName,
                    user?.email,
                    user?.providerData?.get(0)?.photoUrl.toString() + "?height=1000&width=1000",
                    this
                )
                getUserInfo()
            } else {
                //TODO
            }
        }
    }

    // Setting style to buttons
    private fun setButtonStyle() {
        UtilsButtonStyle.setButtonsStyle(
            this,
            preferencesSettings.getBoolean("themeKey", false),
            buttonAuthenticate
        )
    }

    // Method for setting user acc info if he is logged in
    private fun getUserInfo() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val username = preferences.getString("name", "cc")
        if (username != "cc") {
            textViewName.text = username
            textViewEmail.text = preferences.getString("email", "email@")
            GlideApp.with(this)
                .load(preferences.getString("imageUrl", "imageDefault"))
                .into(imageViewAvatar)
            setAccountDetailsOnNavigation(
                username,
                preferences.getString("email", "email@"),
                preferences.getString("imageUrl", "imageDefault")
            )
        }
    }

    // Method for log in and log out depending on users authentication at the moment
    private fun signInOut() {
        if (FirebaseAuth.getInstance().currentUser == null) {
            UtilsAuthentication.startSignInProccess(this)
        } else {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    setButtonText()
                    textViewName.text = ""
                    textViewEmail.text = ""
                    imageViewAvatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_avatar))
                }
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = preferences.edit()
            editor.remove("name").apply()
            resettingAccountDetailsInNavigation()
        }
    }

    // Setting button text (Sign in or sign out, depending on the current state)
    private fun setButtonText() {
        if (FirebaseAuth.getInstance().currentUser != null) buttonAuthenticate.text =
            "Sign out" else buttonAuthenticate.text = "Sign in"
    }

    // Setting up navigation drawer with listeners
    private fun setupNavigation() {
        setSupportActionBar(toolbar_main)
        toggle = UtilsNavigationFunctions.setUpNavigation(this, drawer_layout, toolbar_main)
        nav_view.setNavigationItemSelectedListener(this)
    }

    // Setting acc info in navigation view
    private fun setAccountDetailsOnNavigation(name: String?, email: String?, urlImage: String?) {
        UtilsNavigationFunctions.setAccountInfoData(
            name, email, urlImage, this, nav_view.getHeaderView(0).nav_header_name,
            nav_view.getHeaderView(0).nav_header_email,
            nav_view.getHeaderView(0).nav_header_imageView
        )
    }

    // Resetting acc info in the navigation to his default parameters
    private fun resettingAccountDetailsInNavigation() {
        UtilsNavigationFunctions.setAccountInfoData(
            ContextCompat.getDrawable(this, R.drawable.ic_avatar), this, nav_view.getHeaderView(0).nav_header_name,
            nav_view.getHeaderView(0).nav_header_email,
            nav_view.getHeaderView(0).nav_header_imageView
        )
    }
}

