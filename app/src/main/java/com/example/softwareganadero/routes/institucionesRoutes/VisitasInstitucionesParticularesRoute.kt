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
    VisitasInstitucionesParticularesScreen(
        onBack = { nav.popBackStack("visitas", inclusive = false) }
    )
}
