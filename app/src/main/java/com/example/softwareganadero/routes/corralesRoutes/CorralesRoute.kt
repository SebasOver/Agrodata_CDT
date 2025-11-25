package com.example.softwareganadero.routes.corralesRoutes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.corrales.CorralesScreen

@Composable
fun CorralesRoute(nav: NavController) {
    CorralesScreen(
        onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) },
        onNavigate = { destino -> nav.navigate(destino) }
    )
}