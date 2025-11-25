package com.example.softwareganadero.ui.theme.corrales

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.corralesDomains.WeighingRepository
import com.example.softwareganadero.viewmodel.corrales.PesajeViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PesajeScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { WeighingRepository(db) }

    val viewModel: PesajeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PesajeViewModel(
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
                title = { Text("Pesaje", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            item {
                Text("Sexo")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        modifier = Modifier.selectable(
                            selected = viewModel.sex == "M",
                            onClick = { viewModel.onSexChanged("M") },
                            role = Role.RadioButton
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = viewModel.sex == "M", onClick = null)
                        Text("Macho", modifier = Modifier.padding(start = 6.dp))
                    }
                    Row(
                        modifier = Modifier.selectable(
                            selected = viewModel.sex == "H",
                            onClick = { viewModel.onSexChanged("H") },
                            role = Role.RadioButton
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = viewModel.sex == "H", onClick = null)
                        Text("Hembra", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }

            item { Text("Número animal") }
            item {
                TextField(
                    value = viewModel.animalNumber,
                    onValueChange = viewModel::onNumberChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = viewModel.animalNumber.isNotEmpty() && !viewModel.numberOk,
                    supportingText = {
                        if (viewModel.animalNumber.isNotEmpty() && !viewModel.numberOk)
                            Text("Solo números")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
            }

            item { Text("Raza") }
            item {
                TextField(
                    value = viewModel.breed,
                    onValueChange = viewModel::onBreedChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.breed.isNotEmpty() && !viewModel.breedOk,
                    supportingText = {
                        if (viewModel.breed.isNotEmpty() && !viewModel.breedOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item { Text("Color") }
            item {
                TextField(
                    value = viewModel.coatColor,
                    onValueChange = viewModel::onColorChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.coatColor.isNotEmpty() && !viewModel.colorOk,
                    supportingText = {
                        if (viewModel.coatColor.isNotEmpty() && !viewModel.colorOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item { Text("C.C (Condición corporal)") }
            item {
                TextField(
                    value = viewModel.cc,
                    onValueChange = viewModel::onCcChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.cc.isNotEmpty() && !viewModel.ccOk,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = viewModel.notes,
                    onValueChange = viewModel::onNotesChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E73C8),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (viewModel.saving) "Guardando..." else "Guardar")
                }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = viewModel.showSuccess,
        title = "Guardado con éxito",
        message = "El pesaje se registró correctamente.",
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
