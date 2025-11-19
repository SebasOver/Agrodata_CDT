package com.example.softwareganadero.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.PrecipitacionRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrecipitacionScreen(
    navBack: () -> Unit,
    currentOperatorName: String
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { PrecipitacionRepository(db) }
    val scope = rememberCoroutineScope()

    val lightBlue = Color(0xFFE6F0FA)
    val primaryBlue = Color(0xFF2E73C8)

    // ---------- Estado: Precipitación ----------
    var precipMm by rememberSaveable { mutableStateOf("") }
    var savingPrecip by rememberSaveable { mutableStateOf(false) }
    val precipValid = precipMm.isNotBlank() && precipMm.toDoubleOrNull() != null

    // ---------- Estado: Inventario ----------
    var lot by rememberSaveable { mutableStateOf("") }
    var healthy by rememberSaveable { mutableStateOf("") }
    var sick by rememberSaveable { mutableStateOf("") }
    var savingInv by rememberSaveable { mutableStateOf(false) }

    val lotInt = lot.toIntOrNull()
    val hInt = healthy.toIntOrNull()
    val sInt = sick.toIntOrNull()
    val total = (hInt ?: 0) + (sInt ?: 0)

    val lotValid = lot.isNotBlank()
    val inventoryValid = lotValid && hInt != null && sInt != null

    var showPrecipSuccess by rememberSaveable { mutableStateOf(false) }
    var showInvSuccess by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Precipitación e inventario animales",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Image(
                        painterResource(R.drawable.logo_blanco),
                        null,
                        Modifier.size(44.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { inner ->

        // SCROLL VERTICAL PARA TODO EL CONTENIDO
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ====== Sección: Precipitación ======
            Text(
                "Precipitación",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text("Cantidad (mm)")
            TextField(
                value = precipMm,
                onValueChange = { s ->
                    if (s.isEmpty() || s.matches(Regex("""\d*\.?\d*"""))) {
                        precipMm = s
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = precipMm.isNotEmpty() && precipMm.toDoubleOrNull() == null,
                supportingText = {
                    if (precipMm.isNotEmpty() && precipMm.toDoubleOrNull() == null) {
                        Text("Ej: 12.3")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Button(
                onClick = {
                    if (!precipValid || savingPrecip) return@Button
                    savingPrecip = true
                    scope.launch {
                        try {
                            val nowMillis = System.currentTimeMillis()
                            val nowText =
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                    .format(
                                        Instant.ofEpochMilli(nowMillis)
                                            .atZone(ZoneId.systemDefault())
                                    )

                            repo.savePrecipitation(
                                precipMm.toDouble(),
                                currentOperatorName.trim(),
                                nowText,
                                nowMillis
                            )
                            precipMm = ""
                            showPrecipSuccess = true
                        } catch (e: Exception) {
                            Toast.makeText(
                                ctx,
                                "Error: ${e.message ?: "desconocido"}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            savingPrecip = false
                        }
                    }
                },
                enabled = precipValid && !savingPrecip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (precipValid) primaryBlue else Color(0xFF9CB9E6),
                    contentColor = Color.White
                )
            ) {
                Text(if (savingPrecip) "Guardando..." else "Guardar")
            }

            Spacer(Modifier.height(8.dp))

            // ====== Sección: Inventario potreros ======
            Text(
                "Inventario animales",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text("Lote de ganado")
            TextField(
                value = lot,
                onValueChange = { lot = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Text("Animales sanos")
            TextField(
                value = healthy,
                onValueChange = { s ->
                    if (s.isEmpty() || s.matches(Regex("""\d+"""))) healthy = s
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = healthy.isNotEmpty() && hInt == null,
                supportingText = {
                    if (healthy.isNotEmpty() && hInt == null) Text("Número entero")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Text("Animales enfermos")
            TextField(
                value = sick,
                onValueChange = { s ->
                    if (s.isEmpty() || s.matches(Regex("""\d+"""))) sick = s
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = sick.isNotEmpty() && sInt == null,
                supportingText = {
                    if (sick.isNotEmpty() && sInt == null) Text("Número entero")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total")
                Text(
                    text = total.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = primaryBlue
                )
            }

            Button(
                onClick = {
                    if (!inventoryValid || savingInv) {
                        if (!inventoryValid) {
                            Toast.makeText(
                                ctx,
                                "Completa lote, sanos y enfermos con datos válidos",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return@Button
                    }
                    savingInv = true
                    scope.launch {
                        try {
                            val nowMillis = System.currentTimeMillis()
                            val nowText =
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                    .format(
                                        Instant.ofEpochMilli(nowMillis)
                                            .atZone(ZoneId.systemDefault())
                                    )

                            repo.savePastureInventory(
                                lotInt ?: 0,
                                hInt!!,
                                sInt!!,
                                total,
                                currentOperatorName.trim(),
                                nowText,
                                nowMillis
                            )
                            lot = ""
                            healthy = ""
                            sick = ""
                            showInvSuccess = true
                        } catch (e: Exception) {
                            Toast.makeText(
                                ctx,
                                "Error: ${e.message ?: "desconocido"}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            savingInv = false
                        }
                    }
                },
                enabled = inventoryValid && !savingInv,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (inventoryValid) primaryBlue else Color(0xFF9CB9E6),
                    contentColor = Color.White
                )
            ) {
                Text(if (savingInv) "Guardando..." else "Guardar")
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Diálogo precipitación
    SuccessDialogDual(
        show = showPrecipSuccess,
        title = "Precipitación guardada",
        message = "Continúa con el inventario de animales.",
        primaryText = "Volver",
        onPrimary = { showPrecipSuccess = false; navBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showPrecipSuccess = false },
        onDismiss = { showPrecipSuccess = false }
    )

    // Diálogo inventario
    SuccessDialogDual(
        show = showInvSuccess,
        title = "Inventario guardado",
        message = "Se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showInvSuccess = false; navBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showInvSuccess = false },
        onDismiss = { showInvSuccess = false }
    )
}

