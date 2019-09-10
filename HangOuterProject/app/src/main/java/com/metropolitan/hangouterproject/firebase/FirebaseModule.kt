package com.metropolitan.hangouterproject.firebase

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.metropolitan.hangouterproject.app_module.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class FirebaseModule(private val myApplication: MyApplication) {

    @Provides
    @Named("events")
    fun getDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("events")
    }

    @Provides
    @Named("reservations")
    fun getDatabaseReferenceReservations(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("reservations")
    }

    @Provides
    @Named("eventRatings")
    fun getDatabaseReferenceRatings(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("eventRatings")
    }

    @Provides
    fun provideContext(): Context {
        return myApplication
    }
}
