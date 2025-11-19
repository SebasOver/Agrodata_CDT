package com.example.softwareganadero.routes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.inicio.BienvenidaOperarioScreen

@Composable
fun BienvenidaOperarioRoute(nav: NavController) {
    BienvenidaOperarioScreen(
        onBack = { nav.popBackStack("welcome", inclusive = false) },
        onOpcionClick = { opcion ->
            when (opcion.texto) {
                "Corrales" -> nav.navigate("corrales")
                "Visitas"  -> nav.navigate("visitas")
                "Potreros" -> nav.navigate("potreros")
                "Cultivos" -> nav.navigate("cultivos") // NUEVO destino
            }
        }
    )
}