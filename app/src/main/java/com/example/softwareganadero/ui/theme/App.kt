package com.example.softwareganadero.ui.theme

import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AgrodataApp() {
    val systemUi = rememberSystemUiController()
    val dark = isSystemInDarkTheme()
    val brandBlue = Color(0xFF175C9C)
    val session: SessionViewModel = viewModel()  // <- VM de sesión compartido
    LaunchedEffect(dark) {
        systemUi.setSystemBarsColor(color = Color.Transparent, darkIcons = !dark)
    }
    MaterialTheme(colorScheme = lightColorScheme(
        primary = brandBlue, onPrimary = Color.White,
        background = brandBlue, onBackground = Color.White
    )) {
        val nav = rememberNavController()
        var operadorActual by rememberSaveable { mutableStateOf<String?>(null) }
        NavHost(navController = nav, startDestination = "welcome") {
            composable("welcome") {
                WelcomeScreen(
                    nav = nav,
                    onContinue = { name ->
                        session.setOperario(name)          // guarda el operario logueado
                        nav.navigate("bienvenida_operario")
                    }
                )
            }
            composable("home/{name}", arguments = listOf(navArgument("name"){ type = NavType.StringType })) {
                val name = it.arguments?.getString("name").orEmpty()
                HomePlaceholder(name)
            }
            composable("adminExport/{name}", arguments = listOf(navArgument("name"){ type = NavType.StringType })) {
                val name = it.arguments?.getString("name").orEmpty()
                AdminExportScreen(currentUserName = name)
            }
            composable("bienvenida_operario") {
                BienvenidaOperarioScreen(
                    onBack = { nav.popBackStack("welcome", inclusive = false) },
                    onOpcionClick = { opcion ->
                        when (opcion.texto) {
                            "Corrales" -> nav.navigate("corrales")
                            "Visitas" -> nav.navigate("visitas")
                            "Potreros" -> nav.navigate("potreros")
                        }
                    }
                )
            }

            // Destinos que abrirán los botones
            composable("corrales") {
                CorralesScreen(
                    onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) },
                    onNavigate = { destino -> nav.navigate(destino) }
                )
            }
            composable("visitas") {
                VisitasScreen(
                    onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) },
                    onNavigate = { destino -> nav.navigate(destino) }
                )
            }
            composable("potreros") {
                // lee el operario actual desde el VM
                val operador = session.operarioActual.orEmpty()
                PotrerosScreen(
                    onBack = { nav.popBackStack("bienvenida_operario", inclusive = false) },
                    onNavigate = { destino ->
                        if (destino == "potreros/registro_nacimiento") {
                            nav.navigate("potreros/registro_nacimiento/${Uri.encode(operador)}")
                        } else {
                            nav.navigate(destino)
                        }
                    }
                )
            }
            composable(
                "potreros/registro_nacimiento/{operatorName}",
                arguments = listOf(navArgument("operatorName"){ type = NavType.StringType })
            ) { backStackEntry ->
                val operatorName = backStackEntry.arguments?.getString("operatorName").orEmpty()
                RegistroNacimientosScreen(
                    navBack = { nav.popBackStack("potreros", inclusive = false) },
                    currentOperatorName = operatorName
                )
            }
    }
}
}