package com.example.softwareganadero.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.softwareganadero.export.AdminExportScreen
import com.example.softwareganadero.routes.BienvenidaOperarioRoute
import com.example.softwareganadero.routes.CercasUnificadasRoute
import com.example.softwareganadero.routes.CorralesRoute
import com.example.softwareganadero.routes.DeteccionCelosRoute
import com.example.softwareganadero.routes.EvaluacionesPraderaAguaRoute
import com.example.softwareganadero.routes.PotrerosRoute
import com.example.softwareganadero.routes.PrecipitacionRoute
import com.example.softwareganadero.routes.RegistroNacimientosRoute
import com.example.softwareganadero.routes.SuplementosRoute
import com.example.softwareganadero.routes.VisitasRoute
import com.example.softwareganadero.routes.WelcomeRoute
import com.example.softwareganadero.routes.corralesRoutes.ControlSanitarioRoute
import com.example.softwareganadero.routes.corralesRoutes.PalpacionRoute
import com.example.softwareganadero.routes.corralesRoutes.PesajeRoute
import com.example.softwareganadero.routes.corralesRoutes.TriageRoute
import com.example.softwareganadero.routes.cultivosRoutes.CultivosRoute
import com.example.softwareganadero.routes.institucionesRoutes.VisitasInstitucionesParticularesRoute
import com.example.softwareganadero.viewmodel.SessionViewModel
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
            // Login
            composable("welcome") { WelcomeRoute(nav) }

            // Admin (se mantiene)
            composable(
                "adminExport/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) {
                val name = it.arguments?.getString("name").orEmpty()
                AdminExportScreen(
                    currentUserName = name,
                    onBack = {
                        nav.navigate("welcome") {
                            popUpTo("welcome") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    adminEmail = "johansebastiantarazonadiaz@gmail.com"
                )
            }

            // Menú operario
            composable("bienvenida_operario") { BienvenidaOperarioRoute(nav) }

            // Menús principales
            composable("corrales") { CorralesRoute(nav) }
            composable("visitas") { VisitasRoute(nav) }
            composable("potreros") { PotrerosRoute(nav) }
            composable("cultivos") { CultivosRoute(nav) }

            // Potreros - subrutas
            composable("potreros/precipitacion") {
                val operador = session.operarioActual.orEmpty()  // o donde lo tengas
                PrecipitacionRoute(nav = nav, currentOperatorName = operador)
            }
            composable("potreros/registro_nacimiento") {
                val operador = session.operarioActual.orEmpty()
                RegistroNacimientosRoute(
                    nav = nav,
                    currentOperatorName = operador
                )
            }
            composable("potreros/cercas") { CercasUnificadasRoute(nav) }
            composable("potreros/deteccion_celos") {
                DeteccionCelosRoute(onBack = { nav.popBackStack("potreros", inclusive = false) })
            }
            composable("potreros/evaluaciones_pradera_agua") {
                EvaluacionesPraderaAguaRoute(nav = nav) // sin onBack aquí
            }
            composable("potreros/suplementos") { SuplementosRoute(nav) }
            // Corrales - subrutas
            composable("corrales/control_sanitario"){ ControlSanitarioRoute(nav) }
            composable("corrales/pesaje") { PesajeRoute(nav) }
            composable("corrales/palpacion") { PalpacionRoute(nav) }
            composable("corrales/triage") { TriageRoute(nav) }
            // Visitas - subrutas
            composable("visitas/instituciones_particulares") {
                VisitasInstitucionesParticularesRoute(nav)
            }
        }
    }
}
