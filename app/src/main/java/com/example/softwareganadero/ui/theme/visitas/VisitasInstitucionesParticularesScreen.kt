package com.example.softwareganadero.ui.theme.visitas

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.softwareganadero.R
import com.example.softwareganadero.data.visitasData.InstitutionRecord
import com.example.softwareganadero.data.visitasData.ParticularRecord
import com.example.softwareganadero.dialogs.SuccessDialogDual
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

enum class VisitType { INSTITUTION, PARTICULAR }

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitasInstitucionesParticularesScreen(
    onBack: () -> Unit,
    // lambdas para institutions
    onGuardarInstitucion: suspend (
        visitorName: String,
        reason: String,
        notes: String?,
        ts: Long,
        tsText: String
    ) -> Unit,
    onRegistrarSalidaInstitucion: suspend (
        id: Long,
        ts: Long,
        tsText: String
    ) -> Unit,
    loadOpenInstituciones: suspend () -> List<InstitutionRecord>,
    // lambdas para particulares
    onGuardarParticular: suspend (
        visitorName: String,
        reason: String,
        notes: String?,
        ts: Long,
        tsText: String
    ) -> Unit,
    onRegistrarSalidaParticular: suspend (
        id: Long,
        ts: Long,
        tsText: String
    ) -> Unit,
    loadOpenParticulares: suspend () -> List<ParticularRecord>
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    // selección tipo
    var selectedType by rememberSaveable { mutableStateOf<VisitType?>(null) }

    // formulario
    var visitorName by rememberSaveable { mutableStateOf("") }
    var reason by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }

    // dropdown
    var openInstituciones by remember { mutableStateOf<List<InstitutionRecord>>(emptyList()) }
    var openParticulares by remember { mutableStateOf<List<ParticularRecord>>(emptyList()) }
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedIdForExit by rememberSaveable { mutableStateOf<Long?>(null) }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    val nameOk = visitorName.isNotBlank() && visitorName.matches(letters)
    val reasonOk = reason.isNotBlank() && reason.matches(letters)

    suspend fun reloadOpen() {
        when (selectedType) {
            VisitType.INSTITUTION -> {
                openInstituciones = loadOpenInstituciones()
            }
            VisitType.PARTICULAR -> {
                openParticulares = loadOpenParticulares()
            }
            null -> Unit
        }
    }

    // cuando cambia el tipo, recargamos el dropdown
    LaunchedEffect(selectedType) {
        selectedIdForExit = null
        reloadOpen()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Instituciones y particulares") },
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
            // RADIO BUTTONS
            item {
                Text("Tipo de visita")
            }
            item {
                Column(Modifier.selectableGroup()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .selectable(
                                selected = selectedType == VisitType.INSTITUTION,
                                onClick = { selectedType = VisitType.INSTITUTION },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == VisitType.INSTITUTION,
                            onClick = { selectedType = VisitType.INSTITUTION }
                        )
                        Text("Institución")
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .selectable(
                                selected = selectedType == VisitType.PARTICULAR,
                                onClick = { selectedType = VisitType.PARTICULAR },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == VisitType.PARTICULAR,
                            onClick = { selectedType = VisitType.PARTICULAR }
                        )
                        Text("Particular")
                    }
                }
            }

            item { Text("Nombre visitante") }
            item {
                TextField(
                    value = visitorName,
                    onValueChange = { s ->
                        if (s.isEmpty() || s.matches(letters)) visitorName = s
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = visitorName.isNotEmpty() && !nameOk,
                    supportingText = {
                        if (visitorName.isNotEmpty() && !nameOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item { Text("Motivo de la visita") }
            item {
                TextField(
                    value = reason,
                    onValueChange = { s ->
                        if (s.isEmpty() || s.matches(letters)) reason = s
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = reason.isNotEmpty() && !reasonOk,
                    supportingText = {
                        if (reason.isNotEmpty() && !reasonOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
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

            // DROPDOWN según el tipo
            item { Text("Seleccionar visita pendiente") }
            item {
                val selectedVisitText = when (selectedType) {
                    VisitType.INSTITUTION -> {
                        val v = openInstituciones.firstOrNull { it.id == selectedIdForExit }
                        v?.let { "${it.visitorName} - ${it.reason} (${it.createdAtText})" }
                    }
                    VisitType.PARTICULAR -> {
                        val v = openParticulares.firstOrNull { it.id == selectedIdForExit }
                        v?.let { "${it.visitorName} - ${it.reason} (${it.createdAtText})" }
                    }
                    null -> null
                }

                val hasList = when (selectedType) {
                    VisitType.INSTITUTION -> openInstituciones.isNotEmpty()
                    VisitType.PARTICULAR -> openParticulares.isNotEmpty()
                    null -> false
                }

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedVisitText
                            ?: when {
                                selectedType == null -> "Selecciona tipo primero"
                                !hasList -> "No hay visitas pendientes"
                                else -> "Elige una visita"
                            },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = lightBlue,
                            unfocusedContainerColor = lightBlue
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        when (selectedType) {
                            VisitType.INSTITUTION -> openInstituciones.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text("${v.visitorName} - ${v.reason} (${v.createdAtText})") },
                                    onClick = {
                                        selectedIdForExit = v.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                            VisitType.PARTICULAR -> openParticulares.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text("${v.visitorName} - ${v.reason} (${v.createdAtText})") },
                                    onClick = {
                                        selectedIdForExit = v.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                            null -> Unit
                        }
                    }
                }
            }

            // BOTÓN GUARDAR
            item {
                Button(
                    onClick = {
                        val type = selectedType ?: run {
                            Toast.makeText(
                                ctx,
                                "Selecciona tipo de visita",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }
                        if (!nameOk) {
                            Toast.makeText(
                                ctx,
                                "Nombre de visitante requerido (solo letras)",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }
                        if (!reasonOk) {
                            Toast.makeText(
                                ctx,
                                "Motivo requerido (solo letras)",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }
                        if (saving || showSuccess) return@Button
                        saving = true

                        val ts = System.currentTimeMillis()
                        val tsText =
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))

                        scope.launch {
                            try {
                                when (type) {
                                    VisitType.INSTITUTION -> onGuardarInstitucion(
                                        visitorName.trim(),
                                        reason.trim(),
                                        notes.ifBlank { null },
                                        ts,
                                        tsText
                                    )
                                    VisitType.PARTICULAR -> onGuardarParticular(
                                        visitorName.trim(),
                                        reason.trim(),
                                        notes.ifBlank { null },
                                        ts,
                                        tsText
                                    )
                                }
                                visitorName = ""
                                reason = ""
                                notes = ""
                                reloadOpen()
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
                ) { Text(if (saving) "Guardando..." else "Guardar") }
            }

            // BOTÓN SALIDA
            item {
                OutlinedButton(
                    onClick = {
                        val type = selectedType ?: run {
                            Toast.makeText(
                                ctx,
                                "Selecciona tipo de visita",
                                Toast.LENGTH_LONG
                            ).show()
                            return@OutlinedButton
                        }
                        val id = selectedIdForExit ?: run {
                            Toast.makeText(
                                ctx,
                                "Selecciona una visita pendiente",
                                Toast.LENGTH_LONG
                            ).show()
                            return@OutlinedButton
                        }
                        val ts = System.currentTimeMillis()
                        val tsText =
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))

                        scope.launch {
                            try {
                                when (type) {
                                    VisitType.INSTITUTION ->
                                        onRegistrarSalidaInstitucion(id, ts, tsText)
                                    VisitType.PARTICULAR ->
                                        onRegistrarSalidaParticular(id, ts, tsText)
                                }
                                Toast.makeText(
                                    ctx,
                                    "Salida registrada",
                                    Toast.LENGTH_LONG
                                ).show()
                                selectedIdForExit = null
                                reloadOpen()
                            } catch (e: Throwable) {
                                Toast.makeText(
                                    ctx,
                                    e.message ?: "Error al registrar salida",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Registrar salida de visita") }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "La visita se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = { showSuccess = false },
        onDismiss = { showSuccess = false }
    )
}
