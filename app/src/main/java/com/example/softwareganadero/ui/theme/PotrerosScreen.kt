package com.example.softwareganadero.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softwareganadero.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotrerosScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit // recibe la ruta/destino al que ir
) {
    val sys = rememberSystemUiController()
    LaunchedEffect(Unit) { sys.setSystemBarsColor(Color.White, darkIcons = true) }

    val opciones = listOf(
        "Precipitacion y inventario" to "potreros/precipitacion",
        "Deteccion celos" to "potreros/deteccion_celos",
        "Registro Nacimiento" to "potreros/registro_nacimiento",
        "Evaluacion pradera y agua" to "potreros/evaluaciones_pradera_agua",
        "Pastoreo y estado cercas" to "potreros/pastoreo_cercas",
        "Suplementos" to "potreros/suplementos"
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Potreros", fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(R.drawable.logo_blanco),
                        contentDescription = "Logo",
                        modifier = Modifier.size(44.dp)
                    )


                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { inner ->
        // Lista de botones grandes, estilo de tu diseño
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly, // reparte verticalmente
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            opciones.forEach { (titulo, ruta) ->
                Button(
                    onClick = { onNavigate(ruta) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E73C8), contentColor = Color.White),
                    shape = RoundedCornerShape(40.dp),
                    contentPadding = PaddingValues(vertical = 18.dp, horizontal = 24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp) // alto base
                        .clip(RoundedCornerShape(40.dp))
                        .shadow(4.dp, RoundedCornerShape(40.dp), clip = false)
                ) {
                    Text(titulo, fontFamily = Nunito, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}