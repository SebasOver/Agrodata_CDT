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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.softwareganadero.R
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.dialogs.SuccessDialogDual
import com.example.softwareganadero.domain.BirthRepository
import com.example.softwareganadero.viewmodel.RegistroNacimientosViewModel
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

    // Crear ViewModel con factory sencilla
    val viewModel: RegistroNacimientosViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegistroNacimientosViewModel(
                    db = db,
                    birthRepo = birthRepo,
                    operatorName = currentOperatorName
                ) as T
            }
        }
    )

    val lightBlue = Color(0xFFE6F0FA)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro nacimientos", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dropdown vacas
            var expand by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expand,
                onExpandedChange = { expand = !expand },
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = viewModel.cowTag ?: "Vaca",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expand) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2E73C8),
                        unfocusedContainerColor = Color(0xFF2E73C8),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )
                ExposedDropdownMenu(
                    expanded = expand,
                    onDismissRequest = { expand = false }
                ) {
                    viewModel.cows.forEach { tag ->
                        DropdownMenuItem(
                            text = { Text(tag) },
                            onClick = {
                                viewModel.onCowSelected(tag)
                                expand = false
                            }
                        )
                    }
                }
            }

            // Cría
            Text("Cría")
            TextField(
                value = viewModel.calfTag,
                onValueChange = viewModel::onCalfChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Sexo
            Text("Sexo")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier.selectable(
                        selected = viewModel.sex == "M",
                        onClick = { viewModel.onSexChanged("M") },
                        role = Role.RadioButton
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = viewModel.sex == "M", onClick = null)
                    Text("Macho", modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.width(24.dp))
                Row(
                    modifier = Modifier.selectable(
                        selected = viewModel.sex == "H",
                        onClick = { viewModel.onSexChanged("H") },
                        role = Role.RadioButton
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = viewModel.sex == "H", onClick = null)
                    Text("Hembra", modifier = Modifier.padding(start = 8.dp))
                }
            }

            // Color
            Text("Color")
            TextField(
                value = viewModel.color,
                onValueChange = viewModel::onColorChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // Peso
            Text("Peso")
            TextField(
                value = viewModel.weight,
                onValueChange = viewModel::onWeightChanged,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Calostro
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calostro")
                Switch(
                    checked = viewModel.colostrum,
                    onCheckedChange = viewModel::onColostrumChanged
                )
            }

            // Observaciones
            Text("Observaciones")
            TextField(
                value = viewModel.notes,
                onValueChange = viewModel::onNotesChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 96.dp, max = 160.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightBlue,
                    unfocusedContainerColor = lightBlue
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveBirth { msg ->
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show()
                    }
                },
                enabled = !viewModel.saving && !viewModel.showSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E73C8),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (viewModel.saving) "Guardando..." else "Guardar",
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    SuccessDialogDual(
        show = viewModel.showSuccess,
        title = "Guardado con éxito",
        message = "El nacimiento se registró correctamente.",
        primaryText = "Volver",
        onPrimary = {
            viewModel.resetAndClearCow()
            navBack()
        },
        secondaryText = "Continuar registrando",
        onSecondary = {
            viewModel.resetForNew()
        },
        onDismiss = { viewModel.dismissSuccess() }
    )
}
