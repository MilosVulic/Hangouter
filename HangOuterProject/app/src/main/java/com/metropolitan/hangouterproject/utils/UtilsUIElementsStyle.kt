package com.metropolitan.hangouterproject.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.widget.EditText
import android.widget.ImageView
import com.metropolitan.hangouterproject.R

object UtilsUIElementsStyle {

    // Settings icons color
    @JvmStatic
    fun setIconColors(context: Context, newValue: Boolean, vararg icons: ImageView) {
        when (newValue) {
            true -> setIconColorHelper(context, R.color.backgroundOrangeTheme, icons)
            else -> setIconColorHelper(context, R.color.backgroundColorDarkTheme, icons)
        }
    }

    // Helper method for setting icons color
    private fun setIconColorHelper(context: Context, color: Int, icons: Array<out ImageView>) {
        for (i in icons.indices) {
            icons[i].setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }

    // Setting color style to description ui element
    @JvmStatic
    fun setDescriptionBorderStyle(newValue: Boolean, editText: EditText) {
        when (newValue) {
            true -> setDescriptionBorderStyleHelper(R.drawable.description_border_dark_theme, editText)
            else -> setDescriptionBorderStyleHelper(R.drawable.description_border_orange_theme, editText)
        }
    }

    // Helper method for setting style to description ui element
    private fun setDescriptionBorderStyleHelper(borderColor: Int, descriptionField: EditText) {
        descriptionField.setBackgroundResource(borderColor)
    }

    // Setting background color to the cardViews
    @JvmStatic
    fun setCardViewBackgroundStyle(newValue: Boolean, context: Context, vararg cardViews: CardView) {
        when (newValue) {
            true -> setCardViewBackgroundThemeHelper(context, R.color.cardViewBack, cardViews)
        }
    }

    // Helper method for setting background color to cardViews
    private fun setCardViewBackgroundThemeHelper(context: Context, colorId: Int, cardViews: Array<out CardView>) {
        for (card in cardViews.indices) {
            cardViews[card].setBackgroundColor(ContextCompat.getColor(context, colorId))
        }
    }
}