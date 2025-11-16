package com.example.softwareganadero.routes.cultivosRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.cultivosDomains.CropRepository
import com.example.softwareganadero.ui.theme.cultivos.CultivosScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CultivosRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { CropRepository(db) }

    CultivosScreen(
        onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) }, // o donde corresponda
        onGuardar = { lot, species, hasPests, hasDiseases, notes, ts, tsText ->
            repo.save(lot, species, hasPests, hasDiseases, notes, ts, tsText)
        }
    )
}
