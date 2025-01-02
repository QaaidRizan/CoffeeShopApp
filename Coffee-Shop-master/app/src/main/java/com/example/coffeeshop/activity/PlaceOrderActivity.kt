package com.example.coffeeshop.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coffeeshop.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class PlaceOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var btnGetLocation: Button
    private lateinit var btnConfirmOrder: Button
    private lateinit var etCustomerName: EditText
    private lateinit var etCustomerPhone: EditText
    private lateinit var etAddress: EditText

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        // Initialize UI elements
        btnGetLocation = findViewById(R.id.btnGetLocation)
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)
        etCustomerName = findViewById(R.id.etCustomerName)
        etCustomerPhone = findViewById(R.id.etCustomerPhone)
        etAddress = findViewById(R.id.etAddress)

        btnGetLocation.isEnabled = false

        // Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Button click listener for getting location
        btnGetLocation.setOnClickListener {
            fetchCurrentLocation()
        }

        // Button click listener for confirming the order
        btnConfirmOrder.setOnClickListener {
            confirmOrder()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        btnGetLocation.isEnabled = true
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show()
    }

    private fun fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    etAddress.setText(getAddressFromLatLng(location.latitude, location.longitude))
                } else {
                    Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show()
                    requestNewLocationData()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationError", "Error fetching location: ${exception.message}")
                Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun requestNewLocationData() {
        Toast.makeText(this, "Location not available, try again later", Toast.LENGTH_SHORT).show()
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "No address found for the location"
        } catch (e: Exception) {
            Log.e("GeocoderError", "Error fetching address: ${e.message}")
            "Unable to fetch address"
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun confirmOrder() {
        val name = etCustomerName.text.toString()
        val phone = etCustomerPhone.text.toString()
        val address = etAddress.text.toString()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Logic for confirming the order
        Toast.makeText(this, "Order confirmed for $name", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                fetchCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
