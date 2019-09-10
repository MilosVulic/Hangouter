package com.metropolitan.hangouterproject.firebase

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

interface FirebaseCalls {

    fun getAllUpcomingEvents(databaseReference: DatabaseReference, context: Context?, recyclerView: RecyclerView)

    fun getPastEvents(databaseReference: DatabaseReference, context: Context?, recyclerView: RecyclerView)

    fun getEventId(databaseReference: DatabaseReference, context: Context?, imageUriString: String)

    fun getMyReservations(databaseReference: DatabaseReference, databaseReferenceReservations: DatabaseReference, context: Context?, recyclerView: RecyclerView)

    fun updateEventCapacity(databaseReference: DatabaseReference, context: Context?, oldValue: Int, eventKey: String)

    fun getAllReservations(databaseReference: DatabaseReference, context: Context?)

    fun updateEventRates(databaseReference: DatabaseReference, context: Context?, eventVotes: Int, eventVote: Int, eventRatings: Double, eventRate: Double, eventKey: String)

    fun getRatingsByUser(databaseReference: DatabaseReference, context: Context?, userId: String)

    fun updateRating(databaseReference: DatabaseReference, context: Context?, ratingId: String, newRate: Double)

    fun getAllEvents(databaseReference: DatabaseReference, context: Context?)

    fun getAllRatings(databaseReference: DatabaseReference, context: Context?)

}