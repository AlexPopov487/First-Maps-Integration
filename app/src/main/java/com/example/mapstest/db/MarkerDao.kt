package com.example.mapstest.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MarkerDao {

    @Query("SELECT * FROM MarkerEntity ORDER BY id DESC")
    fun getAllMarkers(): LiveData<List<MarkerEntity>>

    @Query("DELETE FROM MarkerEntity WHERE id = :markerId ")
    suspend fun removeMarker(markerId: Long)

    @Insert
    suspend fun insertMarker(marker: MarkerEntity)
}