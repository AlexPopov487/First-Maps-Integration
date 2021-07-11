package com.example.mapstest

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the fragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.main_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // initialize fusedLocationClient to track user's current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        googleMap.apply {

            uiSettings.isZoomControlsEnabled = true
            setOnMapClickListener {
                Toast.makeText(requireContext(), "Syndey is clicked", Toast.LENGTH_SHORT).show()
            }
        }

        googleMap.setOnMapLongClickListener { latLng ->
            onMapLongPressListener(latLng)

        }
        setUpMap()
    }

    private fun onMapLongPressListener(latLng: LatLng) {
        placeMarkerOnMap(latLng, "new marker")
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    private fun setUpMap() {
        // if the permission is not granted, ask for it
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // isMyLocationEnabled = true enables the my-location layer which draws
        // a light blue dot on the user’s location. It also adds a button to the map that,
        // when tapped, centers the map on the user’s location.
        googleMap.apply {
            isMyLocationEnabled = true
        }
        // fusedLocationClient.getLastLocation() provides the most recent location currently
        // available.
        // If you were able to retrieve the the most recent location, then move the
        // camera to the user’s current location.
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lastLocation = it
                val currentLatLng = LatLng(it.latitude, it.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng, title: String) {
        val markerOption = MarkerOptions().position(location)
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
        googleMap.addMarker(markerOption)?.apply {
            setTitle(title)
            isDraggable = true
        }
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (!addresses.isNullOrEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" +
                            address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapFragment", e.message.toString())
            Toast.makeText(
                requireContext(),
                "Error: couldn't fetch the address!",
                Toast.LENGTH_SHORT
            ).show()
        }
        Log.i("MapFragment", "address is $addressText")

        return addressText
    }
}