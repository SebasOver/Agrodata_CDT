package com.example.softwareganadero.ui.theme.cultivos

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Alignment
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
fun CultivosScreen(
    onBack: () -> Unit,
    onGuardar: suspend (
        lot: String,
        species: String,
        hasPests: Boolean,
        hasDiseases: Boolean,
        notes: String?,
        ts: Long,
        tsText: String
    ) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    var lot by rememberSaveable { mutableStateOf("") }
    var species by rememberSaveable { mutableStateOf("") }
    var hasPests by rememberSaveable { mutableStateOf(false) }
    var hasDiseases by rememberSaveable { mutableStateOf(false) }
    var notes by rememberSaveable { mutableStateOf("") }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    val lettersAndDigits = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9 ]+$")

    val lotOk = lot.isNotBlank() && lot.matches(lettersAndDigits)
    val speciesOk = species.isNotBlank() && species.matches(letters)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cultivos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            item { Text("Lote") }
            item {
                TextField(
                    value = lot,
                    onValueChange = { s ->
                        if (s.isEmpty() || s.matches(lettersAndDigits)) lot = s
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = lot.isNotEmpty() && !lotOk,
                    supportingText = {
                        if (lot.isNotEmpty() && !lotOk)
                            Text("Solo letras, números y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item { Text("Especie") }
            item {
                TextField(
                    value = species,
                    onValueChange = { s ->
                        if (s.isEmpty() || s.matches(letters)) species = s
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = species.isNotEmpty() && !speciesOk,
                    supportingText = {
                        if (species.isNotEmpty() && !speciesOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Plagas")
                    Switch(
                        checked = hasPests,
                        onCheckedChange = { hasPests = it }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enfermedades")
                    Switch(
                        checked = hasDiseases,
                        onCheckedChange = { hasDiseases = it }
                    )
                }
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
            }

            item {
                Button(
                    onClick = {
                        if (!lotOk) {
                            Toast.makeText(
                                ctx,
                                "Lote requerido (letras y números)",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }
                        if (!speciesOk) {
                            Toast.makeText(
                                ctx,
                                "Especie requerida (solo letras)",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }
                        if (saving || showSuccess) return@Button
                        saving = true

                        val ts = System.currentTimeMillis()
                        val tsText =
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .format(
                                    Instant.ofEpochMilli(ts)
                                        .atZone(ZoneId.systemDefault())
                                )

                        scope.launch {
                            try {
                                onGuardar(
                                    lot.trim(),
                                    species.trim(),
                                    hasPests,
                                    hasDiseases,
                                    notes.ifBlank { null },
                                    ts,
                                    tsText
                                )
                                lot = ""
                                species = ""
                                hasPests = false
                                hasDiseases = false
                                notes = ""
                                showSuccess = true
                            } catch (e: Throwable) {
                                Toast.makeText(
                                    ctx,
                                    e.message ?: "Error al guardar",
                                    Toast.LENGTH_LONG
                                ).show()
                            } finally {
                                saving = false
                            }
                        }
                    },
                    enabled = !saving && !showSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E73C8),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (saving) "Guardando..." else "Guardar")
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "El cultivo se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showSuccess = false },
        onDismiss = { showSuccess = false }
    )
}
