package com.metropolitan.hangouterproject.firebase

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.metropolitan.hangouterproject.adapters.RecyclerViewEventsAdapter
import com.metropolitan.hangouterproject.models.Event
import com.metropolitan.hangouterproject.models.Rating
import com.metropolitan.hangouterproject.models.Reservation
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FirebaseCallsImp : FirebaseCalls {

    private var mAdapter: RecyclerView.Adapter<*>? = null

    companion object {
        var eventKey: String? = null
        var allReservationsList: ArrayList<Reservation>? = null
        var ratingsIdByUser: ArrayList<String>? = null
        var allRatingsByCurrentUser: ArrayList<Rating>? = null
        var eventKeys: HashMap<String, String>? = null
        var ratingsIds: ArrayList<String>? = null
        var allRatings: ArrayList<Rating>? = null
    }

    override fun getAllUpcomingEvents(
        databaseReference: DatabaseReference,
        context: Context?,
        recyclerView: RecyclerView
    ) {
        val query = databaseReference.orderByChild("startDate").startAt(Calendar.getInstance().timeInMillis.toDouble())
        val results = ArrayList<Event>()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { results.add(it.getValue(Event::class.java)!!) }
                mAdapter =
                    context?.let { context -> RecyclerViewEventsAdapter(approximateEventUpcoming(results), context) }
                recyclerView.adapter = mAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getPastEvents(databaseReference: DatabaseReference, context: Context?, recyclerView: RecyclerView) {
        val query = databaseReference.orderByChild("startDate")
            .startAt(Calendar.getInstance().timeInMillis.toDouble() - 604800000)
            .endAt(Calendar.getInstance().timeInMillis.toDouble())
        val results = ArrayList<Event>()


        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    results.add(it.getValue(Event::class.java)!!)
                }

                val queryy = databaseReference.orderByChild("startDate").startAt(Calendar.getInstance().timeInMillis.toDouble())
                val resultss = ArrayList<Event>()
                val finalFinalEvents = ArrayList<Event>()

                queryy.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach {
                            resultss.add(it.getValue(Event::class.java)!!)
                        }

                        val finalEvents = resultss
                        val aproximatedList = approximateEventUpcoming(finalEvents)
                        for (i in finalEvents.indices) {
                            if (finalEvents[i] !in aproximatedList) {
                                finalFinalEvents.add(finalEvents[i])
                            }
                        }
                        val listaFinal = results.plus(finalFinalEvents) as ArrayList<Event>
                        val unDuplicatedArray = listaFinal.reversed().distinctBy { it.imageName } as ArrayList<Event>

                        mAdapter = context?.let { context ->
                            RecyclerViewEventsAdapter(
                                ArrayList(unDuplicatedArray.reversed()),
                                context
                            )
                        }
                        recyclerView.adapter = mAdapter
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getEventId(databaseReference: DatabaseReference, context: Context?, imageUriString: String) {
        val query = databaseReference.orderByChild("imageName").equalTo(imageUriString)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    eventKey = it.key
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun getMyReservations(
        databaseReference: DatabaseReference,
        databaseReferenceReservations: DatabaseReference,
        context: Context?,
        recyclerView: RecyclerView
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val queryFindReservations = databaseReferenceReservations.orderByChild("userId").equalTo(user.uid)
            val reservations = ArrayList<Reservation>()
            queryFindReservations.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { reservations.add(it.getValue(Reservation::class.java)!!) }
                    //  Log.d("tagic","provera lista rezervacija je " + reservations.size)

                    val queryUpcomingEvents = databaseReference.orderByChild("startDate")
                        .startAt(Calendar.getInstance().timeInMillis.toDouble())
                    val results = ArrayList<Event>()
                    val eventsIdsMap: HashMap<String, String> = HashMap()
                    val eventIdsMapFinal: HashMap<String, String> = HashMap()
                    queryUpcomingEvents.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dataSnapshot.children.forEach {
                                results.add(it.getValue(Event::class.java)!!)
                                eventsIdsMap[it.key.toString()] = it.child("imageName").value.toString()
                            }

                            val aproximatedEvents = approximateEventUpcoming(results)
                            //         Log.d("tagic","provera mapa ne aproximirana " + eventsIdsMap.size)
                            //        Log.d("tagic","provera lista eventa aproximirana je " + aproximatedEvents.size)
                            for ((k, v) in eventsIdsMap) {
                                for (j in aproximatedEvents.indices) {
                                    if (aproximatedEvents[j].imageName == v) {
                                        eventIdsMapFinal[k] = v
                                    }
                                }
                            }

                            //    Log.d("tagic","provera mapa aproximirana " + eventIdsMapFinal.size)

                            val finalListOfReservedEvents = ArrayList<Event>()
                            val temporaryList = ArrayList<String>()  // imageName unique list
                            for (i in reservations.indices) {
                                for ((k, v) in eventIdsMapFinal) {
                                    if (reservations[i].eventId == k) {
                                        temporaryList.add(v)
                                    }
                                }
                            }

                            //     Log.d("tagic","provera lista ove temporary " + temporaryList.size)

                            for (i in aproximatedEvents.indices) {
                                for (j in temporaryList.indices) {
                                    if (aproximatedEvents[i].imageName == temporaryList[j]) {
                                        finalListOfReservedEvents.add(aproximatedEvents[i])
                                    }
                                }
                            }
                            //      Log.d("tagic","provera final listice " + finalListOfReservedEvents .size)
                            mAdapter = context?.let { context ->
                                RecyclerViewEventsAdapter(
                                    finalListOfReservedEvents,
                                    context
                                )
                            }
                            recyclerView.adapter = mAdapter
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
                        }
                    })
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun updateEventCapacity(
        databaseReference: DatabaseReference,
        context: Context?,
        oldValue: Int,
        eventKey: String
    ) {
        val ref = databaseReference.child(eventKey)
        val taskMap = HashMap<String, Any>()
        taskMap["capacityReserved"] = oldValue + 1
        ref.updateChildren(taskMap)
    }

    private fun approximateEventUpcoming(results: ArrayList<Event>): ArrayList<Event> {
        val finalEvents = ArrayList<Event>()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
        val localTime = SimpleDateFormat("Z", Locale.getDefault()).format(calendar.time)
        val offsetLocale = localTime.substring(0, 3)

        results.forEach {
            val offset = it.timeZone.substring(it.timeZone.length - 6, it.timeZone.length - 3).trim()
            val approximate = (offset.toInt() - offsetLocale.toInt()) * 3600000
            if ((it.startDate - (Calendar.getInstance().timeInMillis + approximate)) > 0) {
                finalEvents.add(it)
            }
        }
        val unDuplicatedArray = finalEvents.reversed().distinctBy { it.imageName } as ArrayList<Event>
        return ArrayList(unDuplicatedArray.reversed())
    }

    override fun getAllReservations(databaseReference: DatabaseReference, context: Context?) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            val reservations = ArrayList<Reservation>()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { reservations.add(it.getValue(Reservation::class.java)!!) }
                allReservationsList = reservations
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun updateEventRates(
        databaseReference: DatabaseReference,
        context: Context?,
        eventVotes: Int,
        eventVote: Int,
        eventRatings: Double,
        eventRate: Double,
        eventKey: String
    ) {
        val ref = databaseReference.child(eventKey)
        val taskMap = HashMap<String, Any>()
        taskMap["totalVotes"] = eventVotes + eventVote
        taskMap["totalRating"] = eventRatings + eventRate
        ref.updateChildren(taskMap)
    }


    override fun getRatingsByUser(databaseReference: DatabaseReference, context: Context?, userId: String) {
        val query = databaseReference.orderByChild("userId").equalTo(userId)
        val ratings = ArrayList<Rating>()
        val ratingsId = ArrayList<String>()
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    ratings.add(it.getValue(Rating::class.java)!!)
                    ratingsId.add(it.key.toString())
                }
                allRatingsByCurrentUser?.clear()
                allRatingsByCurrentUser = ratings
                ratingsIdByUser = ratingsId
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Can't read data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun updateRating(databaseReference: DatabaseReference, context: Context?, ratingId: String, newRate: Double) {
        val ref = databaseReference.child(ratingId)
        val taskMap = HashMap<String, Any>()
        taskMap["rating"] = newRate
        ref.updateChildren(taskMap)
    }

    override fun getAllEvents(databaseReference: DatabaseReference, context: Context?) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            val events = ArrayList<Event>()
            val eventMap : HashMap<String, String>? = HashMap()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { events.add(it.getValue(Event::class.java)!!)
                    eventMap?.set(it.key.toString(), it.getValue(Event::class.java)!!.imageName)
                }
                Log.d("tagic", "Velicina je " + eventMap?.size)
                Log.d("tagic", "Velicina je " + events.size)
                eventKeys = eventMap
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun getAllRatings(databaseReference: DatabaseReference, context: Context?) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            val ratings = ArrayList<Rating>()
            val ratingsId = ArrayList<String>()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach {
                    ratings.add(it.getValue(Rating::class.java)!!)
                    ratingsId.add(it.key.toString())
                }
                allRatings = ratings
                ratingsIds = ratingsId
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}