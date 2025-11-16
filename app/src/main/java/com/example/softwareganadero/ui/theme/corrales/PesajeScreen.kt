package com.example.softwareganadero.ui.theme.corrales

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
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
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
fun PesajeScreen(
    onBack: () -> Unit,
    onGuardar: suspend (
        sex: String,                  // "M" o "H"
        number: String,               // solo números
        breed: String,                // solo texto
        color: String,                // solo texto
        cc: String,                   // texto o número
        notes: String?,               // opcional
        ts: Long,
        tsText: String
    ) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    var sex by rememberSaveable { mutableStateOf<String?>(null) }
    var animalNumber by rememberSaveable { mutableStateOf("") }
    var breed by rememberSaveable { mutableStateOf("") }
    var coatColor by rememberSaveable { mutableStateOf("") }
    var cc by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    val onlyLetters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    val onlyDigits = Regex("^\\d+$")

    val numberOk = animalNumber.isNotBlank() && animalNumber.matches(onlyDigits)
    val breedOk = breed.isNotBlank() && breed.matches(onlyLetters)
    val colorOk = coatColor.isNotBlank() && coatColor.matches(onlyLetters)
    val ccOk = cc.isNotBlank() // libre: no vacío
    val sexOk = sex != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pesaje", fontWeight = FontWeight.Bold) },
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
            item {
                Text("Sexo")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Row(
                        modifier = Modifier.selectable(selected = sex == "M", onClick = { sex = "M" }, role = Role.RadioButton),
                        verticalAlignment = Alignment.CenterVertically
                    ) { RadioButton(selected = sex == "M", onClick = null); Text("Macho", modifier = Modifier.padding(start = 6.dp)) }
                    Row(
                        modifier = Modifier.selectable(selected = sex == "H", onClick = { sex = "H" }, role = Role.RadioButton),
                        verticalAlignment = Alignment.CenterVertically
                    ) { RadioButton(selected = sex == "H", onClick = null); Text("Hembra", modifier = Modifier.padding(start = 6.dp)) }
                }
            }

            item { Text("Número animal") }
            item {
                TextField(
                    value = animalNumber,
                    onValueChange = { s -> if (s.isEmpty() || s.matches(Regex("\\d+"))) animalNumber = s },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = animalNumber.isNotEmpty() && !numberOk,
                    supportingText = { if (animalNumber.isNotEmpty() && !numberOk) Text("Solo números") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
            }

            item { Text("Raza") }
            item {
                TextField(
                    value = breed,
                    onValueChange = { s -> if (s.isEmpty() || s.matches(onlyLetters)) breed = s },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = breed.isNotEmpty() && !breedOk,
                    supportingText = { if (breed.isNotEmpty() && !breedOk) Text("Solo letras y espacios") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }

            item { Text("Color") }
            item {
                TextField(
                    value = coatColor,
                    onValueChange = { s -> if (s.isEmpty() || s.matches(onlyLetters)) coatColor = s },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = coatColor.isNotEmpty() && !colorOk,
                    supportingText = { if (coatColor.isNotEmpty() && !colorOk) Text("Solo letras y espacios") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }

            item { Text("C.C (Condición corporal)") }
            item {
                TextField(
                    value = cc,
                    onValueChange = { s -> cc = s }, // libre: número o texto
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = cc.isNotEmpty() && !ccOk,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
            }

            item {
                Button(
                    onClick = {
                        if (!sexOk) { Toast.makeText(ctx, "Selecciona el sexo", Toast.LENGTH_LONG).show(); return@Button }
                        if (!numberOk) { Toast.makeText(ctx, "Número animal requerido (solo números)", Toast.LENGTH_LONG).show(); return@Button }
                        if (!breedOk) { Toast.makeText(ctx, "Raza requerida (solo texto)", Toast.LENGTH_LONG).show(); return@Button }
                        if (!colorOk) { Toast.makeText(ctx, "Color requerido (solo texto)", Toast.LENGTH_LONG).show(); return@Button }
                        if (!ccOk) { Toast.makeText(ctx, "C.C requerido", Toast.LENGTH_LONG).show(); return@Button }
                        if (saving || showSuccess) return@Button
                        saving = true

                        val ts = System.currentTimeMillis()
                        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
                        scope.launch {
                            try {
                                onGuardar(
                                    sex!!,
                                    animalNumber.trim(),
                                    breed.trim(),
                                    coatColor.trim(),
                                    cc.trim(),
                                    notes.ifBlank { null },
                                    ts, tsText
                                )
                                // limpiar para seguir registrando
                                animalNumber = ""; breed = ""; coatColor = ""; cc = ""; notes = ""
                                // mantén el sexo si quieres registrar varios con el mismo valor
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
        message = "El pesaje se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showSuccess = false },
        onDismiss = { showSuccess = false }
    )
}
