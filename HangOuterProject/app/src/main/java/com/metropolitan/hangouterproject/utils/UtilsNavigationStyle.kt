package com.metropolitan.hangouterproject.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import com.metropolitan.hangouterproject.R

object UtilsNavigationStyle {


    // Setting app theme to activity
    @JvmStatic
    fun setApplicationTheme(context: Context, newValue: Boolean) {
        when (newValue) {
            true -> context.setTheme(R.style.BlackTheme)
            else -> context.setTheme(R.style.OrangeTheme)
        }
    }

    // Applying style to navigation view items
    @JvmStatic
    fun setStyleNavigationView(context: Context, newValue: Boolean, navigationView: NavigationView) {
        when (newValue) {
            true -> applyStyleThemeNavigationView(
                context,
                navigationView,
                R.drawable.selected_item_navigation_view_dark_theme,
                Color.parseColor("#008577"),
                Color.WHITE
            )
            else -> applyStyleThemeNavigationView(
                context,
                navigationView,
                R.drawable.selected_item_navigation_view_orange_theme,
                Color.parseColor("#f47100"),
                Color.BLACK
            )
        }
    }

    // Setting color attributes and background color of selected item (Navigation view)
    private fun applyStyleThemeNavigationView(
        context: Context,
        navigationView: NavigationView,
        drawableId: Int,
        vararg colors: Int
    ) {
        val colorStyle = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(-android.R.attr.state_checked)),
            intArrayOf(colors[0], colors[1])
        )
        navigationView.itemTextColor = colorStyle
        navigationView.itemIconTintList = colorStyle
        navigationView.itemBackground = ContextCompat.getDrawable(context, drawableId)
    }
}