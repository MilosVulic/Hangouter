package com.metropolitan.hangouterproject.events

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.firebase.FirebaseCallsImp
import com.metropolitan.hangouterproject.firebase.FirebaseDelegate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_view_fragment.view.*

class PastEventsFragment : Fragment() {

    private var mLayoutManager: RecyclerView.LayoutManager? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.card_view_fragment, container, false)
        view.my_recycler_view.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(context)
        view.my_recycler_view.layoutManager = mLayoutManager
        showPastEvents(view.my_recycler_view)
        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.nav_view?.menu?.getItem(0)?.isChecked = true
    }

    private fun showPastEvents(recyclerView: RecyclerView) {
        val firebaseCallsImp = FirebaseCallsImp()
        FirebaseDelegate(firebaseCallsImp).getPastEvents(HomeFragment.referenceDB, context, recyclerView)
    }
}