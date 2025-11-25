package com.example.softwareganadero.routes.corralesRoutes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.corralesDomains.TriageRepository
import com.example.softwareganadero.ui.theme.corrales.TriageScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TriageRoute(nav: NavController) {
    TriageScreen(
        onBack = { nav.popBackStack("corrales", inclusive = false) }
    )
}
