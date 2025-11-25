package com.example.softwareganadero.routes.potrerosRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.SupplementsRepository
import com.example.softwareganadero.ui.theme.potreros.SuplementosScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SuplementosRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { SupplementsRepository(db) }
    val scope = rememberCoroutineScope()

    SuplementosScreen(
        onBack = { nav.popBackStack("potreros", inclusive = false) },
        onGuardar = { rotation, lot, animals, name, quantity, ts, tsText ->
            // se llama desde la Screen dentro de una coroutine
            repo.save(rotation, lot, animals, name, quantity, ts, tsText)
        }
    )
}
