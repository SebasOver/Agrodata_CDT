package com.example.softwareganadero.routes.potrerosRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.potreros.EvaluacionesPraderaAguaScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluacionesPraderaAguaRoute(nav: NavController) {
    EvaluacionesPraderaAguaScreen(
        onBack = { nav.popBackStack("potreros", inclusive = false) }
    )
}