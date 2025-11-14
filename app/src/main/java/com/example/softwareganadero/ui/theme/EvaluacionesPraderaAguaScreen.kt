package com.example.softwareganadero.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionesPraderaAguaScreen(
    onBack: () -> Unit,
    onGuardarPradera: suspend (kind: String, height: String, color: String?, ts: Long, tsText: String) -> Unit,
    onGuardarAgua: suspend (availability: String, temperature: String, ts: Long, tsText: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    // Estado Pradera
    var kind by rememberSaveable { mutableStateOf("Entrada") } // "Entrada" o "Salida"
    var height by rememberSaveable { mutableStateOf("") }
    val colores = listOf("verde intenso","verde normal","verde claro")
    var colorExpanded by rememberSaveable { mutableStateOf(false) }
    var colorSelected by rememberSaveable { mutableStateOf<String?>(null) }

    // Estado Agua
    var availability by rememberSaveable { mutableStateOf<String?>(null) } // Escaso/Normal/Suficiente
    var temperature by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Evaluaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = { Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Evaluación pradera
            Text("Evaluacion pradera", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(
                    modifier = Modifier.selectable(selected = kind == "Entrada", onClick = { kind = "Entrada" }, role = Role.RadioButton),
                    verticalAlignment = Alignment.CenterVertically
                ) { RadioButton(selected = kind == "Entrada", onClick = null); Text("Entrada", modifier = Modifier.padding(start = 6.dp)) }
                Row(
                    modifier = Modifier.selectable(selected = kind == "Salida", onClick = { kind = "Salida" }, role = Role.RadioButton),
                    verticalAlignment = Alignment.CenterVertically
                ) { RadioButton(selected = kind == "Salida", onClick = null); Text("Salida", modifier = Modifier.padding(start = 6.dp)) }
            }

            Text("Altura")
            TextField(
                value = height,
                onValueChange = { txt ->
                    // Acepta entero o decimal con un solo punto
                    val ok = txt.isEmpty() || txt.matches(Regex("""\d+(\.\d{0,2})?"""))
                    if (ok) height = txt
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            ExposedDropdownMenuBox(
                expanded = colorExpanded,
                onExpandedChange = { colorExpanded = !colorExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = colorSelected ?: "Color",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colorExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
                ExposedDropdownMenu(expanded = colorExpanded, onDismissRequest = { colorExpanded = false }) {
                    colores.forEach { c ->
                        DropdownMenuItem(text = { Text(c) }, onClick = { colorSelected = c; colorExpanded = false })
                    }
                }
            }

            Button(
                onClick = {
                    val h = height.trim()
                    val hNum = h.toDoubleOrNull()
                    if (h.isEmpty() || hNum == null) {
                        Toast.makeText(ctx, "Altura numérica requerida", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    val c = colorSelected?.trim().orEmpty()
                    if (c.isEmpty()) {
                        Toast.makeText(ctx, "Selecciona un color", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    val ts = System.currentTimeMillis()
                    val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(java.time.Instant.ofEpochMilli(ts).atZone(java.time.ZoneId.systemDefault()))
                    scope.launch {
                        try {
                            onGuardarPradera(kind, h, c, ts, tsText)
                            Toast.makeText(ctx, "Pradera guardada", Toast.LENGTH_LONG).show()
                            // Limpieza
                            height = ""
                            colorSelected = null
                            // No limpiamos kind para respetar la última selección
                        } catch (t: Throwable) {
                            Toast.makeText(ctx, t.message ?: "Error al guardar pradera", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) { Text("Guardar") }

            Divider(color = Color(0x11000000))

            // Evaluación agua
            Text("Evaluacion agua", style = MaterialTheme.typography.titleMedium)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                listOf("Escaso","Normal","Suficiente").forEach { label ->
                    Row(
                        modifier = Modifier.selectable(
                            selected = availability == label,
                            onClick = { availability = label },
                            role = Role.RadioButton
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = availability == label, onClick = null)
                        Text(label, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }

            Text("Temperatura")
            TextField(
                value = temperature,
                onValueChange = { txt -> if (txt.all { it.isDigit() || it == '.' } || txt.isEmpty()) temperature = txt },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = {
                    val a = availability
                    val t = temperature.trim()
                    if (a.isNullOrEmpty()) {
                        Toast.makeText(ctx, "Selecciona disponibilidad de agua", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (t.isEmpty() || t.toDoubleOrNull() == null) {
                        Toast.makeText(ctx, "Temperatura numérica requerida", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    val ts = System.currentTimeMillis()
                    val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(java.time.Instant.ofEpochMilli(ts).atZone(java.time.ZoneId.systemDefault()))
                    scope.launch {
                        try {
                            onGuardarAgua(a, t, ts, tsText)
                            Toast.makeText(ctx, "Agua guardada", Toast.LENGTH_LONG).show()
                            // Limpieza
                            availability = null
                            temperature = ""
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
    }
}
