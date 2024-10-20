package com.example.lab16parliament.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Miro Saarinen
 * 21/10/2024
 * Data class for MP extras.
 */

@Entity(tableName = "mp_extras_table")
data class MPExtras(
    @PrimaryKey
    val hetekaId: Int,
    val twitter: String?,
    val bornYear: Int?,
    val constituency: String?,
)
