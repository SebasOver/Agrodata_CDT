package com.example.softwareganadero.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EvaluacionesPraderaAguaScreen(
    onBack: () -> Unit,
    onGuardarPradera: suspend (kind: String, rotation: String, paddock: String, height: String, color: String, ts: Long, tsText: String) -> Unit,
    onGuardarAgua: suspend (availability: String, temperature: String, ts: Long, tsText: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)
    val indexAgua = 9
    //Mostrar mensaje confirmacion
    val listState = rememberLazyListState()
    var showPraderaSuccess by rememberSaveable { mutableStateOf(false) }
    var showAguaSuccess by rememberSaveable { mutableStateOf(false) }
    // Estado Pradera
    var kind by rememberSaveable { mutableStateOf("Entrada") }        // Entrada/Salida
    var height by rememberSaveable { mutableStateOf("") }
    val colores = listOf("verde intenso","verde normal","verde claro")
    var colorExpanded by rememberSaveable { mutableStateOf(false) }
    var colorSelected by rememberSaveable { mutableStateOf<String?>(null) }

    // Rotación/Potrero persistentes una sola vez
    var rotation by rememberSaveable { mutableStateOf("") }
    var paddock by rememberSaveable { mutableStateOf("") }
    // Bandera: entrada registrada => ocultar inputs de rotación/potrero
    var entradaFijada by rememberSaveable { mutableStateOf(false) }

    // Estado Agua
    var availability by rememberSaveable { mutableStateOf<String?>(null) }
    var temperature by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Evaluaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                actions = { Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { inner ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Evaluacion pradera", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    RadioWithLabel("Entrada", selected = kind == "Entrada") { kind = "Entrada" }
                    RadioWithLabel("Salida", selected = kind == "Salida") { kind = "Salida" }
                }
            }

            // Rotación y Potrero: visibles solo si aún no se fijaron
            item {
                AnimatedVisibility(
                    visible = !entradaFijada,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Rotacion")
                        TextField(
                            value = rotation,
                            onValueChange = { rotation = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                            singleLine = true
                        )
                        Text("Potrero")
                        TextField(
                            value = paddock,
                            onValueChange = { paddock = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                            singleLine = true
                        )
                    }
                }
            }

            // Si ya se fijaron, muestra resumen compacto para que “Agua” suba
            item {
                AnimatedVisibility(
                    visible = entradaFijada,
                    enter = fadeIn(), exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7F9FC), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Rotación: $rotation")
                        Text("Potrero: $paddock")
                    }
                }
            }

            item {
                Text("Altura")
                TextField(
                    value = height,
                    onValueChange = { txt -> if (txt.isEmpty() || txt.matches(Regex("""\d+(\.\d{0,2})?"""))) height = txt },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                ExposedDropdownMenuBox(expanded = colorExpanded, onExpandedChange = { colorExpanded = !colorExpanded }, modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = colorSelected ?: "Color",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                    )
                    ExposedDropdownMenu(expanded = colorExpanded, onDismissRequest = { colorExpanded = false }) {
                        colores.forEach { c -> DropdownMenuItem(text = { Text(c) }, onClick = { colorSelected = c; colorExpanded = false }) }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        val r = rotation.trim()
                        val p = paddock.trim()
                        if (!entradaFijada) {
                            if (r.isEmpty()) { Toast.makeText(ctx, "Rotación requerida", Toast.LENGTH_LONG).show(); return@Button }
                            if (p.isEmpty()) { Toast.makeText(ctx, "Potrero requerido", Toast.LENGTH_LONG).show(); return@Button }
                        }
                        val h = height.trim()
                        val hNum = h.toDoubleOrNull() ?: run {
                            Toast.makeText(ctx, "Altura numérica requerida", Toast.LENGTH_LONG).show(); return@Button
                        }
                        val c = colorSelected?.trim().orEmpty()
                        if (c.isEmpty()) { Toast.makeText(ctx, "Selecciona un color", Toast.LENGTH_LONG).show(); return@Button }

                        val ts = System.currentTimeMillis()
                        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
                        scope.launch {
                            try {
                                onGuardarPradera(kind, rotation, paddock, h, c, ts, tsText)
                                height = ""; colorSelected = null
                                if (kind == "Entrada") {
                                    entradaFijada = true
                                    kind = "Salida"
                                    Toast.makeText(ctx, "Entrada registrada. Continúa con la salida.", Toast.LENGTH_SHORT).show()
                                } else {
                                    showPraderaSuccess = true
                                }
                            } catch (t: Throwable) {
                                Toast.makeText(ctx, t.message ?: "Error al guardar pradera", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Guardar") }
            }

            item { Divider(color = Color(0x11000000)) }

            // Evaluación agua
            stickyHeader {
                // Header pegajoso para que Agua se mantenga visible al desplazarse
                Surface(color = Color.White) {
                    Text("Evaluacion agua", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    listOf("Escaso","Normal","Suficiente").forEach { label ->
                        RadioWithLabel(label, selected = availability == label) { availability = label }
                    }
                }
            }

            item {
                Text("Temperatura")
                TextField(
                    value = temperature,
                    onValueChange = { txt -> if (txt.all { it.isDigit() || it == '.' } || txt.isEmpty()) temperature = txt },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                Button(
                    onClick = {
                        val a = availability ?: run {
                            Toast.makeText(ctx, "Selecciona disponibilidad de agua", Toast.LENGTH_LONG).show(); return@Button
                        }
                        val t = temperature.trim().toDoubleOrNull() ?: run {
                            Toast.makeText(ctx, "Temperatura numérica requerida", Toast.LENGTH_LONG).show(); return@Button
                        }
                        val ts = System.currentTimeMillis()
                        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
                        scope.launch {
                            try {
                                onGuardarAgua(a, t.toString(), ts, tsText)
                                availability = null; temperature = ""
                                showAguaSuccess = true
                            } catch (t2: Throwable) {
                                Toast.makeText(ctx, t2.message ?: "Error al guardar agua", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Guardar") }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }
    SuccessDialogDual(
        show = showPraderaSuccess,
        title = "Salida guardada",
        message = "Ahora registra la evaluación de agua.",
        primaryText = "Volver",
        onPrimary = { showPraderaSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = {
            showPraderaSuccess = false
            // desplazar a Agua; ajusta el índice del stickyHeader
            scope.launch { listState.animateScrollToItem(indexAgua) }
        },
        onDismiss = { showPraderaSuccess = false }
    )

// Agua: volver
    SuccessDialogDual(
        show = showAguaSuccess,
        title = "Agua guardada",
        message = "Se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showAguaSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = {
            // dejar listo para otro registro de Agua si deseas
            availability = null; temperature = ""
            showAguaSuccess = false
        },
        onDismiss = { showAguaSuccess = false }
    )
}

@Composable
private fun RadioWithLabel(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text, modifier = Modifier.padding(start = 6.dp))
    }
}
