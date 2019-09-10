package com.metropolitan.hangouterproject.reservations

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.firebase.FirebaseCallsImp
import com.metropolitan.hangouterproject.firebase.FirebaseDelegate
import kotlinx.android.synthetic.main.card_view_fragment.view.*
import javax.inject.Inject
import javax.inject.Named

class MyReservationsFragment : Fragment() {

    @Inject
    @field:Named("events")
    lateinit var databaseReference: DatabaseReference

    @Inject
    @field:Named("reservations")
    lateinit var databaseReferenceReservation: DatabaseReference

    private var mLayoutManager: RecyclerView.LayoutManager? = null

    companion object {
        lateinit var app: MyApplication
        lateinit var referenceDB: DatabaseReference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as MyApplication).getFirebaseComponent().inject(this)
        app = activity?.application as MyApplication
        referenceDB = databaseReference
        val view = inflater.inflate(R.layout.card_view_fragment, container, false)
        mLayoutManager = LinearLayoutManager(context)
        view.my_recycler_view.layoutManager = mLayoutManager
        showMyReservations(view.my_recycler_view)
        return view
    }

    private fun showMyReservations(recyclerView: RecyclerView) {
        val firebaseCallsImp = FirebaseCallsImp()
        FirebaseDelegate(firebaseCallsImp).getMyReservations(databaseReference, databaseReferenceReservation, context, recyclerView)
    }
}
