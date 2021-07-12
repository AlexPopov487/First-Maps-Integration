package com.example.mapstest.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mapstest.db.MarkerDb
import com.example.mapstest.db.MarkerEntity
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MarkerDb.getInstance(application)
    private val dao = db.markerDao()

    private val _markersList = dao.getAllMarkers()

    val markersList: LiveData<List<MarkerEntity>>
        get() = _markersList


    fun saveMarker(lat: Double, long: Double, title: String?) {
        viewModelScope.launch {
            dao.insertMarker(
                MarkerEntity(
                    latitude = lat,
                    longitude = long,
                    title = title ?: "Untitled marker"
                )
            )
        }
    }

    fun updateMarkerTitle(marker: Marker) {
        viewModelScope.launch {
            val markerEntity = dao.getByLatLng(
                marker.position.longitude,
                marker.position.latitude
            )
            dao.insertMarker(markerEntity.copy(title = marker.title))

        }
    }

    fun removeMarker(marker: Marker) {
        viewModelScope.launch {
            dao.removeMarker(
                lng = marker.position.longitude,
                lat = marker.position.latitude
            )
        }

    }
}