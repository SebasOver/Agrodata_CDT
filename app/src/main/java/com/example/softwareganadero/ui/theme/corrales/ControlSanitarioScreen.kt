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
fun ControlSanitarioScreen(
    onBack: () -> Unit,
    onGuardar: suspend (tratamiento: String, animal: String, medicamentos: String, dosis: String, cantidad: Double, obs: String?, ts: Long, tsText: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    var tratamiento by rememberSaveable { mutableStateOf("") }
    var animal by rememberSaveable { mutableStateOf("") }
    var medicamentos by rememberSaveable { mutableStateOf("") }
    var dosis by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("") }
    var observaciones by rememberSaveable { mutableStateOf("") }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    val cantidadOk = cantidad.isNotBlank() && cantidad.matches(Regex("""\d+(\.\d{1,2})?"""))

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Control sanitario", fontWeight = FontWeight.Bold) },
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
            item { Text("Tratamiento") }
            item {
                TextField(
                    value = tratamiento,
                    onValueChange = { tratamiento = it }, // alfanumérico libre
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Animal") }
            item {
                TextField(
                    value = animal,
                    onValueChange = { animal = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Medicamentos") }
            item {
                TextField(
                    value = medicamentos,
                    onValueChange = { medicamentos = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Dosis") }
            item {
                TextField(
                    value = dosis,
                    onValueChange = { dosis = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Cantidad") }
            item {
                TextField(
                    value = cantidad,
                    onValueChange = { s -> if (s.isEmpty() || s.matches(Regex("""\d+(\.\d{0,2})?"""))) cantidad = s },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = cantidad.isNotEmpty() && !cantidadOk,
                    supportingText = { if (cantidad.isNotEmpty() && !cantidadOk) Text("Número, hasta 2 decimales") }
                )
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
            }

            item {
                Button(
                    onClick = {
                        val t = tratamiento.trim()
                        val a = animal.trim()
                        val m = medicamentos.trim()
                        val d = dosis.trim()
                        val c = cantidad.trim().toDoubleOrNull()
                        if (t.isEmpty()) { Toast.makeText(ctx, "Tratamiento requerido", Toast.LENGTH_LONG).show(); return@Button }
                        if (a.isEmpty()) { Toast.makeText(ctx, "Animal requerido", Toast.LENGTH_LONG).show(); return@Button }
                        if (m.isEmpty()) { Toast.makeText(ctx, "Medicamentos requeridos", Toast.LENGTH_LONG).show(); return@Button }
                        if (d.isEmpty()) { Toast.makeText(ctx, "Dosis requerida", Toast.LENGTH_LONG).show(); return@Button }
                        if (c == null) { Toast.makeText(ctx, "Cantidad numérica requerida", Toast.LENGTH_LONG).show(); return@Button }
                        if (saving || showSuccess) return@Button
                        saving = true

                        val ts = System.currentTimeMillis()
                        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
                        scope.launch {
                            try {
                                onGuardar(t, a, m, d, c, observaciones.ifBlank { null }, ts, tsText)
                                // limpieza para continuar registrando
                                tratamiento = ""; animal = ""; medicamentos = ""; dosis = ""; cantidad = ""; observaciones = ""
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

    // Diálogo dual
    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "El control sanitario se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showSuccess = false /* ya se limpiaron campos tras guardar */ },
        onDismiss = { showSuccess = false }
    )
}
