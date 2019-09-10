package com.metropolitan.hangouterproject.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import com.metropolitan.hangouterproject.app_module.MyApplication
import java.util.*
import javax.inject.Inject

open class LocalizationHelper(base: Context) : ContextWrapper(base) {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    fun wrap(context: Context, application: MyApplication): ContextWrapper {
        application.getSettingsComponent().inject(this)
        val language = Locale(preferencesSettings.getString("languageKey", "no selection")!!).language
        val config = context.resources.configuration
        val sysLocale: Locale?

        sysLocale = when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            true -> getSystemLocale(config)
            else -> getSystemLocaleLegacy(config)
        }

        if (language != "" && sysLocale.language != language) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                true -> setSystemLocale(config, locale)
                else -> setSystemLocaleLegacy(config, locale)
            }
        }
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        return LocalizationHelper(context)
    }


    @Suppress("DEPRECATION")
    private fun getSystemLocaleLegacy(config: Configuration): Locale {
        return config.locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun getSystemLocale(config: Configuration): Locale {
        return config.locales.get(0)
    }

    @Suppress("DEPRECATION")
    private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
        config.locale = locale
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun setSystemLocale(config: Configuration, locale: Locale) {
        config.setLocale(locale)
    }
}