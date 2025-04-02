package com.example.geolocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), LocationListener {
    private val LOCATION_PERM_CODE = 2
    private lateinit var locationManager: LocationManager
    private lateinit var statusTextView: TextView
    private lateinit var providersTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusTextView = findViewById(R.id.status)
        providersTextView = findViewById(R.id.providers)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        findViewById<Button>(R.id.updButton).setOnClickListener {
            requestLocation()
        }

        checkPermissionsAndStart()
    }

    private fun checkPermissionsAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERM_CODE)
        } else {
            requestLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val providers = locationManager.getProviders(true)
        providersTextView.text = "Available providers: $providers"

        if (providers.isEmpty()) {
            statusTextView.text = "Offline"
            Toast.makeText(this, "No location providers available", Toast.LENGTH_LONG).show()
            return
        }

        val provider = locationManager.getBestProvider(Criteria(), true)
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 5000, 5f, this)
            val location = locationManager.getLastKnownLocation(provider)
            if (location != null) {
                displayCoord(location.latitude, location.longitude)
            }
        }
    }

    override fun onLocationChanged(loc: Location) {
        displayCoord(loc.latitude, loc.longitude)
        Log.d("Location", "Updated: ${loc.latitude}, ${loc.longitude}")
    }

    override fun onProviderEnabled(provider: String) {
        statusTextView.text = "Online"
    }

    override fun onProviderDisabled(provider: String) {
        statusTextView.text = "Offline"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERM_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayCoord(latitude: Double, longitude: Double) {
        findViewById<TextView>(R.id.lat).text = String.format("%.5f", latitude)
        findViewById<TextView>(R.id.lng).text = String.format("%.5f", longitude)
    }
}
