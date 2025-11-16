package com.example.softwareganadero.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.visitas.VisitasScreen

@Composable
fun VisitasRoute(nav: NavController) {
    VisitasScreen(
        onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) },
        onNavigate = { destino -> nav.navigate(destino) }
    )
}