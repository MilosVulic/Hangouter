package com.metropolitan.hangouterproject.firebase

import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.events.HomeFragment
import com.metropolitan.hangouterproject.reservations.MyReservationsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseModule::class])
interface FirebaseComponent {

    fun inject(myApplication: MyApplication)

    fun inject(homeFragment: HomeFragment)

    fun inject(myReservationsFragment: MyReservationsFragment)
}