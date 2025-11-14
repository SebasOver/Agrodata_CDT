package com.example.softwareganadero.ui.theme

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.softwareganadero.routes.BienvenidaOperarioRoute
import com.example.softwareganadero.routes.DeteccionCelosRoute
import com.example.softwareganadero.routes.EvaluacionesPraderaAguaRoute
import com.example.softwareganadero.routes.PastoreoYCercasRoute
import com.example.softwareganadero.routes.WelcomeRoute
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@RequiresApi(Build.VERSION_CODES.O)
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
        NavHost(navController = nav, startDestination = "welcome") {
            composable("welcome") { WelcomeRoute(nav) }
            composable("home/{name}", arguments = listOf(navArgument("name"){ type = NavType.StringType })) {
                val name = it.arguments?.getString("name").orEmpty()
                HomePlaceholder(name)
            }
            composable("adminExport/{name}", arguments = listOf(navArgument("name"){ type = NavType.StringType })) {
                val name = it.arguments?.getString("name").orEmpty()
                AdminExportScreen(currentUserName = name)
            }
            composable("bienvenida_operario") { BienvenidaOperarioRoute(nav) }


            /*
            composable("corrales") { CorralesRoute(onBack = { nav.popBackStack("bienvenida_operario", false) }) }
            composable("visitas") { VisitasRoute(onBack = { nav.popBackStack("bienvenida_operario", false) }) }
            composable("potreros") { PotrerosRoute(onBack = { nav.popBackStack("bienvenida_operario", false) }) }

            // NUEVO: Cultivos
            composable("cultivos") { CultivosRoute(onBack = { nav.popBackStack("bienvenida_operario", false) }) }
            */

            composable("potreros/precipitacion") {
                val operador = session.operarioActual.orEmpty()
                PrecipitacionScreen(
                    navBack = { nav.popBackStack("potreros", inclusive = false) },
                    currentOperatorName = operador
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
            composable("potreros/pastoreo_cercas") {
                PastoreoYCercasRoute(
                    onBack = { nav.popBackStack("potreros", inclusive = false) }
                )
            }
            composable("potreros/deteccion_celos") {
                DeteccionCelosRoute(onBack = { nav.popBackStack("potreros", inclusive = false) })
            }
            composable("potreros/evaluaciones_pradera_agua") {
                EvaluacionesPraderaAguaRoute(onBack = { nav.popBackStack("potreros", inclusive = false) })
            }
        }
    }
}

