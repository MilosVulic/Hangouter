package com.metropolitan.hangouterproject.utils

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.widget.Button
import com.metropolitan.hangouterproject.R

object UtilsButtonStyle {

    // Setting style to buttons
    @JvmStatic
    fun setButtonsStyle(context: Context, newValue: Boolean, vararg buttons: Button) {
        when (newValue) {
            true -> setButtonsStyleHelper(context, R.color.colorAccentDarkTheme, buttons)
            else -> setButtonsStyleHelper(context, R.color.colorAccentOrangeTheme, buttons)
        }
    }

    // Helper method for setting style to buttons
    private fun setButtonsStyleHelper(context: Context, buttonColor: Int, buttons: Array<out Button>) {
        for (button in buttons.indices) {
            buttons[button].setTextColor(ContextCompat.getColor(context, R.color.backgroundOrangeTheme))
            buttons[button].setBackgroundResource(buttonColor)
        }
    }

    // Setting style to complement buttons
    @JvmStatic
    fun setComplementButtonsStyle(context: Context, newValue: Boolean, vararg buttons: Button) {
        when (newValue) {
            true -> setComplementButtonsStyleHelper(
                context,
                R.color.colorAccentDarkTheme,
                R.drawable.button_complement_dark_theme,
                buttons
            )
            else -> setComplementButtonsStyleHelper(
                context,
                R.color.colorAccentOrangeTheme,
                R.drawable.button_complement_orange_theme,
                buttons
            )
        }
    }

    // Helper method for setting style to complement buttons
    private fun setComplementButtonsStyleHelper(
        context: Context,
        buttonColor: Int,
        buttonBorderColor: Int,
        buttons: Array<out Button>
    ) {
        for (button in buttons.indices) {
            buttons[button].setTextColor(ContextCompat.getColor(context, buttonColor))
            buttons[button].setBackgroundResource(buttonBorderColor)
        }
    }


    // Setting style to buttons in the cardView
    @JvmStatic
    fun setCardViewButtonsStyle(enabled: Boolean, context: Context, cardView: CardView, button1 : Button, button2: Button) {
        if (enabled) {
            cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.cardViewBackgroundDarkColor))
            setCardViewButtonsStyle(context, R.drawable.ripple_effect_dark_theme, R.color.colorAccentDarkTheme, button1,button2)
        } else {
            setCardViewButtonsStyle(context, R.drawable.ripple_effect_light_theme, R.color.colorAccentOrangeTheme, button1,button2)
        }
    }

    // Helper method for setting style to complement buttons for cardView
    private fun setCardViewButtonsStyle(context: Context, backgroundRes: Int, colorRes: Int, vararg buttons: Button){
        for (button in buttons.indices) {
            buttons[button].setTextColor(ContextCompat.getColor(context, colorRes))
            buttons[button].setBackgroundResource(backgroundRes)
        }
    }
}