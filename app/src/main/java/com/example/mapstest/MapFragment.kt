package com.example.mapstest

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mapstest.viewModels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.utils.collection.addMarker


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var collection: MarkerManager.Collection

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val viewModel: MapViewModel by viewModels()

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


        // fetch the markers from db if there are any
        viewModel.markersList.observe(viewLifecycleOwner) { markerList ->
            Log.i("MapFragment", "TESTING: $markerList")

            collection.clear()
            for (markerEntity in markerList) {
                collection.addMarker {
                    title(markerEntity.title)
                    position(LatLng(markerEntity.latitude, markerEntity.longitude))
                }
            }
        }

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        val markerManager = MarkerManager(googleMap)
        collection = markerManager.newCollection()

        googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
        }

        // setup the map at first launch
        setUpMap()

        googleMap.setOnMapLongClickListener { latLng ->
            onMapLongPressListener(latLng)
        }

        collection.setOnMarkerClickListener { clickedMarker ->
            val markerTitle = EditText(requireContext()).apply {
                setText(clickedMarker.title)
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("EditMarker")
                .setView(markerTitle)
                .setPositiveButton(
                    "Change title"
                ) { _, _ ->
                    clickedMarker.title = markerTitle.text.toString()

                    // update marker's title in db
                    viewModel.updateMarkerTitle(clickedMarker)
                }
                .setNeutralButton(
                    "Remove marker"
                ) { dialog, _ ->
                    clickedMarker.isVisible = false
                    clickedMarker.remove()
                    viewModel.removeMarker(clickedMarker)
                    dialog.cancel()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
                .show()
            true
        }

    }

    private fun onMapLongPressListener(latLng: LatLng) {
        placeMarkerOnMap(latLng, "new marker")
        //TODO: show snackbar tap on the marker to change the title
    }


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
            // TODO: SHOW SORRY DIALOG
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
        viewModel.saveMarker(location.latitude, location.longitude, title)
        }

}