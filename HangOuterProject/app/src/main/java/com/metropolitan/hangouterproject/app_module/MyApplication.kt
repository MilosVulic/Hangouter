package com.metropolitan.hangouterproject.app_module

import android.app.Application
import com.metropolitan.hangouterproject.firebase.DaggerFirebaseComponent
import com.metropolitan.hangouterproject.firebase.FirebaseComponent
import com.metropolitan.hangouterproject.firebase.FirebaseModule
import com.metropolitan.hangouterproject.settings.DaggerSettingsComponent
import com.metropolitan.hangouterproject.settings.SettingsComponent
import com.metropolitan.hangouterproject.settings.SettingsModule

class MyApplication : Application() {

    private lateinit var settingsComponent: SettingsComponent
    private lateinit var firebaseComponent: FirebaseComponent

    override fun onCreate() {
        super.onCreate()
        settingsComponent = DaggerSettingsComponent.builder().settingsModule(SettingsModule(this)).build()
        settingsComponent.inject(this)
        firebaseComponent = DaggerFirebaseComponent.builder().firebaseModule(FirebaseModule(this)).build()
        firebaseComponent.inject(this)
    }

    fun getSettingsComponent(): SettingsComponent {
        return settingsComponent
    }

    fun getFirebaseComponent(): FirebaseComponent {
        return firebaseComponent
    }
}