package com.metropolitan.hangouterproject

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.FirebaseDatabase
import com.metropolitan.hangouterproject.adapters.TabAdapter
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.events.HomeFragment
import com.metropolitan.hangouterproject.events.PastEventsFragment
import com.metropolitan.hangouterproject.firebase.FirebaseCallsImp
import com.metropolitan.hangouterproject.firebase.FirebaseDelegate
import com.metropolitan.hangouterproject.utils.UtilsAuthentication
import com.metropolitan.hangouterproject.utils.UtilsNavigationFunctions
import com.metropolitan.hangouterproject.utils.UtilsNavigationStyle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    private lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).getSettingsComponent().inject(this)
        UtilsNavigationStyle.setApplicationTheme(this, preferencesSettings.getBoolean("themeKey", false))
        setContentView(R.layout.activity_main)
        setupNavigation()
        UtilsNavigationStyle.setStyleNavigationView(this, preferencesSettings.getBoolean("themeKey", false), nav_view)
        configureTabLayout()
        val firebaseCallsImp = FirebaseCallsImp()
        FirebaseDelegate(firebaseCallsImp).getAllEvents(FirebaseDatabase.getInstance().getReference("events"), context)
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

    // Setting up navigation drawer with listeners
    private fun setupNavigation() {
        setSupportActionBar(toolbar_main)
        toggle = UtilsNavigationFunctions.setUpNavigation(this, drawer_layout, toolbar_main)
        nav_view.setNavigationItemSelectedListener(this)
        setUpAccountInfoOnNavigation()
    }

    // Reading data from preferences in case that user is logged in and his account info data is saved in preferences
    private fun setUpAccountInfoOnNavigation() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val username = preferences.getString("name", "cc")
        if (username != "cc") {
            setAccountDetailsOnNavigation(
                username,
                preferences.getString("email", "email@"),
                preferences.getString("imageUrl", "imageDefault")
            )
        }
    }

    // Setting up tabs (upcoming events and past 7 days)
    private fun configureTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"))
        tabLayout.addTab(tabLayout.newTab().setText("Past 7 days"))
        pager.adapter = TabAdapter(supportFragmentManager, tabLayout.tabCount, arrayOf(HomeFragment(), PastEventsFragment()))
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        when(tabLayout.selectedTabPosition){0 -> {
            UtilsAuthentication.putFlagIntoStorage(true, context)
            UtilsAuthentication.putFlagRatingIntoStorage(false, context)
        } }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
                when(tab.text){
                    "Upcoming" -> {
                        UtilsAuthentication.putFlagIntoStorage(true, context)
                        UtilsAuthentication.putFlagRatingIntoStorage(false, context) }
                    "Past 7 days" -> {
                        UtilsAuthentication.putFlagIntoStorage(false, context)
                        UtilsAuthentication.putFlagRatingIntoStorage(true, context) }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    // Setting name, email and avatar in navigation drawer
    private fun setAccountDetailsOnNavigation(name: String?, email: String?, urlImage: String?) {
        UtilsNavigationFunctions.setAccountInfoData(
            name, email, urlImage, this, nav_view.getHeaderView(0).nav_header_name,
            nav_view.getHeaderView(0).nav_header_email,
            nav_view.getHeaderView(0).nav_header_imageView
        )
    }
}

