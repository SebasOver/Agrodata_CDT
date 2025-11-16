package com.example.softwareganadero.routes.institucionesRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.visitasDomains.InstitutionRepository
import com.example.softwareganadero.domain.visitasDomains.ParticularRepository
import com.example.softwareganadero.ui.theme.visitas.VisitasInstitucionesParticularesScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VisitasInstitucionesParticularesRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val institutionRepo = remember { InstitutionRepository(db) }
    val particularRepo = remember { ParticularRepository(db) }

    VisitasInstitucionesParticularesScreen(
        onBack = { nav.popBackStack("visitas", inclusive = false) },
        onGuardarInstitucion = { name, reason, notes, ts, tsText ->
            institutionRepo.saveEntry(name, reason, notes, ts, tsText)
        },
        onRegistrarSalidaInstitucion = { id, ts, tsText ->
            institutionRepo.closeVisit(id, ts, tsText)
        },
        loadOpenInstituciones = { institutionRepo.getOpenVisits() },
        onGuardarParticular = { name, reason, notes, ts, tsText ->
            particularRepo.saveEntry(name, reason, notes, ts, tsText)
        },
        onRegistrarSalidaParticular = { id, ts, tsText ->
            particularRepo.closeVisit(id, ts, tsText)
        },
        loadOpenParticulares = { particularRepo.getOpenVisits() }
    )
}
