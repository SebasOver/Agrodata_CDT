package com.example.softwareganadero.ui.theme.visitas

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.dialogs.SuccessDialogDual
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.viewmodel.visitas.VisitType
import com.example.softwareganadero.viewmodel.visitas.VisitasInstitucionesParticularesViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitasInstitucionesParticularesScreen(
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val viewModel: VisitasInstitucionesParticularesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VisitasInstitucionesParticularesViewModel(db) as T
            }
        }
    )

    val lightBlue = Color(0xFFE6F0FA)
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Instituciones y particulares") },
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
            item { Text("Tipo de visita") }
            item {
                Column(Modifier.selectableGroup()) {
                    Row(
                        Modifier.fillMaxWidth().height(40.dp)
                            .selectable(
                                selected = viewModel.selectedType == VisitType.INSTITUTION,
                                onClick = { viewModel.onSelectType(VisitType.INSTITUTION) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = viewModel.selectedType == VisitType.INSTITUTION, onClick = { viewModel.onSelectType(VisitType.INSTITUTION) })
                        Text("Institución")
                    }
                    Row(
                        Modifier.fillMaxWidth().height(40.dp)
                            .selectable(
                                selected = viewModel.selectedType == VisitType.PARTICULAR,
                                onClick = { viewModel.onSelectType(VisitType.PARTICULAR) },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = viewModel.selectedType == VisitType.PARTICULAR, onClick = { viewModel.onSelectType(VisitType.PARTICULAR) })
                        Text("Particular")
                    }
                }
            }

            item { Text("Nombre visitante") }
            item {
                TextField(
                    value = viewModel.visitorName,
                    onValueChange = viewModel::onVisitorChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.visitorName.isNotEmpty() && !viewModel.nameOk,
                    supportingText = {
                        if (viewModel.visitorName.isNotEmpty() && !viewModel.nameOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item { Text("Motivo de la visita") }
            item {
                TextField(
                    value = viewModel.reason,
                    onValueChange = viewModel::onReasonChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = viewModel.reason.isNotEmpty() && !viewModel.reasonOk,
                    supportingText = {
                        if (viewModel.reason.isNotEmpty() && !viewModel.reasonOk)
                            Text("Solo letras y espacios")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            item { Text("Observaciones") }
            item {
                TextField(
                    value = viewModel.notes,
                    onValueChange = viewModel::onNotesChanged,
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = lightBlue,
                        unfocusedContainerColor = lightBlue
                    )
                )
            }

            // Dropdown pendiente
            item { Text("Seleccionar visita pendiente") }
            item {
                val selectedVisitText = when (viewModel.selectedType) {
                    VisitType.INSTITUTION -> {
                        val v = viewModel.openInstituciones.firstOrNull { it.id == viewModel.selectedIdForExit }
                        v?.let { "${it.visitorName} - ${it.reason} (${it.createdAtText})" }
                    }
                    VisitType.PARTICULAR -> {
                        val v = viewModel.openParticulares.firstOrNull { it.id == viewModel.selectedIdForExit }
                        v?.let { "${it.visitorName} - ${it.reason} (${it.createdAtText})" }
                    }
                    null -> null
                }
                val hasList = when (viewModel.selectedType) {
                    VisitType.INSTITUTION -> viewModel.openInstituciones.isNotEmpty()
                    VisitType.PARTICULAR -> viewModel.openParticulares.isNotEmpty()
                    null -> false
                }

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = selectedVisitText
                            ?: when {
                                viewModel.selectedType == null -> "Selecciona tipo primero"
                                !hasList -> "No hay visitas pendientes"
                                else -> "Elige una visita"
                            },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = lightBlue,
                            unfocusedContainerColor = lightBlue
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        when (viewModel.selectedType) {
                            VisitType.INSTITUTION -> viewModel.openInstituciones.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text("${v.visitorName} - ${v.reason} (${v.createdAtText})") },
                                    onClick = {
                                        viewModel.onSelectIdForExit(v.id)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                            VisitType.PARTICULAR -> viewModel.openParticulares.forEach { v ->
                                DropdownMenuItem(
                                    text = { Text("${v.visitorName} - ${v.reason} (${v.createdAtText})") },
                                    onClick = {
                                        viewModel.onSelectIdForExit(v.id)
                                        dropdownExpanded = false
                                    }
                                )
                            }
                            null -> Unit
                        }
                    }
                }
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

            // Salida
            item {
                OutlinedButton(
                    onClick = {
                        viewModel.closeVisit { msg ->
                            Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("Registrar salida de visita") }
            }

            item { Spacer(Modifier.height(12.dp)) }
        }
    }

    SuccessDialogDual(
        show = viewModel.showSuccess,
        title = "Guardado con éxito",
        message = "La visita se registró correctamente.",
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
