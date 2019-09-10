package com.metropolitan.hangouterproject.settings

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.metropolitan.hangouterproject.app_module.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SettingsModule(private val myApplication: MyApplication) {

    @Provides
    @Singleton
    fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    fun provideContext(): Context {
        return myApplication
    }
}