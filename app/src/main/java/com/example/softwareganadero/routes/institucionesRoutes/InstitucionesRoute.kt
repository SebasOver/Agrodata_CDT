package com.example.softwareganadero.routes.institucionesRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.visitasDomains.InstitutionRepository
import com.example.softwareganadero.ui.theme.visitas.InstitucionesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InstitucionesRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { InstitutionRepository(db) }

    InstitucionesScreen(
        onBack = { nav.popBackStack("visitas", inclusive = false) },
        onGuardar = { visitorName, reason, notes, ts, tsText ->
            repo.saveEntry(visitorName, reason, notes, ts, tsText)
        },
        onRegistrarSalida = { id, ts, tsText ->
            repo.closeVisit(id, ts, tsText)
        },
        loadOpenVisits = {
            repo.getOpenVisits()
        }
    )
}