package com.example.softwareganadero.ui.theme.potreros

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.potrerosDomain.PastureEvaluationRepository
import com.example.softwareganadero.domain.potrerosDomain.WaterEvaluationRepository
import com.example.softwareganadero.viewmodel.potreros.EvaluacionesPraderaAguaViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EvaluacionesPraderaAguaScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val pastureRepo = remember { PastureEvaluationRepository(db) }
    val waterRepo = remember { WaterEvaluationRepository(db) }

    val viewModel: EvaluacionesPraderaAguaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EvaluacionesPraderaAguaViewModel(
                    db = db,
                    pastureRepo = pastureRepo,
                    waterRepo = waterRepo
                ) as T
            }
        }
    )

    val lightBlue = Color(0xFFE6F0FA)
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val indexAgua = 9  // igual que tenías

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Evaluaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Evaluacion pradera", style = MaterialTheme.typography.titleMedium)
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    RadioWithLabel(
                        "Entrada",
                        selected = viewModel.kind == "Entrada"
                    ) { viewModel.onKindChanged("Entrada") }
                    RadioWithLabel(
                        "Salida",
                        selected = viewModel.kind == "Salida"
                    ) { viewModel.onKindChanged("Salida") }
                }
            }

            // Rotación y Potrero visibles solo si no se fijaron
            item {
                AnimatedVisibility(
                    visible = !viewModel.entradaFijada,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Rotacion")
                        TextField(
                            value = viewModel.rotation,
                            onValueChange = viewModel::onRotationChanged,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = lightBlue,
                                unfocusedContainerColor = lightBlue
                            ),
                            singleLine = true
                        )
                        Text("Potrero")
                        TextField(
                            value = viewModel.paddock,
                            onValueChange = viewModel::onPaddockChanged,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = lightBlue,
                                unfocusedContainerColor = lightBlue
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Resumen compacto cuando ya se fijaron
            item {
                AnimatedVisibility(
                    visible = viewModel.entradaFijada,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7F9FC), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Rotación: ${viewModel.rotation}")
                        Text("Potrero: ${viewModel.paddock}")
                    }
                }
            }

            item {
                Text("Altura")
                TextField(
                    value = viewModel.height,
                    onValueChange = viewModel::onHeightChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                var colorExpanded by rememberSaveable { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = colorExpanded,
                    onExpandedChange = { colorExpanded = !colorExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = viewModel.colorSelected ?: "Color",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = colorExpanded
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
                        expanded = colorExpanded,
                        onDismissRequest = { colorExpanded = false }
                    ) {
                        viewModel.colores.forEach { c ->
                            DropdownMenuItem(
                                text = { Text(c) },
                                onClick = {
                                    viewModel.onColorSelected(c)
                                    colorExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.savePradera(
                            onError = { msg ->
                                Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                            },
                            onEntradaRegistrada = {
                                Toast.makeText(
                                    ctx,
                                    "Entrada registrada. Continúa con la salida.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E73C8),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Guardar") }
            }

            item { Divider(color = Color(0x11000000)) }

            // Evaluación agua
            stickyHeader {
                Surface(color = Color.White) {
                    Text(
                        "Evaluacion agua",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    listOf("Escaso", "Normal", "Suficiente").forEach { label ->
                        RadioWithLabel(
                            label,
                            selected = viewModel.availability == label
                        ) { viewModel.onAvailabilitySelected(label) }
                    }
                }
            }

            item {
                Text("Temperatura")
                TextField(
                    value = viewModel.temperature,
                    onValueChange = viewModel::onTemperatureChanged,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            item {
                Button(
                    onClick = {
                        viewModel.saveAgua { msg ->
                            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E73C8),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Guardar") }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = viewModel.showPraderaSuccess,
        title = "Salida guardada",
        message = "Ahora registra la evaluación de agua.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.dismissPraderaSuccess()
            onBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = {
            viewModel.dismissPraderaSuccess()
            scope.launch { listState.animateScrollToItem(indexAgua) }
        },
        onDismiss = { viewModel.dismissPraderaSuccess() }
    )

    SuccessDialogDual(
        show = viewModel.showAguaSuccess,
        title = "Agua guardada",
        message = "Se registró correctamente.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.dismissAguaSuccess()
            onBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = { viewModel.dismissAguaSuccess() },
        onDismiss = { viewModel.dismissAguaSuccess() }
    )
}


@Composable
private fun RadioWithLabel(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.selectable(selected = selected, onClick = onClick, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text, modifier = Modifier.padding(start = 6.dp))
    }
}
