package com.example.lab16parliament.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lab16parliament.data.MP
import com.example.lab16parliament.data.MPComment
import com.example.lab16parliament.data.MPExtras
import kotlinx.coroutines.flow.Flow

/**
 * Miro Saarinen
 * 21/10/2024
 * Data access object for MPs.
 */

@Dao
interface MPDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<MP>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExtras(mpExtras: List<MPExtras>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentAndGrade(commentAndGrade: MPComment)

    @Query("SELECT * FROM mp_table")
    fun getAllMPs(): Flow<List<MP>>

    @Query("SELECT * FROM mp_table WHERE hetekaId = :hetekaId")
    fun getMPById(hetekaId: Int): Flow<MP>

    @Query("SELECT * FROM mp_comment_table WHERE hetekaId = :hetekaId")
    fun getMPComments(hetekaId: Int): Flow<List<MPComment>>

    @Query("SELECT DISTINCT party FROM mp_table")
    fun getParties(): Flow<List<String>>

    @Query("SELECT * FROM mp_table WHERE party = :party")
    fun getMPsByParty(party: String): Flow<List<MP>>

    @Query("SELECT * FROM mp_extras_table WHERE hetekaId = :hetekaId")
    fun getMPExtras(hetekaId: Int): Flow<List<MPExtras>>



}
