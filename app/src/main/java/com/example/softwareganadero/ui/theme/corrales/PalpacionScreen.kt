package com.example.softwareganadero.ui.theme.corrales

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.softwareganadero.R
import com.example.softwareganadero.dialogs.SuccessDialogDual
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalpacionScreen(
    onBack: () -> Unit,
    onGuardar: suspend (animalNumber: String, pregnancyDays: Int, observations: String?, ts: Long, tsText: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    var animalNumber by rememberSaveable { mutableStateOf("") }
    var pregnancyDays by rememberSaveable { mutableStateOf("") }
    var observations by rememberSaveable { mutableStateOf("") }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    val numberOk = animalNumber.isNotBlank() && animalNumber.all { it.isDigit() }
    val daysOk = pregnancyDays.isNotBlank() && pregnancyDays.all { it.isDigit() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Palpación", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
                actions = { Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("Número de animal") }
            item {
                TextField(
                    value = animalNumber,
                    onValueChange = { s -> if (s.isEmpty() || s.all { it.isDigit() }) animalNumber = s },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = animalNumber.isNotEmpty() && !numberOk,
                    supportingText = { if (animalNumber.isNotEmpty() && !numberOk) Text("Solo números") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }

            item { Text("Días de preñez") }
            item {
                TextField(
                    value = pregnancyDays,
                    onValueChange = { s -> if (s.isEmpty() || s.all { it.isDigit() }) pregnancyDays = s },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = pregnancyDays.isNotEmpty() && !daysOk,
                    supportingText = { if (pregnancyDays.isNotEmpty() && !daysOk) Text("Solo números") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = observations,
                    onValueChange = { observations = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
            }

            item {
                Button(
                    onClick = {
                        if (!numberOk) { Toast.makeText(ctx, "Número de animal requerido y numérico", Toast.LENGTH_LONG).show(); return@Button }
                        if (!daysOk) { Toast.makeText(ctx, "Días de preñez requerido y numérico", Toast.LENGTH_LONG).show(); return@Button }
                        if (saving || showSuccess) return@Button
                        saving = true

                        val ts = System.currentTimeMillis()
                        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
                        scope.launch {
                            try {
                                onGuardar(animalNumber.trim(), pregnancyDays.toInt(), observations.ifBlank { null }, ts, tsText)
                                // limpiar para otro registro
                                animalNumber = ""; pregnancyDays = ""; observations = ""
                                showSuccess = true
                            } catch (e: Throwable) {
                                Toast.makeText(ctx, e.message ?: "Error al guardar", Toast.LENGTH_LONG).show()
                            } finally { saving = false }
                        }
                    },
                    enabled = !saving && !showSuccess,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White)
                ) { Text(if (saving) "Guardando..." else "Guardar") }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "La palpación se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showSuccess = false },
        onDismiss = { showSuccess = false }
    )
}
