package com.example.softwareganadero.viewmodel.cultivos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.cultivosDomains.CropRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class CultivosViewModel(
    private val db: AgroDatabase,
    private val repo: CropRepository
) : ViewModel() {

    private val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    private val lettersAndDigits = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ0-9 ]+$")

    var lot by mutableStateOf("")
        private set
    var species by mutableStateOf("")
        private set
    var hasPests by mutableStateOf(false)
        private set
    var hasDiseases by mutableStateOf(false)
        private set
    var notes by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    val lotOk: Boolean
        get() = lot.isNotBlank() && lot.matches(lettersAndDigits)
    val speciesOk: Boolean
        get() = species.isNotBlank() && species.matches(letters)

    // setters UI
    fun onLotChanged(text: String) {
        if (text.isEmpty() || text.matches(lettersAndDigits)) lot = text
    }

    fun onSpeciesChanged(text: String) {
        if (text.isEmpty() || text.matches(letters)) species = text
    }

    fun onHasPestsChanged(value: Boolean) { hasPests = value }
    fun onHasDiseasesChanged(value: Boolean) { hasDiseases = value }
    fun onNotesChanged(text: String) { notes = text }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        if (!lotOk) {
            onError("Lote requerido (letras y números)")
            return
        }
        if (!speciesOk) {
            onError("Especie requerida (solo letras)")
            return
        }
        if (saving || showSuccess) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    lot = lot.trim(),
                    species = species.trim(),
                    hasPests = hasPests,
                    hasDiseases = hasDiseases,
                    notes = notes.ifBlank { null },
                    ts = ts,
                    tsText = tsText
                )
                // limpiar
                lot = ""
                species = ""
                hasPests = false
                hasDiseases = false
                notes = ""
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