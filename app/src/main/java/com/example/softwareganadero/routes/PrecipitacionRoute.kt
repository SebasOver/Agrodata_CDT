package com.example.softwareganadero.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.softwareganadero.ui.theme.potreros.PrecipitacionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PrecipitacionRoute(
    nav: NavController,
    currentOperatorName: String
) {
    PrecipitacionScreen(
        navBack = { nav.popBackStack("potreros", inclusive = false) },
        currentOperatorName = currentOperatorName
    )
}