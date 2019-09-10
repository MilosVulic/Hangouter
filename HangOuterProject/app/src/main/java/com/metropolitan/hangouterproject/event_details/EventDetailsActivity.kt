package com.metropolitan.hangouterproject.event_details

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.events.GlideApp
import com.metropolitan.hangouterproject.events.HomeFragment
import com.metropolitan.hangouterproject.firebase.FirebaseCallsImp
import com.metropolitan.hangouterproject.firebase.FirebaseDelegate
import com.metropolitan.hangouterproject.models.Rating
import com.metropolitan.hangouterproject.models.Reservation
import com.metropolitan.hangouterproject.utils.UtilsButtonStyle
import com.metropolitan.hangouterproject.utils.UtilsNavigationStyle
import com.metropolitan.hangouterproject.utils.UtilsUIElementsStyle
import kotlinx.android.synthetic.main.activity_event_details.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EventDetailsActivity : AppCompatActivity(), View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    private var totalRatings: Double = 0.0
    private var totalVotes: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).getSettingsComponent().inject(this)
        UtilsNavigationStyle.setApplicationTheme(this, preferencesSettings.getBoolean("themeKey", false))
        setContentView(R.layout.activity_event_details)
        UtilsUIElementsStyle.setCardViewBackgroundStyle(
            preferencesSettings.getBoolean("themeKey", false),
            this,
            card_viewLocation,
            card_viewNameAndDescription,
            card_viewNumberPriceCapacity,
            card_viewDateTime,
            card_viewReserve,
            card_viewRating
        )
        setUpDetailsData()
        setupNavigation()
        setButtonStyle()
        setIconStyle()
        buttonReserve.setOnClickListener(this)
        buttonRating.onRatingBarChangeListener = this
        hideReserveButton()
        hideRatingButton()
    }

    // Back button handling at toolbar on activity_settings (Back to EventList)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    // Event handling for buttons (reserve button)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonReserve -> reserveEvent()
        }
    }

    // Finding out is this user already made reservation for this particular event
    override fun onResume() {
        super.onResume()
        val firebaseCallsImp = FirebaseCallsImp()
        FirebaseDelegate(firebaseCallsImp).getAllReservations(HomeFragment.referenceDBReservations, this)
       /* val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDelegate(firebaseCallsImp).getRatingsByUser(
                HomeFragment.referenceDBRatings,
                this,
                user.uid
            )
        }*/
        setNumStars()
    }


    // Uploading new rating to the firebase
    override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
        val user = FirebaseAuth.getInstance().currentUser
        val firebaseCallsImp = FirebaseCallsImp()
        if (user != null) {
            if (!restrictFromDuplicationRating()) {
                Log.d("tagic","Uso sam ovde znaci kao da user nije votao prethodno")
                val userRating = Rating(getEventKey(), user.uid, rating.toDouble())
                HomeFragment.referenceDBRatings.push().setValue(userRating)
                FirebaseDelegate(firebaseCallsImp).updateEventRates(
                    HomeFragment.referenceDB, this,
                    this.intent.getIntExtra("totalVotes", 0),
                    1,
                    this.intent.getDoubleExtra("totalRating", 0.0),
                    rating.toDouble(),
                    getEventKey()
                )
                totalRatings = (totalRatings + rating.toDouble())
                totalVotes = (totalVotes + 1)
                textViewTotalRating.text = (totalRatings / (this.intent.getIntExtra(
                    "totalVotes",
                    0
                ) + 1)).toString() + "/(" + totalVotes.toString() + ")"
            } else {
                Log.d("tagic","Uso sam ovde znaci da je user votao")
                for (i in FirebaseCallsImp.allRatings!!.indices) {
                    if (FirebaseCallsImp.allRatings!![i].eventId == FirebaseCallsImp.eventKey.toString() && FirebaseCallsImp.allRatings!![i].userId == user.uid) {
                        FirebaseDelegate(firebaseCallsImp).updateRating(
                            HomeFragment.referenceDBRatings,
                            this,
                            FirebaseCallsImp.ratingsIds!![i],
                            rating.toDouble()
                        )
                        FirebaseDelegate(firebaseCallsImp).updateEventRates(
                            HomeFragment.referenceDB, this,
                            totalVotes,
                            0,
                            totalRatings,
                            (rating.toDouble() - FirebaseCallsImp.allRatings!![i].rating),
                            getEventKey()
                        )
                        totalRatings =
                            (totalRatings + (rating.toDouble() - FirebaseCallsImp.allRatings!![i].rating))
                        textViewTotalRating.text =
                            (totalRatings / totalVotes).toString() + textViewTotalRating.text.substring(
                                textViewTotalRating.text.indexOf('/')
                            )
                        FirebaseCallsImp.allRatings!![i].rating = rating.toDouble()
                        break
                    }
                }
            }
        }
    }

    // Filling up ui elements with content provided from the card view
    private fun setUpDetailsData() {
        textViewEventNameDetails.text = this.intent.getStringExtra("eventName")
        textViewCapacityDetails.text =
            this.intent.getIntExtra("capacityReserved", 0).toString() + "/" + this.intent.getIntExtra(
                "capacity",
                0
            ).toString()
        textViewEventPhoneDetails.text = this.intent.getStringExtra("phoneNumber")
        textViewEventLocationDetails.text = this.intent.getStringExtra("address")
        textViewDescriptionDetails.text = this.intent.getStringExtra("description")
        textViewPriceDetails.text = this.intent.getIntExtra("price", 0).toString()
        textViewEventTimeZoneDetails.text = this.intent.getStringExtra("timeZone")
        textViewEventStartContent.text = getDateTime(this.intent.getLongExtra("startDate", 0).toString())
        textViewEventEndContent.text = getDateTime(this.intent.getLongExtra("endDate", 0).toString())
        totalRatings = this.intent.getDoubleExtra("totalRating", 0.0)
        totalVotes = this.intent.getIntExtra("totalVotes", 0)
        textViewTotalRating.text = (totalRatings / this.intent.getIntExtra(
            "totalVotes",
            0
        )).toString() + "/(" + this.intent.getIntExtra("totalVotes", 0).toString() + ")"

        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef: StorageReference = storageReference.child("images/" + this.intent.getStringExtra("imageName"))
        GlideApp.with(this)
            .load(imageRef)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageViewCardViewDetails)
    }


    @SuppressLint("PrivateResource")
    private fun setupNavigation() {
        setSupportActionBar(toolbar_settings)
        val upArrow: Drawable? = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
        upArrow?.setColorFilter(ContextCompat.getColor(this, R.color.backgroundOrangeTheme), PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Event Details"
        toolbar_settings.setTitleTextColor(Color.WHITE)
    }

    // Converting long timestamp to real readable date format
    private fun getDateTime(s: String): String? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd   hh:mm:ss", Locale.getDefault())
            val netDate = Date(s.toLong())
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    // Setting style to buttons
    private fun setButtonStyle() {
        UtilsButtonStyle.setButtonsStyle(
            this,
            preferencesSettings.getBoolean("themeKey", false),
            buttonReserve
        )
    }

    // Setting style icons
    private fun setIconStyle() {
        UtilsUIElementsStyle.setIconColors(
            this,
            preferencesSettings.getBoolean("themeKey", false),
            imageViewCapacity,
            imageViewEventPhoneDetails,
            imageViewEventTimeZoneDetails,
            imageViewEventLocationDetails,
            imageViewPriceDetails
        )
    }

    // Method for reserving event, but if you really fast open activity and click on reserve there is chance that event id could be null because of firebase call
    private fun reserveEvent() {
        val user = FirebaseAuth.getInstance().currentUser
        val reservationsMade =
            textViewCapacityDetails.text.substring(0, textViewCapacityDetails.text.indexOf('/')).toInt()
        val capacity = this.intent.getIntExtra("capacity", 0)
        if (user != null) {
            if (reservationsMade < capacity) {
                if (!restrictUserForReservations(user.uid)) {
                    val reservation = Reservation(FirebaseCallsImp.eventKey.toString(), user.uid)
                    HomeFragment.referenceDBReservations.push().setValue(reservation)
                    val firebaseCallsImp = FirebaseCallsImp()
                    FirebaseDelegate(firebaseCallsImp).updateEventCapacity(
                        HomeFragment.referenceDB,
                        this,
                        reservationsMade,
                        FirebaseCallsImp.eventKey.toString()
                    )
                    val updatedString = (textViewCapacityDetails.text.substring(
                        0,
                        textViewCapacityDetails.text.indexOf('/')
                    ).toInt() + 1).toString()
                    textViewCapacityDetails.text =
                        updatedString + textViewCapacityDetails.text.substring(textViewCapacityDetails.text.indexOf('/'))
                } else {
                    Toast.makeText(this, "You have already made that reservation", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All places have been reserved, you can't reserve event", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "User isn't logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideReserveButton() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val flagReservation = preferences.getBoolean("showReservation", false)
        if (flagReservation) card_viewReserve.visibility = View.VISIBLE else card_viewReserve.visibility = View.GONE
    }

    private fun hideRatingButton() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val flagReservation = preferences.getBoolean("showRating", false)
        if (flagReservation) card_viewRating.visibility = View.VISIBLE else card_viewRating.visibility = View.GONE
    }

    private fun restrictUserForReservations(userId: String): Boolean {
        val lista = FirebaseCallsImp.allReservationsList
        for (reservation in lista!!.indices) {
            if (lista[reservation].eventId == FirebaseCallsImp.eventKey.toString() && lista[reservation].userId == userId) {
                return true
            }
        }
        return false
    }

    private fun restrictFromDuplicationRating(): Boolean {
        val listRatings = FirebaseCallsImp.allRatings
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            for (rating in listRatings!!.indices) {
                if (listRatings[rating].eventId == getEventKey() && listRatings[rating].userId == user.uid) {
                    return true
                }
            }
        }
        return false
    }


    private fun setNumStars() {
        val mapAllEvents = FirebaseCallsImp.eventKeys
        val user = FirebaseAuth.getInstance().currentUser
        var eventKey = ""

        if (user != null) {
            if (mapAllEvents != null) {
                for ((k, v) in mapAllEvents) {
                    if (v == this.intent.getStringExtra("imageName")) {
                        eventKey = k
                    }
                }
            }

            for (i in FirebaseCallsImp.allRatings!!.indices) {
                if (FirebaseCallsImp.allRatings!![i].eventId == eventKey && FirebaseCallsImp.allRatings!![i].userId == user.uid) {
                    buttonRating.rating = FirebaseCallsImp.allRatings!![i].rating.toFloat()
                }
            }
        }
    }

    private fun getEventKey() : String {
        val mapAllEvents = FirebaseCallsImp.eventKeys
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            if (mapAllEvents != null) {
                for ((k, v) in mapAllEvents) {
                    if (v == this.intent.getStringExtra("imageName")) {
                        return k
                    }
                }
            }
        }
        return ""
    }
}
