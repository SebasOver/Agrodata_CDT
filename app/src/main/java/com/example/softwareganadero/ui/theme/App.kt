package com.example.softwareganadero.ui.theme

import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
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
    LaunchedEffect(dark) {
        systemUi.setSystemBarsColor(color = Color.Transparent, darkIcons = !dark)
    }
    MaterialTheme(colorScheme = lightColorScheme(
        primary = brandBlue, onPrimary = Color.White,
        background = brandBlue, onBackground = Color.White
    )) {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = "welcome") {
            composable("welcome") {
                WelcomeScreen(
                    nav = nav,
                    onContinue = { name -> nav.navigate("home/${Uri.encode(name)}") } // fallback operador
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
        }
    }
}