package com.example.mapstest.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.mapstest.R
import com.example.mapstest.databinding.FragmentMapBinding
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
import com.google.android.material.snackbar.Snackbar
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
    private val arguments: MapFragmentArgs by navArgs()

    private lateinit var binding: FragmentMapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
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
            showMarkerClickedDialog(clickedMarker)
        }
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

            // when user first launches the app and is asked for geo permission, and grants it,
            // the fragment is already created for that time and the code below will not be
            // implemented properly until the fragment is re-inflated, i.e. no camera animation,
            // no compass button and no user location (blue dot) are seen until user relaunches
            // MapFragment.
            // That said, the cardView is shown when the permission isn't there to restart the app
            binding.restartAppCV.visibility = View.VISIBLE
            binding.closeAppBt.setOnClickListener {
                val restart = Intent(requireContext(), MainActivity::class.java)
                startActivity(restart)
            }
            return
        }

        // isMyLocationEnabled = true enables the my-location layer which draws
        // a light blue dot on the user’s location. It also adds a button to the map that,
        // when tapped, centers the map on the user’s location.
        googleMap.apply {
            isMyLocationEnabled = true

        }

        // if we have any arguments passed from AllMarkersFragment, then handle the behavior right
        // here, during the map setup
        if (arguments.isArgumentPassed) {
            val position = LatLng(arguments.lat.toDouble(), arguments.lng.toDouble())
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f))
        } else {
            // fusedLocationClient.getLastLocation() provides the most recent location currently
            // available.
            // If you were able to retrieve the the most recent location, then move the
            // camera to the user’s current location.
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    lastLocation = it
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
                }
            }
        }
    }

    private fun showMarkerClickedDialog(clickedMarker: Marker): Boolean {
        val markerTitle = EditText(requireContext()).apply {
            setText(clickedMarker.title)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.edit_marker_dialog))
            .setView(markerTitle)
            .setPositiveButton(
                getString(R.string.change_title_marker_dialog)
            ) { _, _ ->
                clickedMarker.title = markerTitle.text.toString()

                // update marker's title in db
                viewModel.updateMarkerTitle(clickedMarker)
            }
            .setNegativeButton(
                getString(R.string.remove_marker_dialog)
            ) { dialog, _ ->
                clickedMarker.isVisible = false
                clickedMarker.remove()
                viewModel.removeMarker(clickedMarker)
                dialog.cancel()
            }
            .setNeutralButton(getString(R.string.cancel_bt_marker_dialog)) { dialog, id ->
                dialog.cancel()
            }
            .show()
        return true
    }

    private fun onMapLongPressListener(latLng: LatLng) {
        placeMarkerOnMap(latLng)
        Snackbar.make(binding.mainMapFragment, getString(R.string.make_marker_tapToRename),
        Snackbar.LENGTH_SHORT).show()
    }

    private fun placeMarkerOnMap(location: LatLng) {
        viewModel.saveMarker(location.latitude, location.longitude)
        }

}