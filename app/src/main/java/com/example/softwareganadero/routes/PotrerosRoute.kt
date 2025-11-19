package com.example.softwareganadero.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.potreros.PotrerosScreen

@Composable
fun PotrerosRoute(nav: NavController) {
    PotrerosScreen(
        onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) },
        onNavigate = { destino -> nav.navigate(destino) }
    )
}