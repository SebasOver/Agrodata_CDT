package com.example.softwareganadero.ui.theme.corrales

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.corralesDomains.ControlSanitarioRepository
import com.example.softwareganadero.viewmodel.corrales.ControlSanitarioViewModel
import androidx.lifecycle.ViewModelProvider
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlSanitarioScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { ControlSanitarioRepository(db) }

    val viewModel: ControlSanitarioViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ControlSanitarioViewModel(
                    db = db,
                    repo = repo
                ) as T
            }
        }
    )

    val lightBlue = Color(0xFFE6F0FA)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Control sanitario", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Image(
                        painterResource(R.drawable.logo_blanco),
                        null,
                        Modifier.size(44.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("Tratamiento") }
            item {
                TextField(
                    value = viewModel.tratamiento,
                    onValueChange = viewModel::onTratamientoChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Animal") }
            item {
                TextField(
                    value = viewModel.animal,
                    onValueChange = viewModel::onAnimalChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Medicamentos") }
            item {
                TextField(
                    value = viewModel.medicamentos,
                    onValueChange = viewModel::onMedicamentosChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Dosis") }
            item {
                TextField(
                    value = viewModel.dosis,
                    onValueChange = viewModel::onDosisChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            }

            item { Text("Cantidad") }
            item {
                TextField(
                    value = viewModel.cantidad,
                    onValueChange = viewModel::onCantidadChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = viewModel.cantidad.isNotEmpty() && !viewModel.cantidadOk,
                    supportingText = { if (viewModel.cantidad.isNotEmpty() && !viewModel.cantidadOk) Text("Número, hasta 2 decimales") }
                )
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = viewModel.observaciones,
                    onValueChange = viewModel::onObservacionesChanged,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.save { msg ->
                            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = !viewModel.saving && !viewModel.showSuccess,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White)
                ) { Text(if (viewModel.saving) "Guardando..." else "Guardar") }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = viewModel.showSuccess,
        title = "Guardado con éxito",
        message = "El control sanitario se registró correctamente.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.dismissSuccess()
            onBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = { viewModel.dismissSuccess() },
        onDismiss = { viewModel.dismissSuccess() }
    )
}
