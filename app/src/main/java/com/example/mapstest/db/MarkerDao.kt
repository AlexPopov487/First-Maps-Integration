package com.example.mapstest.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MarkerDao {

    @Query("SELECT * FROM MarkerEntity ORDER BY id DESC")
    fun getAllMarkers(): LiveData<List<MarkerEntity>>

    @Query("SELECT * FROM MarkerEntity WHERE longitude = :lng AND latitude = :lat LIMIT 1 ")
    suspend fun getByLatLng(lng: Double, lat: Double): MarkerEntity

    @Query("DELETE FROM MarkerEntity WHERE longitude = :lng AND latitude = :lat")
    suspend fun removeMarker(lng: Double, lat: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarker(marker: MarkerEntity)
}