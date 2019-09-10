package com.metropolitan.hangouterproject.events

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.firebase.FirebaseCallsImp
import com.metropolitan.hangouterproject.firebase.FirebaseDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_view_fragment.view.*
import javax.inject.Inject
import javax.inject.Named

class HomeFragment : Fragment() {

    @Inject
    @field:Named("events")
    lateinit var databaseReference: DatabaseReference

    @Inject
    @field:Named("reservations")
    lateinit var databaseReferenceReservation: DatabaseReference

    @Inject
    @field:Named("eventRatings")
    lateinit var databaseReferenceRatings: DatabaseReference

    private var mLayoutManager: RecyclerView.LayoutManager? = null

    companion object {
        lateinit var app: MyApplication
        lateinit var referenceDB: DatabaseReference
        lateinit var referenceDBReservations: DatabaseReference
        lateinit var referenceDBRatings: DatabaseReference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as MyApplication).getFirebaseComponent().inject(this)
        app = activity?.application as MyApplication
        referenceDB = databaseReference
        referenceDBReservations = databaseReferenceReservation
        referenceDBRatings = databaseReferenceRatings
        val view = inflater.inflate(R.layout.card_view_fragment, container, false)
        view.my_recycler_view.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(context)
        view.my_recycler_view.layoutManager = mLayoutManager
        showUpcomingEvents(view.my_recycler_view)
        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.nav_view?.menu?.getItem(0)?.isChecked = true
    }

    private fun showUpcomingEvents(recyclerView: RecyclerView) {
        val firebaseCallsImp = FirebaseCallsImp()
        FirebaseDelegate(firebaseCallsImp).getAllUpcomingEvents(databaseReference, context, recyclerView)
            FirebaseDelegate(firebaseCallsImp).getAllRatings(
                databaseReferenceRatings,
                context
            )
        }
}
