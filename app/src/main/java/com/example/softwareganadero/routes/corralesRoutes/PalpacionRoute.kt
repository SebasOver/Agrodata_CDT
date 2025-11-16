package com.example.softwareganadero.routes.corralesRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.corralesDomains.PalpationRepository
import com.example.softwareganadero.ui.theme.corrales.PalpacionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PalpacionRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { PalpationRepository(db) }

    PalpacionScreen(
        onBack = { nav.popBackStack("corrales", inclusive = false) },
        onGuardar = { number, days, notes, ts, tsText ->
            repo.save(number, days, notes, ts, tsText)
        }
    )
}