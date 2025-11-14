package com.example.softwareganadero.routes

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.UserRole
import com.example.softwareganadero.domain.AuthRepository
import com.example.softwareganadero.ui.theme.WelcomeScreen
import kotlinx.coroutines.launch

@Composable
fun WelcomeRoute(nav: NavController) {
    val ctx = LocalContext.current
    val db = remember { AgroDatabase.get(ctx) }
    val auth = remember { AuthRepository(db) }
    val scope = rememberCoroutineScope()

    var options by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val fromDb = db.userDao().listActive().map { it.fullName }
        options = if (fromDb.isNotEmpty()) fromDb
        else listOf("Camilo Rodelo","Jesus Gonzalez","Yaith Salazar","Pedro Maria")
    }

    WelcomeScreen(
        options = options,
        selected = selected,
        onSelected = { selected = it },
        onIngresar = {
            val name = selected ?: return@WelcomeScreen
            scope.launch {
                val user = auth.authenticateByName(name)
                if (user != null) {
                    val encoded = Uri.encode(user.fullName)
                    if (user.role == UserRole.ADMIN) {
                        nav.navigate("adminExport/$encoded") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        nav.navigate("bienvenida_operario") {
                            popUpTo("welcome") { inclusive = false }
                        }
                    }
                    // limpiar estado del login por si se vuelve a esta pantalla
                    selected = null
                } else {
                    Toast.makeText(ctx, "Usuario no autorizado", Toast.LENGTH_LONG).show()
                }
            }
        }
    )
}