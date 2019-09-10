package com.metropolitan.hangouterproject.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.metropolitan.hangouterproject.MainActivity
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.account.AccountActivity
import com.metropolitan.hangouterproject.events.EventAddingActivity
import com.metropolitan.hangouterproject.events.GlideApp
import com.metropolitan.hangouterproject.reservations.MyReservationsActivity
import com.metropolitan.hangouterproject.settings.SettingsActivity


object UtilsNavigationFunctions {

    @JvmStatic
    fun setUpNavigation(activity: Activity, drawerLayout: DrawerLayout, toolbar: android.support.v7.widget.Toolbar) : ActionBarDrawerToggle {
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toolbar.setTitleTextColor(Color.WHITE)
        return toggle
    }

    @JvmStatic
    fun onNavigationItemSelected(item: MenuItem, context: Context, drawerLayout: DrawerLayout) {

        Handler().postDelayed( {
        when (item.itemId) {
            R.id.nav_item_one -> context.startActivity(Intent(context, MainActivity::class.java))
            R.id.nav_item_two -> context.startActivity(Intent(context, EventAddingActivity::class.java))
            R.id.nav_item_my_reservations -> context.startActivity(Intent(context, MyReservationsActivity::class.java))
            R.id.nav_item_account -> context.startActivity(Intent(context, AccountActivity::class.java))
            R.id.nav_item_five -> sendUsFeedback(context)
            R.id.nav_item_seven -> context.startActivity(Intent(context, SettingsActivity::class.java))
        }}, 220)
        drawerLayout.closeDrawer(Gravity.START, true)
    }

    // Showing send us feedback activity
    private fun sendUsFeedback(context: Context) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("milos_vulic97@yahoo.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback (HangOuter app) ")
            putExtra(
                Intent.EXTRA_TEXT,
                "\n\n\n\n" + "Brand " + Build.BRAND + "\n" + " Version " + Build.VERSION.RELEASE + "\n" + " Api level " + Build.VERSION.SDK_INT + "\n" + " Firmware " + Build.DISPLAY
            )
        }
        context.startActivity(Intent.createChooser(emailIntent, "Send Feedback:"))
    }

    // Setting acc info in the navigation view
    @JvmStatic
    fun setAccountInfoData(name: String?, email: String?, urlImage: String?, context: Context, nameTextView : TextView, emailTextView : TextView, imageView: ImageView){
        nameTextView.text = name
        emailTextView.text = email
        GlideApp.with(context)
            .load(urlImage)
            .into(imageView)
    }

    // Setting acc info in navigation view to its default parameters
    @JvmStatic
    fun setAccountInfoData(resId: Drawable?, context: Context, nameTextView: TextView, emailTextView: TextView, imageView: ImageView){
        nameTextView.text = "Name Surname"
        emailTextView.text = "something55522@gmail.com"
        GlideApp.with(context)
            .load(resId)
            .into(imageView)
    }
}