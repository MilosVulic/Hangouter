package com.metropolitan.hangouterproject.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

class TabAdapter(fm: FragmentManager, numOfTabs: Int, private val fragmenti: Array<Fragment>) :
    FragmentStatePagerAdapter(fm) {

    private val numberOfTabs = numOfTabs

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> fragmenti[0]
            1 -> fragmenti[1]
            else -> fragmenti[0]
        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }
}