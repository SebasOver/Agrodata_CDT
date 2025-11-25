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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.potrerosDomain.PastureFenceRepository
import com.example.softwareganadero.viewmodel.potreros.CercasUnificadasViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CercasUnificadasScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val repo = remember { PastureFenceRepository(db) }

    val viewModel: CercasUnificadasViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CercasUnificadasViewModel(
                    db = db,
                    repo = repo
                ) as T
            }
        }
    )

    val lightBlue = Color(0xFFE6F0FA)
    var volteosExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cercas", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Rotacion")
            TextField(
                value = viewModel.rotacion,
                onValueChange = viewModel::onRotacionChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )

            Text("Potrero")
            TextField(
                value = viewModel.potrero,
                onValueChange = viewModel::onPotreroChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )

            Text("Volteos")
            ExposedDropdownMenuBox(
                expanded = volteosExpanded,
                onExpandedChange = { volteosExpanded = !volteosExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = viewModel.volteosSeleccion ?: "Volteos",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = volteosExpanded
                        )
                    },
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
                    viewModel.opcionesVolteos.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt) },
                            onClick = {
                                viewModel.onVolteosSelected(opt)
                                volteosExpanded = false
                            }
                        )
                    }
                }
            }

            Text("Observaciones")
            TextField(
                value = viewModel.notes,
                onValueChange = viewModel::onNotesChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Button(
                onClick = {
                    viewModel.save { msg ->
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                    }
                },
                enabled = !viewModel.saving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E73C8),
                    contentColor = Color.White
                )
            ) {
                Text(
                    if (viewModel.saving) "Guardando..." else "Guardar",
                    fontSize = 16.sp
                )
            }
        }
    }

    SuccessDialogDual(
        show = viewModel.showFenceSuccess,
        title = "Cerca guardada",
        message = "El registro se guardó correctamente.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.dismissSuccess()
            onBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = {
            viewModel.resetForNew()
        },
        onDismiss = { viewModel.dismissSuccess() }
    )
}

