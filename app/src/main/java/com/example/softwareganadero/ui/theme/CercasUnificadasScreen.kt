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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softwareganadero.R
import com.example.softwareganadero.dialogs.SuccessDialogDual

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CercasUnificadasScreen(
    onBack: () -> Unit,
    onGuardar: (rotacion: String, potrero: String, volteos: String, notes: String?, ts: Long, tsText: String) -> Unit
) {
    val ctx = LocalContext.current
    val lightBlue = Color(0xFFE6F0FA)
    val opcionesVolteos = listOf("1000","3000","5000","7000","9000","11000","13000","15000")

    var rotacion by rememberSaveable { mutableStateOf("") }
    var potrero by rememberSaveable { mutableStateOf("") }
    var volteosExpanded by rememberSaveable { mutableStateOf(false) }
    var volteosSeleccion by rememberSaveable { mutableStateOf<String?>(null) }
    var notes by rememberSaveable { mutableStateOf("") }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showFenceSuccess by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cercas", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
                actions = { Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { inner ->
        Column(
            modifier = Modifier.fillMaxSize().padding(inner).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Rotacion")
            TextField(
                value = rotacion, onValueChange = { rotacion = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                singleLine = true
            )

            Text("Potrero")
            TextField(
                value = potrero, onValueChange = { potrero = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                singleLine = true
            )

            Text("Volteos")
            ExposedDropdownMenuBox(expanded = volteosExpanded, onExpandedChange = { volteosExpanded = !volteosExpanded }, modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = volteosSeleccion ?: "Volteos",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = volteosExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
                )
                ExposedDropdownMenu(expanded = volteosExpanded, onDismissRequest = { volteosExpanded = false }) {
                    opcionesVolteos.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) }, onClick = { volteosSeleccion = opt; volteosExpanded = false })
                    }
                }
            }

            Text("Observaciones")
            TextField(
                value = notes, onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth().height(112.dp),
                colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue)
            )

            Button(
                onClick = {
                    val r = rotacion.trim()
                    val p = potrero.trim()
                    val v = volteosSeleccion?.trim().orEmpty()
                    when {
                        r.isEmpty() -> { Toast.makeText(ctx, "Ingresa rotación", Toast.LENGTH_LONG).show(); return@Button }
                        p.isEmpty() -> { Toast.makeText(ctx, "Ingresa potrero", Toast.LENGTH_LONG).show(); return@Button }
                        v.isEmpty() -> { Toast.makeText(ctx, "Selecciona volteos", Toast.LENGTH_LONG).show(); return@Button }
                    }
                    if (saving) return@Button
                    saving = true
                    val ts = System.currentTimeMillis()
                    val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(java.time.Instant.ofEpochMilli(ts).atZone(java.time.ZoneId.systemDefault()))
                    try {
                        onGuardar(r, p, v, notes.ifBlank { null }, ts, tsText)
                        // limpieza de campos
                        rotacion = ""; potrero = ""; volteosSeleccion = null; notes = ""
                        showFenceSuccess = true // abrir diálogo
                    } catch (e: Throwable) {
                        Toast.makeText(ctx, e.message ?: "Error al guardar", Toast.LENGTH_LONG).show()
                    } finally {
                        saving = false
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White)
            ) { Text(if (saving) "Guardando..." else "Guardar", fontSize = 16.sp) }
        }
    }

    // Diálogo de confirmación
    SuccessDialogDual(
        show = showFenceSuccess,
        title = "Cerca guardada",
        message = "El registro se guardó correctamente.",
        onPrimary = { showFenceSuccess = false; onBack() },
        onSecondary = {
            rotacion = ""; potrero = ""; volteosSeleccion = null; notes = ""
            showFenceSuccess = false
        },
        onDismiss = { showFenceSuccess = false }
    )
}
