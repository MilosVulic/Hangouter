package com.metropolitan.hangouterproject.settings

import com.metropolitan.hangouterproject.MainActivity
import com.metropolitan.hangouterproject.account.AccountActivity
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.events.EventAddingFragment
import com.metropolitan.hangouterproject.events.EventAddingSecondFragment
import com.metropolitan.hangouterproject.events.EventAddingThirdFragment
import com.metropolitan.hangouterproject.adapters.RecyclerViewEventsAdapter
import com.metropolitan.hangouterproject.event_details.EventDetailsActivity
import com.metropolitan.hangouterproject.events.EventAddingActivity
import com.metropolitan.hangouterproject.reservations.MyReservationsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SettingsModule::class])
interface SettingsComponent {

    fun inject(myApplication: MyApplication)

    fun inject(mainActivity: MainActivity)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(settingsActivity: SettingsActivity)

    fun inject(localizationHelper: LocalizationHelper)

    fun inject(eventAddingFragment: EventAddingFragment)

    fun inject(eventAddingSecondFragment: EventAddingSecondFragment)

    fun inject(eventAddingThirdFragment: EventAddingThirdFragment)

    fun inject(recyclerViewEventsAdapter: RecyclerViewEventsAdapter)

    fun inject(eventAddingActivity: EventAddingActivity)

    fun inject(accountActivity: AccountActivity)

    fun inject(eventDetailsActivity: EventDetailsActivity)

    fun inject(myReservationsActivity: MyReservationsActivity)
}
