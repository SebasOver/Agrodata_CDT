package com.example.softwareganadero.data

import com.example.softwareganadero.data.cultivosData.CropRecord
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgroRepository(
    private val db: AgroDatabase,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    private val precipitations = db.precipitationDao()
    private val pastureInventories = db.pastureInventoryDao()
    private val pastureEvaluations = db.pastureEvaluationDao()
    private val waterEvaluations = db.waterEvaluationDao()
    private val pastureFenceLogs = db.pastureFenceLogDao()
    private val supplements = db.supplementDao()
    private val births = db.birthRecordDao()
    private val heats = db.heatDetectionDao()
    private val crops = db.cropDao()

    // --- Lecturas para exportar (pueden ser "todo" o "del día") ---

    suspend fun listPrecipitations(): List<Precipitation> = withContext(io) {
        precipitations.getAll()
    }

    suspend fun listPastureInventories(): List<PastureInventory> = withContext(io) {
        pastureInventories.getAll()
    }

    suspend fun listPastureEvaluations(): List<PastureEvaluation> = withContext(io) {
        pastureEvaluations.getAll()
    }

    suspend fun listWaterEvaluations(): List<WaterEvaluation> = withContext(io) {
        waterEvaluations.getAll()
    }

    suspend fun listPastureFenceLogs(): List<PastureFenceLog> = withContext(io) {
        pastureFenceLogs.getAll()
    }

    suspend fun listSupplements(): List<Supplement> = withContext(io) {
        supplements.getAll()
    }

    suspend fun listBirthRecords(): List<BirthRecord> = withContext(io) {
        births.getAll()
    }

    suspend fun listHeatDetections(): List<HeatDetection> = withContext(io) {
        heats.getAll()
    }

    suspend fun listCropRecords(): List<CropRecord> = withContext(io) {
        crops.getAll()
    }
}