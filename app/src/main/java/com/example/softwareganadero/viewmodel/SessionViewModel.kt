package com.example.softwareganadero.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SessionViewModel : ViewModel() {
    // Operario autenticado; null hasta que Welcome establezca el valor
    var operarioActual by mutableStateOf<String?>(null)
        private set
    fun setOperario(nombre: String) {
        operarioActual = nombre
    }

    fun clear() { operarioActual = null }
}