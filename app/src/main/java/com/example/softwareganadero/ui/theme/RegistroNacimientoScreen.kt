package com.example.softwareganadero.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.BirthRepository
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNacimientosScreen(
    navBack: () -> Unit,
    currentOperatorName: String
) {
    val ctx = LocalContext.current
    val db: AgroDatabase = remember { AgroDatabase.get(ctx) }
    val birthRepo = remember { BirthRepository(db) }
    val scope = rememberCoroutineScope()
    var saving by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    // timestamps al momento de guardar (no en remember, así capturan la hora real del click)
    fun nowPair(): Pair<Long,String> {
        val millis = System.currentTimeMillis()
        val text = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(java.time.Instant.ofEpochMilli(millis).atZone(java.time.ZoneId.systemDefault()))
        return millis to text
    }

    // Cargar vacas
    var cows by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) { cows = db.femaleCowDao().listActive().map { it.tag } }

    // Estado
    var cowTag by rememberSaveable { mutableStateOf<String?>(null) }
    var calfTag by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf<String?>(null) } // obliga selección
    var color by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var colostrum by rememberSaveable { mutableStateOf(false) }
    var notes by rememberSaveable { mutableStateOf("") }

    val lightBlue = Color(0xFFE6F0FA)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro nacimientos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = navBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
                actions = { Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier.fillMaxSize().padding(inner).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dropdown vacas
            var expand by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expand, onExpandedChange = { expand = !expand }, modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = cowTag ?: "Vaca",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expand) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2E73C8),
                        unfocusedContainerColor = Color(0xFF2E73C8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )
                ExposedDropdownMenu(expanded = expand, onDismissRequest = { expand = false }) {
                    cows.forEach { tag ->
                        DropdownMenuItem(text = { Text(tag) }, onClick = { cowTag = tag; expand = false })
                    }
                }
            }

            // Cría (solo números)
            Text("Cria")
            TextField(
                value = calfTag,
                onValueChange = { txt -> if (txt.all { it.isDigit() }) calfTag = txt },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = lightBlue, unfocusedContainerColor = lightBlue),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Sexo obligatorio
            Text("Sexo")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.selectable(selected = sex == "M", onClick = { sex = "M" }, role = Role.RadioButton),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = sex == "M", onClick = null)
                    Text("Macho", modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.width(24.dp))
                Row(
                    modifier = Modifier.selectable(selected = sex == "H", onClick = { sex = "H" }, role = Role.RadioButton),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = sex == "H", onClick = null)
                    Text("Hembra", modifier = Modifier.padding(start = 8.dp))
                }
            }

            // Color obligatorio (texto)
            Text("Color")
            TextField(
                value = color,
                onValueChange = { txt ->
                    // Letras (ASCII + acentuadas), espacios y guion opcional
                    val ok = txt.isEmpty() || txt.matches(Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]{0,30}-?[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]{0,30}$"))
                    if (ok) color = txt
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text) // evita teclado numérico
            )

            // Peso (solo números)
            Text("Peso")
            TextField(
                value = weight,
                onValueChange = { txt -> if (txt.all { it.isDigit() } || txt.count { it == '.' } <= 1) weight = txt },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Calostro
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Calostro")
                Switch(checked = colostrum, onCheckedChange = { colostrum = it })
            }

            // Observaciones (opcional)
            Text("Observaciones")
            TextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp), // antes 140.dp
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val cow = cowTag?.trim().orEmpty()
                    val calf = calfTag.trim()
                    val sexVal = sex
                    val colorTxt = color.trim()
                    val weightTxt = weight.trim()

                    when {
                        cow.isEmpty() -> { Toast.makeText(ctx, "Selecciona la vaca", Toast.LENGTH_LONG).show(); return@Button }
                        calf.isEmpty() -> { Toast.makeText(ctx, "Ingresa número de cría", Toast.LENGTH_LONG).show(); return@Button }
                        calf.toLongOrNull() == null -> { Toast.makeText(ctx, "Cría debe ser numérica", Toast.LENGTH_LONG).show(); return@Button }
                        sexVal.isNullOrEmpty() -> { Toast.makeText(ctx, "Selecciona el sexo", Toast.LENGTH_LONG).show(); return@Button }
                        colorTxt.isEmpty() -> { Toast.makeText(ctx, "Color obligatorio", Toast.LENGTH_LONG).show(); return@Button }
                        weightTxt.isEmpty() -> { Toast.makeText(ctx, "Ingresa el peso", Toast.LENGTH_LONG).show(); return@Button }
                        weightTxt.toDoubleOrNull() == null -> { Toast.makeText(ctx, "Peso debe ser numérico", Toast.LENGTH_LONG).show(); return@Button }
                    }

                    if (saving) return@Button
                    saving = true

                    val (millis, text) = nowPair()
                    scope.launch {
                        try {
                            birthRepo.saveBirth(
                                cowTag = cow,
                                calfTag = calf,
                                sex = sexVal!!,
                                color = colorTxt,
                                weight = weightTxt,
                                colostrum = colostrum,
                                notes = notes,
                                operatorName = currentOperatorName,
                                createdAtText = text,
                                createdAtMillis = millis
                            )
                            showSuccess = true // abrir diálogo de éxito
                        } catch (t: Throwable) {
                            Toast.makeText(ctx, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            saving = false
                        }
                    }
                },
                enabled = !saving && !showSuccess,   // evita dobles envíos mientras el diálogo está visible
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White)
            ) { Text(if (saving) "Guardando..." else "Guardar", fontSize = 16.sp) }
        }
    }
    SuccessDialogDual(
        show = showSuccess,
        title = "Guardado con éxito",
        message = "El nacimiento se registró correctamente.",
        primaryText = "Volver",
        onPrimary = {
            // opcional: limpiar lo que quieras antes de salir
            calfTag = ""
            sex = null
            color = ""
            weight = ""
            colostrum = false
            notes = ""
            showSuccess = false
            navBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = {
            // limpiar para un nuevo registro pero mantener la vaca seleccionada
            calfTag = ""
            sex = null
            color = ""
            weight = ""
            colostrum = false
            notes = ""
            showSuccess = false
            // cowTag se conserva
        },
        onDismiss = { showSuccess = false }
    )
}