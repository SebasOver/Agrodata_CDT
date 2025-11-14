package com.example.softwareganadero.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.HeatDetectionRepository
import com.example.softwareganadero.ui.theme.DeteccionCelosScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeteccionCelosRoute(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { HeatDetectionRepository(db) }
    val scope = rememberCoroutineScope()

    DeteccionCelosScreen(
        onBack = onBack,
        loadCows = { repo.listCows() },
        onGuardar = { inHeat, cowTag, notes, ts, tsText ->
            // Repos ya valida reglas
            repo.save(inHeat, cowTag, notes, ts, tsText)
        }
    )
}