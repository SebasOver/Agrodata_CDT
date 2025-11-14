package com.example.softwareganadero.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.softwareganadero.data.PastureEvaluation

@Dao
interface PastureEvaluationDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: PastureEvaluation): Long

    @Query("SELECT * FROM pasture_evaluations WHERE height_exit IS NULL ORDER BY created_at DESC LIMIT 1")
    suspend fun lastPendingExit(): PastureEvaluation?

    @Query("""
        UPDATE pasture_evaluations
        SET height_exit = :heightExit,
            color_exit = :colorExit,
            created_at = :updatedAt,
            created_at_text = :updatedAtText
        WHERE id = :id
    """)
    suspend fun updateExit(
        id: Long,
        heightExit: String,
        colorExit: String,
        updatedAt: Long,
        updatedAtText: String
    ): Int

    @Query("SELECT * FROM pasture_evaluations ORDER BY created_at DESC")
    suspend fun getAll(): List<PastureEvaluation>
}