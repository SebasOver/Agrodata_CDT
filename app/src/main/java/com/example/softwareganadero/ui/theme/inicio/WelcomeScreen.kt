package com.example.softwareganadero.ui.theme.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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


@Composable
fun WelcomeScreen(
    options: List<String>,
    selected: String?,
    onSelected: (String?) -> Unit,
    onIngresar: () -> Unit
) {
    val logo = painterResource(id = R.drawable.logo_agrodata)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Image(
                painter = logo,
                contentDescription = "Agrodata",
                modifier = Modifier
                    .sizeIn(minWidth = 120.dp, minHeight = 120.dp)
                    .size(clampLogoSize())
                    .clip(CircleShape)
            )

            OperatorDropdown(
                onSelected = { onSelected(it) },
                selected = selected,
                options = options
            )

            Button(
                onClick = onIngresar,
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
