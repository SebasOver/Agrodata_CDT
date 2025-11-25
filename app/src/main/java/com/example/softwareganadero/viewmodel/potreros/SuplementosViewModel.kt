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
import com.example.softwareganadero.domain.potrerosDomain.SupplementsRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class SuplementosViewModel(
    private val db: AgroDatabase,
    private val repo: SupplementsRepository,
    private val operatorName: String
) : ViewModel() {

    var rotation by mutableStateOf("")
        private set
    var lot by mutableStateOf("")
        private set
    var animals by mutableStateOf("")
        private set
    var supName by mutableStateOf("")
        private set
    var quantity by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    // -------- setters para la UI --------

    fun onRotationChanged(text: String) { rotation = text }

    fun onLotChanged(text: String) { lot = text }

    fun onAnimalsChanged(text: String) {
        if (text.isEmpty() || text.all { it.isDigit() }) animals = text
    }

    fun onSupNameChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]*$"))) {
            supName = text
        }
    }

    fun onQuantityChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d+(\.\d{0,2})?"""))) {
            quantity = text
        }
    }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val text = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to text
    }

    // -------- guardar --------

    fun save(onError: (String) -> Unit) {
        val r = rotation.trim()
        val lTxt = lot.trim()
        val aInt = animals.toIntOrNull()
        val n = supName.trim()
        val qDouble = quantity.toDoubleOrNull()

        when {
            r.isEmpty() -> { onError("Rotación requerida"); return }
            lTxt.isEmpty() -> { onError("Lote requerido"); return }
            aInt == null -> { onError("Número de animales requerido"); return }
            n.isEmpty() || n.any { it.isDigit() } -> {
                onError("Nombre del suplemento inválido"); return
            }
            qDouble == null -> { onError("Cantidad numérica requerida"); return }
        }

        if (saving) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    rotation = r,
                    lot = lTxt,
                    animalsCount = aInt,
                    name = n,
                    quantity = qDouble,
                    ts = ts,
                    tsText = tsText
                )
                showSuccess = true
            } catch (t: Throwable) {
                onError(t.message ?: "Error al guardar")
            } finally {
                saving = false
            }
        }
    }

    // -------- reset --------

    fun resetForNew() {
        rotation = ""
        lot = ""
        animals = ""
        supName = ""
        quantity = ""
        showSuccess = false
    }

    fun dismissSuccess() {
        showSuccess = false
    }
}