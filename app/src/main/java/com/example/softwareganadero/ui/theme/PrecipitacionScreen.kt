package com.example.softwareganadero.ui.theme

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.Precipitation
import com.example.softwareganadero.data.PastureInventory
import kotlinx.coroutines.launch


// Repositorio mínimo (usa los DAOs de AgroDatabase)
class PrecipitacionRepository(private val db: AgroDatabase) {
    suspend fun savePrecipitation(amountMm: Double, operator: String, atText: String, atMillis: Long) {
        db.precipitationDao().insert(
            Precipitation(amountMm = amountMm, operatorName = operator, createdAt = atMillis, createdAtText = atText)
        )
    }
    suspend fun savePastureInventory(healthy: Int, sick: Int, total: Int, operator: String, atText: String, atMillis: Long) {
        db.pastureInventoryDao().insert(
            PastureInventory(healthy = healthy, sick = sick, total = total, operatorName = operator, createdAt = atMillis, createdAtText = atText)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrecipitacionScreen(
    navBack: () -> Unit,
    currentOperatorName: String
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { PrecipitacionRepository(db) }
    val scope = rememberCoroutineScope()

    val lightBlue = Color(0xFFE6F0FA)
    val primaryBlue = Color(0xFF2E73C8)

    // ---------- Estado: Precipitación ----------
    var precipMm by rememberSaveable { mutableStateOf("") }
    var savingPrecip by rememberSaveable { mutableStateOf(false) }
    val precipValid = precipMm.isNotBlank() && precipMm.toDoubleOrNull() != null

    // ---------- Estado: Inventario ----------
    var healthy by rememberSaveable { mutableStateOf("") }
    var sick by rememberSaveable { mutableStateOf("") }
    val hInt = healthy.toIntOrNull()
    val sInt = sick.toIntOrNull()
    val total = (hInt ?: 0) + (sInt ?: 0)
    var savingInv by rememberSaveable { mutableStateOf(false) }
    val inventoryValid = hInt != null && sInt != null

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Precipitación e inventario potreros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Image(painterResource(R.drawable.logo_blanco), null, Modifier.size(44.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // ====== Sección: Precipitación ======
            Text("Precipitación", fontWeight = FontWeight.SemiBold, fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally))

            Text("Cantidad (mm)")
            TextField(
                value = precipMm,
                onValueChange = { s -> if (s.isEmpty() || s.matches(Regex("""\d*\.?\d*"""))) precipMm = s },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = precipMm.isNotEmpty() && precipMm.toDoubleOrNull() == null,
                supportingText = { if (precipMm.isNotEmpty() && precipMm.toDoubleOrNull() == null) Text("Ej: 12.3") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Button(
                onClick = {
                    val op = currentOperatorName.trim()
                    //if (op.isBlank()) {
                    //Toast.makeText(ctx, "Operario no disponible. Selecciónalo en inicio.", Toast.LENGTH_LONG).show()
                    //return@Button
                    //}
                    if (!precipValid || savingPrecip) return@Button
                    savingPrecip = true
                    scope.launch {
                        try {
                            val nowMillis = System.currentTimeMillis()
                            val nowText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .format(java.time.Instant.ofEpochMilli(nowMillis)
                                    .atZone(java.time.ZoneId.systemDefault()))
                            repo.savePrecipitation(precipMm.toDouble(), op, nowText, nowMillis)
                            Toast.makeText(ctx, "Precipitación guardada", Toast.LENGTH_SHORT).show()
                            precipMm = ""
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Error: ${e.message ?: "desconocido"}", Toast.LENGTH_LONG).show()
                        } finally { savingPrecip = false }
                    }
                },
                enabled = precipValid && !savingPrecip,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (precipValid) primaryBlue else Color(0xFF9CB9E6),
                    contentColor = Color.White
                )
            ) { Text(if (savingPrecip) "Guardando..." else "Guardar") }

            Spacer(Modifier.height(8.dp))

            // ====== Sección: Inventario potreros ======
            Text(
                "Inventario potreros",
                fontWeight = FontWeight.SemiBold, fontSize = 18.sp,
                textDecoration = TextDecoration.Underline, color = primaryBlue
            )

            Text("Animales sanos")
            TextField(
                value = healthy,
                onValueChange = { s -> if (s.isEmpty() || s.matches(Regex("""\d+"""))) healthy = s },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = healthy.isNotEmpty() && hInt == null,
                supportingText = { if (healthy.isNotEmpty() && hInt == null) Text("Número entero") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Text("Animales enfermos")
            TextField(
                value = sick,
                onValueChange = { s -> if (s.isEmpty() || s.matches(Regex("""\d+"""))) sick = s },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = sick.isNotEmpty() && sInt == null,
                supportingText = { if (sick.isNotEmpty() && sInt == null) Text("Número entero") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Text("Total")
            TextField(
                value = total.toString(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF2F6FD),
                    unfocusedContainerColor = Color(0xFFF2F6FD),
                    disabledContainerColor = Color(0xFFF2F6FD)
                )
            )

            Button(
                onClick = {
                    val op = currentOperatorName.trim()
                    /*if (op.isBlank()) {
                        Toast.makeText(ctx, "Operario no disponible. Selecciónalo en inicio.", Toast.LENGTH_LONG).show()
                        return@Button
                    }*/
                    if (!inventoryValid || savingInv) return@Button
                    savingInv = true
                    scope.launch {
                        try {
                            val nowMillis = System.currentTimeMillis()
                            val nowText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .format(java.time.Instant.ofEpochMilli(nowMillis)
                                    .atZone(java.time.ZoneId.systemDefault()))
                            repo.savePastureInventory(hInt ?: 0, sInt ?: 0, total, op, nowText, nowMillis)
                            Toast.makeText(ctx, "Inventario guardado", Toast.LENGTH_SHORT).show()
                            healthy = ""; sick = ""
                        } catch (e: Exception) {
                            Toast.makeText(ctx, "Error: ${e.message ?: "desconocido"}", Toast.LENGTH_LONG).show()
                        } finally { savingInv = false }
                    }
                },
                enabled = inventoryValid && !savingInv,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (inventoryValid) primaryBlue else Color(0xFF9CB9E6),
                    contentColor = Color.White
                )
            ) { Text(if (savingInv) "Guardando..." else "Guardar") }

            Spacer(Modifier.height(8.dp))
        }
    }
}
