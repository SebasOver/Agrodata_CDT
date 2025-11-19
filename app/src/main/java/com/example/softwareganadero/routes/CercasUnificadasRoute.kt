package com.example.softwareganadero.routes

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.PastureFenceRepository
import com.example.softwareganadero.ui.theme.potreros.CercasUnificadasScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CercasUnificadasRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { PastureFenceRepository(db) }
    val scope = rememberCoroutineScope()

    CercasUnificadasScreen(
        onBack = { nav.popBackStack("potreros", inclusive = false) },
        onGuardar = { rot, pot, vol, notes, ts, tsText ->
            scope.launch {
                try {
                    repo.save(rot, pot, vol, notes, ts, tsText)
                    Toast.makeText(ctx, "Guardado", Toast.LENGTH_LONG).show()
                } catch (t: Throwable) {
                    Toast.makeText(ctx, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    )
}