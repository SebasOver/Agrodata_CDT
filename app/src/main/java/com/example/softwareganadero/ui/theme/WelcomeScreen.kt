package com.example.softwareganadero.ui.theme

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.softwareganadero.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.AgroRepository
import com.example.softwareganadero.data.CsvExporter
import com.example.softwareganadero.data.UserRole
import com.example.softwareganadero.domain.AuthRepository
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen(nav: NavController, onContinue: (String) -> Unit) {
    val logo = painterResource(id = R.drawable.logo_agrodata)
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AgroDatabase.get(ctx) }
    val auth = remember { AuthRepository(db) }

    // Cargar opciones desde Room
    var options by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        val fromDb = db.userDao().listActive().map { it.fullName }
        options = if (fromDb.isNotEmpty()) fromDb
        else listOf("Camilo Rodelo","Jesus Gonzalez","Yaith Salazar","Pedro Maria") // fallback temporal
    }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }

    // Fondo sólido tipo “card” centrada y paddings adaptativos
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .systemBarsPadding()
    ) {
        // Contenido centrado y responsive
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            // Logo escalable
            Image(
                painter = logo,
                contentDescription = "Agrodata",
                modifier = Modifier
                    .sizeIn(minWidth = 120.dp, minHeight = 120.dp)
                    .size( clampLogoSize() )
                    .clip(CircleShape)
            )
            // Campo de nombre
            LaunchedEffect(Unit) {
                val fromDb = db.userDao().listActive().map { it.fullName }
                options = if (fromDb.isNotEmpty()) fromDb
                else listOf("Camilo Rodelo","Jesus Gonzalez","Yaith Salazar","Pedro Maria") // fallback temporal
            }
            OperatorDropdown(onSelected = { selected = it }, selected = selected, options = options)

            Button(
                onClick = {
                    val name = selected ?: return@Button
                    scope.launch {
                        val user = auth.authenticateByName(name)
                        if (user != null) {
                            val encoded = Uri.encode(user.fullName)
                            if (user.role == UserRole.ADMIN) {
                                nav.navigate("adminExport/$encoded") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            } else {
                                nav.navigate("bienvenida_operario") {  // Aquí cambias la ruta
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        } else {
                            Toast.makeText(ctx, "Usuario no autorizado", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = selected != null,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            )
            ) { Text("Ingresar") }
        }
    }
}

@Composable
private fun clampLogoSize(): Dp {
    // Ajuste según ancho de pantalla para mantener proporción agradable
    val width = LocalConfiguration.current.screenWidthDp
    val target = (width * 0.35f).dp
    return target.coerceIn(120.dp, 200.dp)
}


@Composable
fun HomePlaceholder(
    name: String,
    repo: AgroRepository? = null
) {
    val context = LocalContext.current
    val repository = remember(repo, context) {
        repo ?: AgroDatabase.get(context).let { AgroRepository(it) }
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(name) {
        scope.launch { repository.saveProducer(name) }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bienvenido, $name")
        Spacer(Modifier.height(8.dp))
        Text("Panel de operario")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperatorDropdown(
    onSelected: (String) -> Unit,
    selected: String?,
    options: List<String>
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = selected ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Operario") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onSelected(name)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
@Composable
fun AdminExportScreen(currentUserName: String) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { AgroRepository(db) }
    val exporter = remember { CsvExporter(ctx, repo) }
    val scope = rememberCoroutineScope()

    // Verificación defensiva de rol
    LaunchedEffect(currentUserName) {
        val user = db.userDao().findActiveByName(currentUserName)
        if (user?.role != UserRole.ADMIN) {
            Toast.makeText(ctx, "Acceso restringido al administrador", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Panel de administrador: $currentUserName")
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val file = exporter.exportProducersCsv()
                Toast.makeText(ctx, "Exportado: ${file.name}", Toast.LENGTH_LONG).show()
            }
        }) { Text("Exportar datos (CSV)") }
    }
}