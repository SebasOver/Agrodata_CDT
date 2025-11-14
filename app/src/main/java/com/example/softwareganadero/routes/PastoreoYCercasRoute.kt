package com.example.softwareganadero.routes

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.FenceStateRepository
import com.example.softwareganadero.domain.GrazingRepository
import com.example.softwareganadero.ui.theme.PastoreoYCercasScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PastoreoYCercasRoute(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val grazingRepo = remember { GrazingRepository(db) }
    val fenceRepo = remember { FenceStateRepository(db) }
    val scope = rememberCoroutineScope()

    PastoreoYCercasScreen(
        onBack = onBack,
        onGuardarPastoreo = { rot, pot, animals, millis, text ->
            scope.launch {
                try { grazingRepo.save(rot, pot, animals, millis, text) /* ... */ } catch (t: Throwable) { /* ... */ }
            }
        },
        onGuardarCercas = { volteos, millis, text ->
            scope.launch {
                try {
                    fenceRepo.save(volteos, millis, text)
                    Toast.makeText(ctx, "Estado de cercas guardado", Toast.LENGTH_LONG).show()
                } catch (t: Throwable) {
                    Toast.makeText(ctx, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    )
}