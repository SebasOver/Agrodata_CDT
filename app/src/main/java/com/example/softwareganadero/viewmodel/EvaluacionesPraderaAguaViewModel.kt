package com.example.softwareganadero.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.PastureEvaluationRepository
import com.example.softwareganadero.domain.potrerosDomain.WaterEvaluationRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
class EvaluacionesPraderaAguaViewModel(
    private val db: AgroDatabase,
    private val pastureRepo: PastureEvaluationRepository,
    private val waterRepo: WaterEvaluationRepository
) : ViewModel() {

    // -------- Estado Pradera --------
    var kind by mutableStateOf("Entrada")
        private set
    var height by mutableStateOf("")
        private set
    val colores = listOf("verde intenso", "verde normal", "verde claro")
    var colorSelected by mutableStateOf<String?>(null)
        private set

    var rotation by mutableStateOf("")
        private set
    var paddock by mutableStateOf("")
        private set
    var entradaFijada by mutableStateOf(false)
        private set

    var showPraderaSuccess by mutableStateOf(false)
        private set

    // -------- Estado Agua --------
    var availability by mutableStateOf<String?>(null)
        private set
    var temperature by mutableStateOf("")
        private set

    var showAguaSuccess by mutableStateOf(false)
        private set

    // -------- Setters para UI --------
    fun onKindChanged(newKind: String) { kind = newKind }

    fun onHeightChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d+(\.\d{0,2})?"""))) {
            height = text
        }
    }

    fun onRotationChanged(text: String) { rotation = text }
    fun onPaddockChanged(text: String) { paddock = text }

    fun onColorSelected(color: String) { colorSelected = color }

    fun onAvailabilitySelected(value: String) { availability = value }

    fun onTemperatureChanged(text: String) {
        if (text.all { it.isDigit() || it == '.' } || text.isEmpty()) {
            temperature = text
        }
    }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    // -------- Acciones --------

    fun savePradera(
        onError: (String) -> Unit,
        onEntradaRegistrada: () -> Unit
    ) {
        val r = rotation.trim()
        val p = paddock.trim()
        if (!entradaFijada) {
            if (r.isEmpty()) { onError("Rotación requerida"); return }
            if (p.isEmpty()) { onError("Potrero requerido"); return }
        }
        val h = height.trim()
        val c = colorSelected?.trim().orEmpty()
        if (h.toDoubleOrNull() == null) { onError("Altura numérica requerida"); return }
        if (c.isEmpty()) { onError("Selecciona un color"); return }

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                pastureRepo.save(
                    kind = kind,
                    rotation = rotation,
                    paddock = paddock,
                    height = h,
                    color = c,
                    ts = ts,
                    tsText = tsText
                )
                // limpiar solo campos variables
                height = ""
                colorSelected = null

                if (kind == "Entrada") {
                    entradaFijada = true
                    kind = "Salida"
                    onEntradaRegistrada()
                } else {
                    showPraderaSuccess = true
                }
            } catch (t: Throwable) {
                onError(t.message ?: "Error al guardar pradera")
            }
        }
    }

    fun saveAgua(onError: (String) -> Unit) {
        val a = availability ?: run {
            onError("Selecciona disponibilidad de agua"); return
        }
        val tVal = temperature.trim().toDoubleOrNull() ?: run {
            onError("Temperatura numérica requerida"); return
        }

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                waterRepo.save(
                    availability = a,
                    temperature = tVal.toString(),
                    ts = ts,
                    tsText = tsText
                )
                availability = null
                temperature = ""
                showAguaSuccess = true
            } catch (t: Throwable) {
                onError(t.message ?: "Error al guardar agua")
            }
        }
    }

    // -------- Dismiss / reset --------
    fun dismissPraderaSuccess() { showPraderaSuccess = false }
    fun dismissAguaSuccess() { showAguaSuccess = false }
}