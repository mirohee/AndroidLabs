package com.example.lab16parliament.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Miro Saarinen
 * 21/10/2024
 * Data class for MP comments.
 */
@Entity(tableName = "mp_comment_table")
data class MPComment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hetekaId: Int,
    val comment: String,
    val grade: Float
)
