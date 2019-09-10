package com.metropolitan.hangouterproject.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.metropolitan.hangouterproject.R
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)

        val geoLocation = Geocoder(this, Locale.getDefault())
        if (this.intent.extras == null) {
            mMap.setOnMapClickListener { latLng ->
                try {
                    val address = geoLocation.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    mMap.addMarker(MarkerOptions().position(latLng).title(address[0].getAddressLine(address[0].maxAddressLineIndex)))
                    val intent = Intent().apply {
                        putExtra("location", getFormattedAddress(geoLocation, latLng))
                        putExtra("latitude", latLng.latitude.toString())
                        putExtra("longitude", latLng.longitude.toString())
                    }
                    this.setResult(RESULT_OK, intent)
                    finish()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        if (this.intent.extras != null) {
            val location =
                LatLng(this.intent.getStringExtra("Lat").toDouble(), this.intent.getStringExtra("Long").toDouble())
            try {
                val address = geoLocation.getFromLocation(location.latitude, location.longitude, 1)
                mMap.addMarker(MarkerOptions().position(location).title(address[0].getAddressLine(address[0].maxAddressLineIndex)))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Current location is", Toast.LENGTH_SHORT).show()
    }

    private fun getFormattedAddress(geocoder: Geocoder, latLng: LatLng): String {
        val location = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        return location[0].getAddressLine(0)
    }
}
