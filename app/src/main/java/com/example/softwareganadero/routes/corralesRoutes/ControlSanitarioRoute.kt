package com.example.softwareganadero.routes.corralesRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.corralesDomains.ControlSanitarioRepository
import com.example.softwareganadero.ui.theme.corrales.ControlSanitarioScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ControlSanitarioRoute(nav: NavController, db: AgroDatabase = AgroDatabase.get(LocalContext.current)) {
    val repo = remember { ControlSanitarioRepository(db) }
    ControlSanitarioScreen(
        onBack = { nav.popBackStack("corrales", inclusive = false) },
        onGuardar = { tratamiento, animal, medicamentos, dosis, cantidad, obs, ts, tsText ->
            repo.save(tratamiento, animal, medicamentos, dosis, cantidad, obs, ts, tsText)
        }
    )
}