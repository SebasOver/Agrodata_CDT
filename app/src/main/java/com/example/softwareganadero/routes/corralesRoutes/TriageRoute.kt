package com.example.softwareganadero.routes.corralesRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.corralesDomains.TriageRepository
import com.example.softwareganadero.ui.theme.corrales.TriageScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TriageRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { TriageRepository(db) }

    TriageScreen(
        onBack = { nav.popBackStack("corrales", inclusive = false) },
        onGuardar = { number, temp, locomotion, mucosa, notes, ts, tsText ->
            repo.save(number, temp, locomotion, mucosa, notes, ts, tsText)
        }
    )
}
