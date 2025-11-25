package com.example.softwareganadero.routes.potrerosRoutes

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.PastureFenceRepository
import com.example.softwareganadero.ui.theme.potreros.CercasUnificadasScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CercasUnificadasRoute(nav: NavController) {
    CercasUnificadasScreen(
        onBack = { nav.popBackStack("potreros", inclusive = false) }
    )
}