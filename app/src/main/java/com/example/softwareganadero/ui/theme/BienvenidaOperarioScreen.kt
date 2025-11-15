package com.example.softwareganadero.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.softwareganadero.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienvenidaOperarioScreen(
    onBack: () -> Unit,
    onOpcionClick: (OpcionBienvenida) -> Unit
) {
    val sys = rememberSystemUiController()
    LaunchedEffect(Unit) { sys.setSystemBarsColor(color = Color.White, darkIcons = true) }

    val opciones = listOf(
        OpcionBienvenida("Potreros", R.drawable.potreros),
        OpcionBienvenida("Corrales", R.drawable.corral),
        OpcionBienvenida("Cultivos", R.drawable.cultivos),
        OpcionBienvenida("Visitas", R.drawable.visitas)
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bienvenido", fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Image(painter = painterResource(R.drawable.logo_blanco), contentDescription = "Logo", modifier = Modifier.size(48.dp))
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .background(Color.White)
        ) {
            val targetW = 257.39.dp
            val targetH = 113.dp
            val horizPadding = 16.dp
            val availableW = maxWidth - horizPadding * 2
            val scale = (availableW / targetW).coerceAtMost(1.35f)
            val cardW = targetW * scale
            val cardH = targetH * scale

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizPadding),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                opciones.forEach { opcion ->
                    Card(
                        onClick = { onOpcionClick(opcion) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.width(cardW).height(cardH)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                opcion.texto,
                                fontFamily = Nunito,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = (18.sp * scale.coerceAtMost(1.2f)),
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(12.dp))
                            Image(
                                painter = painterResource(opcion.imgRes),
                                contentDescription = opcion.texto,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(cardH * 0.75f)
                                    .aspectRatio(1.25f)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}

data class OpcionBienvenida(val texto: String, val imgRes: Int)