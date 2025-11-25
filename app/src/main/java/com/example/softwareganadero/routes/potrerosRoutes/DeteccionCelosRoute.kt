package com.example.softwareganadero.routes.potrerosRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.HeatDetectionRepository
import com.example.softwareganadero.ui.theme.potreros.DeteccionCelosScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeteccionCelosRoute(onBack: () -> Unit) {
    DeteccionCelosScreen(onBack = onBack)
}