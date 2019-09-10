package com.metropolitan.hangouterproject.events

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.models.Event
import com.metropolitan.hangouterproject.models.Location
import com.metropolitan.hangouterproject.utils.UtilsButtonStyle
import com.metropolitan.hangouterproject.utils.UtilsDialogs
import com.metropolitan.hangouterproject.utils.UtilsUIElementsStyle
import kotlinx.android.synthetic.main.fragment_event_adding_third.*
import kotlinx.android.synthetic.main.fragment_event_adding_third.view.*
import java.sql.Timestamp
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class EventAddingThirdFragment : Fragment(), View.OnClickListener {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    private lateinit var startDateTime: String
    private lateinit var endDateTime: String
    var RC_SIGN_IN = 10003

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity?.application as MyApplication).getSettingsComponent().inject(this)
        val view = inflater.inflate(R.layout.fragment_event_adding_third, container, false)
        setButtonStyle(view)
        setIconStyle(view)
        view.buttonStartDate.setOnClickListener(this)
        view.buttonEndDate.setOnClickListener(this)
        view.buttonAddEvent.setOnClickListener(this)
        context?.let { setSpinner(it, view) }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
            } else {
            }
        }
    }

    // Setting style to buttons
    private fun setButtonStyle(view: View) {
        context?.let {
            UtilsButtonStyle.setButtonsStyle(
                it,
                preferencesSettings.getBoolean("themeKey", false),
                view.buttonStartDate,
                view.buttonEndDate,
                view.buttonAddEvent
            )
        }
    }

    // Setting style icons
    private fun setIconStyle(view: View) {
        context?.let {
            UtilsUIElementsStyle.setIconColors(
                it,
                preferencesSettings.getBoolean("themeKey", false),
                view.imageViewPrice,
                view.imageViewTimeZone
            )
        }
    }

    // Setting event listeners for button clicks (Opening date & time pickers)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonStartDate -> setDateTimePicker(textViewStartDate, true)
            R.id.buttonEndDate -> setDateTimePicker(textViewEndDate, false)
            R.id.buttonAddEvent -> addEvent()
        }
    }

    // Setting date and time picker using 3-rd party library
    private fun setDateTimePicker(textView: TextView, flag: Boolean) {
        SingleDateAndTimePickerDialog.Builder(context)
            .bottomSheet()
            .curved()
            .mustBeOnFuture()
            .defaultDate(Date(System.currentTimeMillis()))
            .title("Date and time")
            .listener { textView.text = formatting(it.toString(), flag) }
            .build().display()
    }

    // Formatting the picked date and time
    private fun formatting(dateTime: String, flag: Boolean): String {
        val year = dateTime.substring(dateTime.length - 4, dateTime.length)
        val day = dateTime.substring(8, 10)
        val month = getMonth(dateTime.substring(4, 7))
        when (flag) {
            true -> startDateTime = "$year-$month-$day " + dateTime.substring(11, 19)
            else -> endDateTime = "$year-$month-$day " + dateTime.substring(11, 19)
        }
        return "$year/$month/$day  " + dateTime.substring(11, 16)
    }

    // Converting text of month to numeric text
    private fun getMonth(value: String): String {
        return when (value) {
            "Jan" -> "01"
            "Feb" -> "02"
            "Mar" -> "03"
            "Apr" -> "04"
            "May" -> "05"
            "Jun" -> "06"
            "Jul" -> "07"
            "Aug" -> "08"
            "Sep" -> "09"
            "Oct" -> "10"
            "Nov" -> "11"
            "Dec" -> "12"
            else -> ""
        }
    }

    // Setting timezones to spinner
    private fun setSpinner(context: Context, view: View) {
        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, timeZones())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.spinnerTimeZones.adapter = adapter
    }

    // Getting and formatting timezones to an array
    private fun timeZones(): ArrayList<String> {
        val timeZonesList = TimeZone.getAvailableIDs()
        val finalTimeZonesList = ArrayList<String>()
        val tempListTimeZones = ArrayList<String>()
        for (i in timeZonesList.indices) {
            if (!tempListTimeZones.contains(TimeZone.getTimeZone(timeZonesList[i]).displayName)) {
                var time = (TimeZone.getTimeZone(timeZonesList[i]).rawOffset / 3600000).toString() + ":00"
                if (time.subSequence(0, 1) != "-") {
                    time = "+$time"
                }
                tempListTimeZones.add(TimeZone.getTimeZone(timeZonesList[i]).displayName)
                finalTimeZonesList.add(TimeZone.getTimeZone(timeZonesList[i]).displayName + "  " + time)
            }
        }
        return finalTimeZonesList
    }

    // Uploading image to the firebase storage
    private fun uploadImage(
        imageUri: Uri,
        storageReference: StorageReference,
        context: Context,
        dialog: AlertDialog,
        imageName: String
    ) {
        val imageRef: StorageReference = storageReference.child("images/$imageName")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                dialog.dismiss()
                Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = ((100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount))
                dialog.setMessage("Uploaded " + progress.toInt() + "%")
            }
    }

    // Adding event to the firebase
    private fun addEvent() {

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val event = Event(
                arguments?.getString("eventName")!!,
                arguments?.getString("phoneNumber")!!,
                arguments?.getInt("capacity")!!,
                Location(
                    arguments?.getString("latitude")!!,
                    arguments?.getString("longitude")!!,
                    arguments?.getString("address")!!
                ),
                arguments?.getString("imageName")!!,
                arguments?.getString("description")!!,
                Timestamp.valueOf(startDateTime).time,
                Timestamp.valueOf(endDateTime).time,
                editTextPrice.text.toString().toInt(),
                spinnerTimeZones.selectedItem.toString()
            )

            HomeFragment.referenceDB.push().setValue(event)
            val dialog = context?.let { UtilsDialogs.setProgressDialog(preferencesSettings, it) }
            context?.let { context ->
                dialog?.let { dialog ->
                    uploadImage(
                        arguments?.getParcelable("imageUri")!!, FirebaseStorage.getInstance().reference, context,
                        dialog, arguments?.getString("imageName")!!
                    )
                }
            }
            user.let {
                val name = user.displayName
                val email = user.email
                val photoUrl = user.photoUrl
                val emailVerified = user.isEmailVerified
                val uid = user.uid
                Log.d("tagic","users id is " + uid)
            }
        } else {
            val providers =
                arrayListOf(AuthUI.IdpConfig.FacebookBuilder().build(), AuthUI.IdpConfig.TwitterBuilder().build(), AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(true)
                    .setAvailableProviders(providers)
                    .setLogo(R.mipmap.my_app) // Set logo drawable
                    .setTheme(R.style.BlackTheme)
                    .setTosAndPrivacyPolicyUrls(
                        "https://example.com/terms.html",
                        "https://example.com/privacy.html"
                    )
                    .build(),
                RC_SIGN_IN
            )
        }
    }
}
