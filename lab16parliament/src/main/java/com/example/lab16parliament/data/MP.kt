package com.example.lab16parliament.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Miro Saarinen
 * 21/10/2024
 * Data class for MPs.
 */
@Entity(tableName = "mp_table")
data class MP(
    @PrimaryKey
    val hetekaId: Int,
    val seatNumber: Int?,
    val lastname: String?,
    val firstname: String?,
    val party: String?,
    val minister: Boolean?,
    val pictureUrl: String?
)
