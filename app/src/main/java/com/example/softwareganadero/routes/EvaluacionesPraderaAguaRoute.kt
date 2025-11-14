package com.example.softwareganadero.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.PastureEvaluationRepository
import com.example.softwareganadero.domain.WaterEvaluationRepository
import com.example.softwareganadero.ui.theme.EvaluacionesPraderaAguaScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluacionesPraderaAguaRoute(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val pastureRepo = remember { PastureEvaluationRepository(db) }
    val waterRepo = remember { WaterEvaluationRepository(db) }

    EvaluacionesPraderaAguaScreen(
        onBack = onBack,
        onGuardarPradera = { kind, height, color, ts, tsText -> pastureRepo.save(kind, height, color, ts, tsText) },
        onGuardarAgua = { availability, temp, ts, tsText -> waterRepo.save(availability, temp, ts, tsText) }
    )
}