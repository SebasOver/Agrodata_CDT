package com.example.softwareganadero.ui.theme

import android.net.Uri

object Routes {
    const val Welcome = "welcome"
    const val BienvenidaPattern = "bienvenida_operario/{operatorName}"
    fun Bienvenida(operatorName: String) = "bienvenida_operario/${Uri.encode(operatorName)}"

    const val PotrerosPattern = "potreros/{operatorName}"
    fun Potreros(operatorName: String) = "potreros/${Uri.encode(operatorName)}"

    const val RNPattern = "potreros/registro_nacimiento/{operatorName}"
    fun RN(operatorName: String) = "potreros/registro_nacimiento/${Uri.encode(operatorName)}"
}