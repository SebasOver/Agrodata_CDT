package com.example.softwareganadero.ui.theme.potreros

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.potrerosDomain.PrecipitacionRepository
import com.example.softwareganadero.viewmodel.potreros.PrecipitacionViewModel

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

    val viewModel: PrecipitacionViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PrecipitacionViewModel(
                    db = db,
                    repo = repo,
                    operatorName = currentOperatorName
                ) as T
            }
        }
    )

    val lightBlue = Color(0xFFE6F0FA)
    val primaryBlue = Color(0xFF2E73C8)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Precipitación e inventario animales",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navBack) {
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
        }
    ) { inner ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ====== Precipitación ======
            Text(
                "Precipitación",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text("Cantidad (mm)")
            TextField(
                value = viewModel.precipMm,
                onValueChange = viewModel::onPrecipChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewModel.precipMm.isNotEmpty() &&
                        viewModel.precipMm.toDoubleOrNull() == null,
                supportingText = {
                    if (viewModel.precipMm.isNotEmpty() &&
                        viewModel.precipMm.toDoubleOrNull() == null
                    ) {
                        Text("Ej: 12.3")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Button(
                onClick = {
                    viewModel.savePrecip { msg ->
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                    }
                },
                enabled = viewModel.precipValid && !viewModel.savingPrecip,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.precipValid)
                        primaryBlue else Color(0xFF9CB9E6),
                    contentColor = Color.White
                )
            ) {
                Text(if (viewModel.savingPrecip) "Guardando..." else "Guardar")
            }

            Spacer(Modifier.height(8.dp))

            // ====== Inventario ======
            Text(
                "Inventario animales",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text("Lote de ganado")
            TextField(
                value = viewModel.lot,
                onValueChange = viewModel::onLotChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Text("Animales sanos")
            TextField(
                value = viewModel.healthy,
                onValueChange = viewModel::onHealthyChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewModel.healthy.isNotEmpty() &&
                        viewModel.healthy.toIntOrNull() == null,
                supportingText = {
                    if (viewModel.healthy.isNotEmpty() &&
                        viewModel.healthy.toIntOrNull() == null
                    ) Text("Número entero")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Text("Animales enfermos")
            TextField(
                value = viewModel.sick,
                onValueChange = viewModel::onSickChanged,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = viewModel.sick.isNotEmpty() &&
                        viewModel.sick.toIntOrNull() == null,
                supportingText = {
                    if (viewModel.sick.isNotEmpty() &&
                        viewModel.sick.toIntOrNull() == null
                    ) Text("Número entero")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total")
                Text(
                    text = viewModel.total.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = primaryBlue
                )
            }

            Button(
                onClick = {
                    viewModel.saveInventory { msg ->
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                    }
                },
                enabled = viewModel.inventoryValid && !viewModel.savingInv,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.inventoryValid)
                        primaryBlue else Color(0xFF9CB9E6),
                    contentColor = Color.White
                )
            ) {
                Text(if (viewModel.savingInv) "Guardando..." else "Guardar")
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    SuccessDialogDual(
        show = viewModel.showPrecipSuccess,
        title = "Precipitación guardada",
        message = "Continúa con el inventario de animales.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.dismissPrecipSuccess()
            navBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = { viewModel.dismissPrecipSuccess() },
        onDismiss = { viewModel.dismissPrecipSuccess() }
    )

    SuccessDialogDual(
        show = viewModel.showInvSuccess,
        title = "Inventario guardado",
        message = "Se registró correctamente.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.dismissInvSuccess()
            navBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = { viewModel.dismissInvSuccess() },
        onDismiss = { viewModel.dismissInvSuccess() }
    )
}


