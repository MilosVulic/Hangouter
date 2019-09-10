package com.metropolitan.hangouterproject.adapters

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.event_details.EventDetailsActivity
import com.metropolitan.hangouterproject.events.GlideApp
import com.metropolitan.hangouterproject.events.HomeFragment
import com.metropolitan.hangouterproject.firebase.FirebaseCallsImp
import com.metropolitan.hangouterproject.firebase.FirebaseDelegate
import com.metropolitan.hangouterproject.maps.MapsActivity
import com.metropolitan.hangouterproject.models.Event
import com.metropolitan.hangouterproject.utils.UtilsButtonStyle
import javax.inject.Inject


class RecyclerViewEventsAdapter(private val mDataset: ArrayList<Event>, private val context: Context) : RecyclerView.Adapter<RecyclerViewEventsAdapter.EventHolderObject>() {

    var uri: Uri? = null
    lateinit var content: String

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    class EventHolderObject(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var name: TextView = itemView.findViewById(R.id.textViewName)
        internal var description: TextView = itemView.findViewById(R.id.textViewDescription)
        internal var cardView: CardView = itemView.findViewById(R.id.card_view)
        internal var buttonFindUs: Button = itemView.findViewById(R.id.findUsButton)
        internal var buttonExplore: Button = itemView.findViewById(R.id.exploreButton)
        internal var imageView: ImageView = itemView.findViewById(R.id.imageViewCardViewDetails)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            myClickListener!!.onItemClick(adapterPosition, v)
        }
    }

    fun setOnItemClickListener(myClickListener: MyClickListener) {
        Companion.myClickListener = myClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolderObject {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_row, parent, false)
        return EventHolderObject(view)
    }

    override fun onBindViewHolder(holder: EventHolderObject, position: Int) {
        HomeFragment.app.getSettingsComponent().inject(this)
        val theme = preferencesSettings.getBoolean("themeKey", false)
        UtilsButtonStyle.setCardViewButtonsStyle(theme, context, holder.cardView, holder.buttonFindUs, holder.buttonExplore)
        holder.name.text = mDataset[position].eventName
        holder.description.text = mDataset[position].description
        val storageReference = FirebaseStorage.getInstance().reference
        displayImage(context, storageReference, holder.imageView, mDataset[position].imageName)

        holder.buttonFindUs.setOnClickListener { v ->
            val intent = Intent(v.context, MapsActivity::class.java)
            intent.putExtra("Lat", mDataset[position].location.latitude)
            intent.putExtra("Long", mDataset[position].location.longitude)
            v.context.startActivity(intent)
        }


        holder.buttonExplore.setOnClickListener { v ->
            val firebaseCallsImp = FirebaseCallsImp()
            FirebaseDelegate(firebaseCallsImp).getEventId(HomeFragment.referenceDB,v.context, mDataset[position].imageName)
            val intent = Intent(v.context, EventDetailsActivity::class.java).apply {
                putExtra("eventName", mDataset[position].eventName)
                putExtra("phoneNumber", mDataset[position].phoneNumber)
                putExtra("capacity", mDataset[position].capacity)
                putExtra("address", mDataset[position].location.address)
                putExtra("description", mDataset[position].description)
                putExtra("price", mDataset[position].price)
                putExtra("startDate", mDataset[position].startDate)
                putExtra("endDate", mDataset[position].endDate)
                putExtra("timeZone", mDataset[position].timeZone)
                putExtra("imageName", mDataset[position].imageName)
                putExtra("capacityReserved", mDataset[position].capacityReserved)
                putExtra("totalVotes", mDataset[position].totalVotes)
                putExtra("totalRating", mDataset[position].totalRating)
            }
            v.context.startActivity(intent)
        }
    }


    fun addItem(dataObj: Event, index: Int) {
        mDataset.add(index, dataObj)
        notifyItemInserted(index)
    }

    fun deleteItem(index: Int) {
        mDataset.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun getItemCount(): Int {
        return mDataset.size
    }

    interface MyClickListener {
        fun onItemClick(position: Int, v: View)
    }

    companion object {
        private var myClickListener: MyClickListener? = null
    }

    private fun displayImage(context: Context, storageReference: StorageReference, imageView: ImageView, imageName: String) {
        val imageRef: StorageReference = storageReference.child("images/$imageName")
        GlideApp.with(context)
            .load(imageRef)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }
}