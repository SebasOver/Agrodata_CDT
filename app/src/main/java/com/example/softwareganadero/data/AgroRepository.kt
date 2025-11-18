package com.example.softwareganadero.data

import com.example.softwareganadero.data.corralesData.HealthControl
import com.example.softwareganadero.data.corralesData.Palpation
import com.example.softwareganadero.data.corralesData.TriageRecord
import com.example.softwareganadero.data.corralesData.Weighing
import com.example.softwareganadero.data.cultivosData.CropRecord
import com.example.softwareganadero.data.visitasData.InstitutionRecord
import com.example.softwareganadero.data.visitasData.ParticularRecord
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
    private val healthControlDao = db.healthControlDao()
    private val palpationDao = db.palpationDao()
    private val triageDao = db.triageDao()
    private val weighingDao = db.weighingDao()
    private val institutionDao = db.institutionDao()
    private val particularDao = db.particularDao()
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
    suspend fun listHealthControls(): List<HealthControl> = withContext(io) {
        healthControlDao.getAll()
    }

    suspend fun listPalpations(): List<Palpation> = withContext(io) {
        palpationDao.getAll()
    }

    suspend fun listTriageRecords(): List<TriageRecord> = withContext(io) {
        triageDao.getAll()
    }

    suspend fun listWeighings(): List<Weighing> = withContext(io) {
        weighingDao.getAll()
    }
    suspend fun listInstitutionRecords(): List<InstitutionRecord> = withContext(io) {
        institutionDao.getAll()
    }

    suspend fun listParticularRecords(): List<ParticularRecord> = withContext(io) {
        particularDao.getAll()
    }
}