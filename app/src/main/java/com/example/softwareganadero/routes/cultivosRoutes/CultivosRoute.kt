package com.example.softwareganadero.routes.cultivosRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.cultivos.CultivosScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CultivosRoute(nav: NavController) {
    CultivosScreen(
        onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) }
    )
}
