package com.example.mapstest.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val title: String,
) {
}