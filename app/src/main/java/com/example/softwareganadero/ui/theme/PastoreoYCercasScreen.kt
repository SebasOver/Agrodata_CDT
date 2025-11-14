package com.example.softwareganadero.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastoreoYCercasScreen(
    onBack: () -> Unit,
    onGuardarPastoreo: (rotacion: String, potrero: String, animals: Int, createdAtMillis: Long, createdAtText: String) -> Unit,
    onGuardarCercas: (volteos: String, createdAtMillis: Long, createdAtText: String) -> Unit
) {
    val ctx = LocalContext.current
    val lightBlue = Color(0xFFE6F0FA)
    val opcionesVolteos = listOf("1000","3000","5000","7000","9000","11000","13000","15000")
    var animals by rememberSaveable { mutableStateOf("") }

    // Estado Pastoreo
    var rotacion by rememberSaveable { mutableStateOf("") }
    var potrero by rememberSaveable { mutableStateOf("") }

    // Estado Cercas
    var volteosExpanded by rememberSaveable { mutableStateOf(false) }
    var volteosSeleccion by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pastoreo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Logo opcional, reemplaza por tu recurso
                     Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp))
                },
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
            // Sección Pastoreo
            Text("Pastoreo", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Rotacion")
                TextField(
                    value = rotacion,
                    onValueChange = { rotacion = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Text("Potrero")
                TextField(
                    value = potrero,
                    onValueChange = { potrero = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Text("Número de animales")
                TextField(
                    value = animals,
                    onValueChange = { txt -> if (txt.all { it.isDigit() } || txt.isEmpty()) animals = txt },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Button(
                    onClick = {
                        val r = rotacion.trim()
                        val p = potrero.trim()
                        val a = animals.trim()
                        if (r.isEmpty() || p.isEmpty() || a.isEmpty()) {
                            Toast.makeText(ctx, "Completa Rotacion, Potrero y Número de animales", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        val animalsInt = a.toIntOrNull() ?: 0
                        if (animalsInt <= 0) {
                            Toast.makeText(ctx, "Número de animales debe ser mayor que 0", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        val now = System.currentTimeMillis()
                        val nowText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .format(java.time.Instant.ofEpochMilli(now).atZone(java.time.ZoneId.systemDefault()))
                        onGuardarPastoreo(r, p, animalsInt,now, nowText)
                        Toast.makeText(ctx, "Pastoreo guardado", Toast.LENGTH_LONG).show()
                        rotacion = ""
                        potrero = ""
                        animals = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Guardar") }
            }

            Divider(color = Color(0x11000000))

            // Sección Estado cercas
            Text("Estado cercas", style = MaterialTheme.typography.titleMedium)

            ExposedDropdownMenuBox(
                expanded = volteosExpanded,
                onExpandedChange = { volteosExpanded = !volteosExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = volteosSeleccion ?: "Volteos",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = volteosExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
                ExposedDropdownMenu(
                    expanded = volteosExpanded,
                    onDismissRequest = { volteosExpanded = false }
                ) {
                    opcionesVolteos.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                volteosSeleccion = opt
                                volteosExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val v = volteosSeleccion?.trim().orEmpty()
                    if (v.isEmpty()) {
                        Toast.makeText(ctx, "Selecciona un valor de volteos", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    val now = System.currentTimeMillis()
                    val nowText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(java.time.Instant.ofEpochMilli(now).atZone(java.time.ZoneId.systemDefault()))
                    onGuardarCercas(v, now, nowText)
                    Toast.makeText(ctx, "Estado de cercas guardado", Toast.LENGTH_LONG).show()
                    volteosSeleccion = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) { Text("Guardar") }
        }
    }
}