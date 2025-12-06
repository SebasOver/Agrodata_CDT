package com.example.softwareganadero

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.softwareganadero.data.sync.SyncAllWorker
import com.example.softwareganadero.ui.theme.AgrodataApp
import com.google.firebase.FirebaseApp
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AgrodataApp() }
        FirebaseApp.initializeApp(this)

        schedulePeriodicSync()
    }
    private fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // solo con red
            .build()

        val request = PeriodicWorkRequestBuilder<SyncAllWorker>(
            15, TimeUnit.MINUTES // mínimo permitido por WorkManager
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_all_work",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}


