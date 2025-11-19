package com.example.softwareganadero.ui.theme.potreros

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
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuplementosScreen(
    onBack: () -> Unit,
    onGuardar: suspend (rotation: String, lot: String, animals: Int, name: String, quantity: Double, ts: Long, tsText: String) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lightBlue = Color(0xFFE6F0FA)

    var rotation by rememberSaveable { mutableStateOf("") }
    var lot by rememberSaveable { mutableStateOf("") }
    var animals by rememberSaveable { mutableStateOf("") }
    var supName by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }

    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Suplementos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
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
            Text("Rotacion")
            TextField(
                value = rotation,
                onValueChange = { rotation = it },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                singleLine = true
            )

            Text("Lote de ganado")
            TextField(
                value = lot,
                onValueChange = { s -> lot = s }, // sin restricción
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text) // ahora texto
            )



            Text("Número de animales")
            TextField(
                value = animals,
                onValueChange = { s -> if (s.isEmpty() || s.all { it.isDigit() }) animals = s },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text("Nombre suplemento")
            TextField(
                value = supName,
                onValueChange = { s ->
                    // Solo letras (incluye acentos y ñ) y espacios
                    if (s.isEmpty() || s.matches(Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]*$"))) {
                        supName = s
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Text("Cantidad")
            TextField(
                value = quantity,
                onValueChange = { s -> if (s.isEmpty() || s.matches(Regex("""\d+(\.\d{0,2})?"""))) quantity = s },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Button(
                onClick = {
                    val r = rotation.trim()
                    val lTxt = lot.trim()                  // <- lote como String
                    val a = animals.toIntOrNull()
                    val n = supName.trim()
                    val q = quantity.toDoubleOrNull()

                    when {
                        r.isEmpty()   -> { Toast.makeText(ctx, "Rotación requerida", Toast.LENGTH_LONG).show(); return@Button }
                        lTxt.isEmpty() -> { Toast.makeText(ctx, "Lote requerido", Toast.LENGTH_LONG).show(); return@Button }
                        a == null     -> { Toast.makeText(ctx, "Número de animales requerido", Toast.LENGTH_LONG).show(); return@Button }
                        n.isEmpty() || n.any { it.isDigit() } ->
                        { Toast.makeText(ctx, "Nombre del suplemento inválido", Toast.LENGTH_LONG).show(); return@Button }
                        q == null     -> { Toast.makeText(ctx, "Cantidad numérica requerida", Toast.LENGTH_LONG).show(); return@Button }
                    }

                    if (saving) return@Button
                    saving = true
                    val ts = System.currentTimeMillis()
                    val tsText = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))

                    scope.launch {
                        try {
                            // Firma: onGuardar(rotation: String, lot: String, animals: Int, name: String, quantity: Double, ts: Long, tsText: String)
                            onGuardar(r, lTxt, a!!, n, q!!, ts, tsText)
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
    }

    // AlertDialog de éxito
    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "El suplemento se registró correctamente.",
        primaryText = "Volver",
        onPrimary = { showSuccess = false; onBack() },
        secondaryText = "Continuar registrando",
        onSecondary = {
            // limpiar y cerrar
            rotation = ""; lot = ""; animals = ""; supName = ""; quantity = ""
            showSuccess = false
        },
        onDismiss = { showSuccess = false }
    )
}
