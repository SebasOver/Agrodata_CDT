package com.example.softwareganadero.export

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.AgroRepository
import com.example.softwareganadero.data.UserRole
import com.example.softwareganadero.data.sync.SyncManager
import com.example.softwareganadero.domain.potrerosDomain.BirthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminExportScreen(
    currentUserName: String,
    onBack: () -> Unit,
    adminEmail: String
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val agroRepo = remember { AgroRepository(db) }

    // Firestore + repos necesarios para sync
    val firestore = remember { com.google.firebase.firestore.FirebaseFirestore.getInstance() }
    val birthRepo = remember { BirthRepository(db, firestore) } // de momento solo nacimientos

    // SyncManager global (irá creciendo con más repos)
    val syncManager = remember {
        SyncManager(
            birthRepository = birthRepo
            // cuando tengas más repos con syncTwoWay(), los añades al constructor
        )
    }

    val exporter = remember { CsvExporter(ctx, agroRepo) }
    val scope = rememberCoroutineScope()

    var sending by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentUserName) {
        val user = db.userDao().findActiveByName(currentUserName)
        if (user?.role != UserRole.ADMIN) {
            Toast.makeText(ctx, "Acceso restringido al administrador", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel administrador") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hola $currentUserName")
            Spacer(Modifier.height(8.dp))
            Text("Pronto más ;)")
            Spacer(Modifier.height(16.dp))
            Text(
                "Por ahora puedes enviar el reporte diario " +
                        "con toda la información registrada."
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (sending) return@Button
                    sending = true
                    scope.launch {
                        try {
                            // 1) Sincronizar toda la base antes de exportar
                            try {
                                syncManager.syncAll()
                            } catch (e: Throwable) {
                                // Si falla la sync, lo notificas pero aún puedes exportar lo local
                                Toast.makeText(
                                    ctx,
                                    e.message ?: "Error al sincronizar datos (se exportará lo local)",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            // 2) Generar todos los CSV
                            exporter.cleanupAllReportCsv()
                            exporter.exportPrecipitationsCsv()
                            exporter.exportPastureInventoriesCsv()
                            exporter.exportPastureEvaluationsCsv()
                            exporter.exportWaterEvaluationsCsv()
                            exporter.exportPastureFenceLogsCsv()
                            exporter.exportSupplementsCsv()
                            exporter.exportBirthRecordsCsv()
                            exporter.exportHeatDetectionsCsv()
                            exporter.exportCropsCsv()
                            exporter.exportHealthControlCsv()
                            exporter.exportPalpationsCsv()
                            exporter.exportTriageCsv()
                            exporter.exportWeighingsCsv()
                            exporter.exportInstitutionVisitsCsv()
                            exporter.exportParticularVisitsCsv()

                            // 3) Comprimir Agrodata en un ZIP
                            val zip = exporter.zipAgrodataDirectory()

                            // 4) Abrir app de correo con el ZIP adjunto
                            sendZipByEmail(
                                context = ctx,
                                file = zip,
                                to = adminEmail,
                                subject = "Reporte diario Agrodata",
                                body = "Adjunto encontrarás un ZIP con los diferentes reportes del CDT."
                            )

                            showSuccess = true
                        } catch (e: Throwable) {
                            Toast.makeText(
                                ctx,
                                e.message ?: "Error al preparar el reporte",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            sending = false
                        }
                    }
                },
                enabled = !sending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(if (sending) "Preparando reporte..." else "Enviar reporte diario")
            }
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            confirmButton = {
                TextButton(onClick = { showSuccess = false }) {
                    Text("Aceptar")
                }
            },
            title = { Text("Reporte preparado") },
            text = {
                Text("Se abrió tu aplicación de correo con el archivo CSV listo para enviarse a $adminEmail.")
            }
        )
    }
}