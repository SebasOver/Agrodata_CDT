package com.example.softwareganadero.viewmodel.corrales

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.corralesDomains.WeighingRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class PesajeViewModel(
    private val db: AgroDatabase,
    private val repo: WeighingRepository
) : ViewModel() {

    var sex by mutableStateOf<String?>(null)
        private set
    var animalNumber by mutableStateOf("")
        private set
    var breed by mutableStateOf("")
        private set
    var coatColor by mutableStateOf("")
        private set
    var cc by mutableStateOf("")
        private set
    var notes by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    private val onlyLetters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    private val onlyDigits = Regex("^\\d+$")

    val numberOk: Boolean
        get() = animalNumber.isNotBlank() && animalNumber.matches(onlyDigits)
    val breedOk: Boolean
        get() = breed.isNotBlank() && breed.matches(onlyLetters)
    val colorOk: Boolean
        get() = coatColor.isNotBlank() && coatColor.matches(onlyLetters)
    val ccOk: Boolean
        get() = cc.isNotBlank()
    val sexOk: Boolean
        get() = sex != null

    // setters UI
    fun onSexChanged(value: String) { sex = value }

    fun onNumberChanged(text: String) {
        if (text.isEmpty() || text.matches(onlyDigits)) animalNumber = text
    }

    fun onBreedChanged(text: String) {
        if (text.isEmpty() || text.matches(onlyLetters)) breed = text
    }

    fun onColorChanged(text: String) {
        if (text.isEmpty() || text.matches(onlyLetters)) coatColor = text
    }

    fun onCcChanged(text: String) { cc = text }

    fun onNotesChanged(text: String) { notes = text }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        if (!sexOk) { onError("Selecciona el sexo"); return }
        if (!numberOk) { onError("Número animal requerido (solo números)"); return }
        if (!breedOk) { onError("Raza requerida (solo texto)"); return }
        if (!colorOk) { onError("Color requerido (solo texto)"); return }
        if (!ccOk) { onError("C.C requerido"); return }
        if (saving || showSuccess) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    sex = sex!!,
                    animalNumber = animalNumber.trim(),
                    breed = breed.trim(),
                    color = coatColor.trim(),
                    bodyCondition = cc.trim(),
                    observations = notes.ifBlank { null },
                    createdAt = ts,
                    createdAtText = tsText
                )
                // limpiar (dejamos el sexo si quieres repetir con el mismo)
                animalNumber = ""
                breed = ""
                coatColor = ""
                cc = ""
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