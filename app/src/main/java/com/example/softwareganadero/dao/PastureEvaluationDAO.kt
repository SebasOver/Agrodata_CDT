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

    // Última evaluación registrada para validar “Salida” requiere “Entrada” previa
    @Query("SELECT * FROM pasture_evaluations ORDER BY created_at DESC LIMIT 1")
    suspend fun last(): PastureEvaluation?

    @Query("SELECT * FROM pasture_evaluations ORDER BY created_at DESC")
    suspend fun getAll(): List<PastureEvaluation>
}