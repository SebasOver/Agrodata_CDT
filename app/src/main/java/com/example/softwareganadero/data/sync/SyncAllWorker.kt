package com.example.softwareganadero.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.BirthRepository
import com.google.firebase.firestore.FirebaseFirestore

class SyncAllWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            // Crear dependencias mínimas (en proyectos grandes usarías DI)
            val db = AgroDatabase.get(applicationContext)
            val firestore = FirebaseFirestore.getInstance()

            val birthRepo = BirthRepository(db, firestore)

            val syncManager = SyncManager(
                birthRepository = birthRepo
            )

            syncManager.syncAll()

            Result.success()
        } catch (e: Exception) {
            // si algo falla, WorkManager reintentará según su política
            Result.retry()
        }
    }
}