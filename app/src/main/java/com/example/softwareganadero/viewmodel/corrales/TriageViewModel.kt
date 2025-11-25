package com.example.softwareganadero.viewmodel.corrales

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.corralesDomains.TriageRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class TriageViewModel(
    private val db: AgroDatabase,
    private val repo: TriageRepository
) : ViewModel() {

    val locomotionOptions = listOf("Normal","Leve","Moderada","Severa")

    var animalNumber by mutableStateOf("")
        private set
    var temperature by mutableStateOf("")
        private set
    var locomotion by mutableStateOf<String?>(null)
        private set
    var mucosaColor by mutableStateOf("")
        private set
    var observations by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    private val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")

    val numberOk: Boolean
        get() = animalNumber.isNotBlank() && animalNumber.all { it.isDigit() }

    val tempOk: Boolean
        get() = temperature.isNotBlank() && temperature.toDoubleOrNull() != null

    val mucosaOk: Boolean
        get() = mucosaColor.isNotBlank() && mucosaColor.matches(letters)

    val locomotionOk: Boolean
        get() = locomotion != null

    // setters UI
    fun onAnimalNumberChanged(text: String) {
        if (text.isEmpty() || text.all { it.isDigit() }) animalNumber = text
    }

    fun onTemperatureChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d+(\.\d{0,2})?"""))) temperature = text
    }

    fun onLocomotionSelected(value: String) {
        locomotion = value
    }

    fun onMucosaChanged(text: String) {
        if (text.isEmpty() || text.matches(letters)) mucosaColor = text
    }

    fun onObservationsChanged(text: String) {
        observations = text
    }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        if (!numberOk) {
            onError("Número de animal requerido y numérico"); return
        }
        if (!tempOk) {
            onError("Temperatura requerida y numérica"); return
        }
        val loco = locomotion ?: run {
            onError("Selecciona locomoción"); return
        }
        if (!mucosaOk) {
            onError("Color de mucosas requerido (solo letras)"); return
        }
        if (saving || showSuccess) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    animalNumber = animalNumber.trim(),
                    temperature = temperature.toDouble(),
                    locomotion = loco,
                    mucosaColor = mucosaColor.trim(),
                    observations = observations.ifBlank { null },
                    ts = ts,
                    tsText = tsText
                )
                // limpiar para otro registro
                animalNumber = ""
                temperature = ""
                locomotion = null
                mucosaColor = ""
                observations = ""
                showSuccess = true
            } catch (e: Throwable) {
                onError(e.message ?: "Error al guardar")
            } finally {
                saving = false
            }
        }
    }

    fun dismissSuccess() { showSuccess = false }
}