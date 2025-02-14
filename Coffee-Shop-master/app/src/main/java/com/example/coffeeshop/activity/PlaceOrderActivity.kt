package com.example.coffeeshop.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.coffeeshop.R
import com.example.coffeeshop.databinding.ActivityPlaceOrderBinding
import com.example.coffeeshop.model.CartItem
import com.example.coffeeshop.model.Order
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.*

class PlaceOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPlaceOrderBinding
    private lateinit var database: DatabaseReference
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("Orders")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 0.0)
        val cartDetails: ArrayList<CartItem>? = intent.getParcelableArrayListExtra("CART_DETAILS")
        val productCount = cartDetails?.size ?: 0
        binding.etTotalPrice.setText("Total: "+totalPrice.toString()+"$")

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnGetLocation.setOnClickListener {
            fetchCurrentLocation()
        }

        binding.btnConfirmOrder.setOnClickListener {
            saveOrderToFirebase(totalPrice, productCount, cartDetails)
            startActivity(Intent(this, OrderActivity::class.java))
        }

    }

    // This method will be called when the map is ready
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
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
                    binding.etAddress.setText(getAddressFromLatLng(location.latitude, location.longitude))
                } else {
                    Toast.makeText(this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationError", "Error fetching location: ${exception.message}")
                Toast.makeText(this, "Error fetching location", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getAddressFromLatLng(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "No address found"
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

    private fun saveOrderToFirebase(totalPrice: Double, productCount: Int, cartDetails: ArrayList<CartItem>?) {
        val name = binding.etCustomerName.text.toString()
        val phone = binding.etCustomerPhone.text.toString()
        val address = binding.etAddress.text.toString()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = database.push().key ?: return

        // Extract product names from the cart using map
        val productNames = cartDetails?.map { it.productName } ?: emptyList()

        val order = Order(
            customerName = binding.etCustomerName.text.toString(),
            customerPhone = binding.etCustomerPhone.text.toString(),
            address = binding.etAddress.text.toString(),
            totalPrice = totalPrice,
            productCount = productCount,
            productNames = productNames,
            orderDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        database.child(orderId).setValue(order)
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to place order!", Toast.LENGTH_SHORT).show()
            }
    }

}
