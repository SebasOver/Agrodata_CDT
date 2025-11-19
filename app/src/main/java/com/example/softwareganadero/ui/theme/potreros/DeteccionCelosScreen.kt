package com.example.softwareganadero.ui.theme.potreros

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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.softwareganadero.R
import com.example.softwareganadero.dialogs.SuccessDialogDual
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeteccionCelosScreen(
    onBack: () -> Unit,
    loadCows: suspend () -> List<String>,
    onGuardar: suspend (inHeat: Boolean, cowTag: String?, notes: String?, createdAtMillis: Long, createdAtText: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    var inHeat by rememberSaveable { mutableStateOf(false) }
    var cows by remember { mutableStateOf<List<String>>(emptyList()) }
    var cowExpanded by rememberSaveable { mutableStateOf(false) }
    var cowSelected by rememberSaveable { mutableStateOf<String?>(null) }
    var notes by rememberSaveable { mutableStateOf("") }
    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        cows = try { loadCows() } catch (_: Throwable) { emptyList() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Deteccion celos", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Hay vacas en celo?")
                Switch(checked = inHeat, onCheckedChange = { inHeat = it })
            }

            if (inHeat) {
                ExposedDropdownMenuBox(
                    expanded = cowExpanded,
                    onExpandedChange = { cowExpanded = !cowExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = cowSelected ?: "Vaca",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(cowExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = lightBlue,
                            unfocusedContainerColor = lightBlue
                        )
                    )
                    ExposedDropdownMenu(expanded = cowExpanded, onDismissRequest = { cowExpanded = false }) {
                        cows.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag) },
                                onClick = { cowSelected = tag; cowExpanded = false }
                            )
                        }
                    }
                }
            }

            Text("Observaciones")
            TextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Button(
                onClick = {
                    val cow = if (inHeat) cowSelected?.trim().orEmpty() else null
                    val n = notes.trim()
                    if (inHeat) {
                        if (cow.isNullOrEmpty()) {
                            Toast.makeText(ctx, "Selecciona la vaca en celo", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                    } else {
                        if (n.isEmpty()) {
                            Toast.makeText(ctx, "Ingresa observaciones", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                    }
                    if (saving) return@Button
                    saving = true

                    val ts = System.currentTimeMillis()
                    val tsText = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
                    scope.launch {
                        try {
                            onGuardar(inHeat, cow, if (n.isEmpty()) null else n, ts, tsText)
                            // Limpieza tras guardar: este caso sí limpia el dropdown
                            inHeat = false
                            cowSelected = null
                            notes = ""
                            showSuccess = true
                        } catch (t: Throwable) {
                            Toast.makeText(ctx, t.message ?: "Error al guardar", Toast.LENGTH_LONG).show()
                        } finally {
                            saving = false
                        }
                    }
                },
                enabled = !saving && !showSuccess,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) { Text(if (saving) "Guardando..." else "Guardar") }
        }
    }
    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "La detección de celo se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = {
            // ya se limpió al guardar; solo cierra el diálogo
            showSuccess = false
        },
        onDismiss = { showSuccess = false }
    )
}
