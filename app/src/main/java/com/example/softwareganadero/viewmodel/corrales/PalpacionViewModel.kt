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
import com.example.softwareganadero.domain.corralesDomains.PalpationRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class PalpacionViewModel(
    private val db: AgroDatabase,
    private val repo: PalpationRepository
) : ViewModel() {

    var animalNumber by mutableStateOf("")
        private set
    var pregnancyDays by mutableStateOf("")
        private set
    var observations by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    val numberOk: Boolean
        get() = animalNumber.isNotBlank() && animalNumber.all { it.isDigit() }
    val daysOk: Boolean
        get() = pregnancyDays.isNotBlank() && pregnancyDays.all { it.isDigit() }

    // setters UI
    fun onNumberChanged(text: String) {
        if (text.isEmpty() || text.all { it.isDigit() }) animalNumber = text
    }

    fun onDaysChanged(text: String) {
        if (text.isEmpty() || text.all { it.isDigit() }) pregnancyDays = text
    }

    fun onObservationsChanged(text: String) { observations = text }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        if (!numberOk) { onError("Número de animal requerido y numérico"); return }
        if (!daysOk) { onError("Días de preñez requerido y numérico"); return }
        if (saving || showSuccess) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    animalNumber = animalNumber.trim(),
                    pregnancyDays = pregnancyDays.toInt(),
                    observations = observations.ifBlank { null },
                    ts = ts,
                    tsText = tsText
                )
                animalNumber = ""
                pregnancyDays = ""
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