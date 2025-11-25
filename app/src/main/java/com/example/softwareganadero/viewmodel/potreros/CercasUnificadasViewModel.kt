package com.example.softwareganadero.viewmodel.potreros

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.PastureFenceRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class CercasUnificadasViewModel(
    private val db: AgroDatabase,
    private val repo: PastureFenceRepository
) : ViewModel() {

    val opcionesVolteos = listOf("1000","3000","5000","7000","9000","11000","13000","15000")

    var rotacion by mutableStateOf("")
        private set
    var potrero by mutableStateOf("")
        private set
    var volteosSeleccion by mutableStateOf<String?>(null)
        private set
    var notes by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showFenceSuccess by mutableStateOf(false)
        private set

    fun onRotacionChanged(text: String) { rotacion = text }
    fun onPotreroChanged(text: String) { potrero = text }
    fun onVolteosSelected(value: String) { volteosSeleccion = value }
    fun onNotesChanged(text: String) { notes = text }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        val r = rotacion.trim()
        val p = potrero.trim()
        val v = volteosSeleccion?.trim().orEmpty()
        when {
            r.isEmpty() -> { onError("Ingresa rotación"); return }
            p.isEmpty() -> { onError("Ingresa potrero"); return }
            v.isEmpty() -> { onError("Selecciona volteos"); return }
        }
        if (saving) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    rotacion = r,
                    potrero = p,
                    volteos = v,
                    notes = notes.ifBlank { null },
                    ts = ts,
                    tsText = tsText
                )
                // limpieza de campos
                rotacion = ""
                potrero = ""
                volteosSeleccion = null
                notes = ""
                showFenceSuccess = true
            } catch (e: Throwable) {
                onError(e.message ?: "Error al guardar")
            } finally {
                saving = false
            }
        }
    }

    fun resetForNew() {
        rotacion = ""
        potrero = ""
        volteosSeleccion = null
        notes = ""
        showFenceSuccess = false
    }

    fun dismissSuccess() {
        showFenceSuccess = false
    }
}