package com.metropolitan.hangouterproject.events

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.AppConstants
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.maps.MapsActivity
import com.metropolitan.hangouterproject.utils.UtilsButtonStyle
import com.metropolitan.hangouterproject.utils.UtilsUIElementsStyle
import kotlinx.android.synthetic.main.fragment_event_adding.*
import kotlinx.android.synthetic.main.fragment_event_adding.view.*
import javax.inject.Inject


class EventAddingFragment : Fragment(), View.OnClickListener, View.OnFocusChangeListener {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    private lateinit var latitude: String
    private lateinit var longitude: String

    private val quickPermissionsOption = QuickPermissionsOptions(permissionsDeniedMethod = { editTextLocation.clearFocus() })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as MyApplication).getSettingsComponent().inject(this)
        val view = inflater.inflate(R.layout.fragment_event_adding, container, false)
        val theme = preferencesSettings.getBoolean("themeKey", false)
        setIconStyle(view, theme)
        context?.let { context -> UtilsUIElementsStyle.setCardViewBackgroundStyle(theme, context, view.card_view, view.card_view1, view.card_view2, view.card_view3 ) }
        setButtonStyle(view)
        view.buttonContinue.setOnClickListener(this)
        view.editTextLocation.onFocusChangeListener = this
        return view
    }

    // Replacing fragments (EventAddingSecondFragment is the new one)
    override fun onClick(v: View?) {
        val fragment = EventAddingSecondFragment()
        val bundle = Bundle().apply {
            putString("eventName", editTextEventNameDetails.text.toString())
            putString("phoneNumber", editTextPhoneNumber.text.toString())
            putInt("capacity", editTextCapacity.text.toString().toInt())
            putString("address", editTextLocation.text.toString())
            putString("latitude", latitude)
            putString("longitude", longitude)
        }
        fragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.constraintLayoutEventAdding, fragment)?.addToBackStack(null)?.commit()
    }

    // Handling event when user focuses editTextLocation ui element
    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (hasFocus)
            openMaps()
    }

    // Getting data from maps activity, its used to obtain events location
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            editTextLocation.setText(data?.getStringExtra("location"))
            latitude = data?.getStringExtra("latitude").toString()
            longitude = data?.getStringExtra("longitude").toString()
            editTextLocation.clearFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        editTextLocation.clearFocus()
    }

    // Setting style to buttons
    private fun setButtonStyle(view: View) {
        context?.let {
            UtilsButtonStyle.setButtonsStyle(
                it,
                preferencesSettings.getBoolean("themeKey", false),
                view.buttonContinue
            )
        }
    }

    // Setting style icons
    private fun setIconStyle(view : View, theme: Boolean) {
        context?.let { UtilsUIElementsStyle.setIconColors(it,theme,view.imageViewEventTitle,view.imageViewCapacity,view.imageViewLocation,view.imageViewPhoneNumber) }
    }

    // Helper method for opening google maps to catch desired location
    private fun openMaps() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, options = quickPermissionsOption) {
            startActivityForResult(Intent(context, MapsActivity::class.java), AppConstants.LOCATION_REQUEST)
        }
}
