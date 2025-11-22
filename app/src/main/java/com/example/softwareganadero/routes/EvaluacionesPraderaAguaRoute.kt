package com.example.softwareganadero.routes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.PastureEvaluationRepository
import com.example.softwareganadero.domain.potrerosDomain.WaterEvaluationRepository
import com.example.softwareganadero.ui.theme.potreros.EvaluacionesPraderaAguaScreen
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EvaluacionesPraderaAguaRoute(nav: NavController) {
    EvaluacionesPraderaAguaScreen(
        onBack = { nav.popBackStack("potreros", inclusive = false) }
    )
}