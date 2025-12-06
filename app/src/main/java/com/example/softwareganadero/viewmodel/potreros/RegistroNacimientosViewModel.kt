package com.example.softwareganadero.viewmodel.potreros

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.BirthRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class RegistroNacimientosViewModel(
    private val db: AgroDatabase,
    private val birthRepo: BirthRepository,
    private val operatorName: String
) : ViewModel() {

    // Lista de vacas
    var cows by mutableStateOf<List<String>>(emptyList())
        private set

    // Campos del formulario
    var cowTag by mutableStateOf<String?>(null)
        private set
    var calfTag by mutableStateOf("")
        private set
    var sex by mutableStateOf<String?>(null)
        private set
    var color by mutableStateOf("")
        private set
    var weight by mutableStateOf("")
        private set
    var colostrum by mutableStateOf(false)
        private set
    var notes by mutableStateOf("")
        private set

    // Estado de UI
    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            cows = db.femaleCowDao().listActive().map { it.tag }
        }
    }

    // --------- Setters UI ---------

    fun onCowSelected(tag: String) { cowTag = tag }

    fun onCalfChanged(text: String) {
        if (text.all { it.isDigit() }) calfTag = text
    }

    fun onSexChanged(value: String) { sex = value }

    fun onColorChanged(text: String) {
        val ok = text.isEmpty() || text.matches(
            Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]{0,30}-?[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]{0,30}$")
        )
        if (ok) color = text
    }

    fun onWeightChanged(text: String) {
        if (text.all { it.isDigit() } || text.count { it == '.' } <= 1) {
            weight = text
        }
    }

    fun onColostrumChanged(value: Boolean) { colostrum = value }

    fun onNotesChanged(text: String) { notes = text }

    fun dismissSuccess() { showSuccess = false }

    // --------- Fechas ---------

    private fun nowPair(): Pair<Long, String> {
        val millis = System.currentTimeMillis()
        val text = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()))
        return millis to text
    }

    // --------- Guardar nacimiento (Room + marcado para sync) ---------

    fun saveBirth(onError: (String) -> Unit) {
        val cow = cowTag?.trim().orEmpty()
        val calf = calfTag.trim()
        val sexVal = sex
        val colorTxt = color.trim()
        val weightTxt = weight.trim()

        when {
            cow.isEmpty() -> { onError("Selecciona la vaca"); return }
            calf.isEmpty() -> { onError("Ingresa número de cría"); return }
            calf.toLongOrNull() == null -> { onError("Cría debe ser numérica"); return }
            sexVal.isNullOrEmpty() -> { onError("Selecciona el sexo"); return }
            colorTxt.isEmpty() -> { onError("Color obligatorio"); return }
            weightTxt.isEmpty() -> { onError("Ingresa el peso"); return }
            weightTxt.toDoubleOrNull() == null -> { onError("Peso debe ser numérico"); return }
        }

        if (saving) return
        saving = true

        val (millis, text) = nowPair()

        viewModelScope.launch {
            try {
                birthRepo.saveBirth(
                    cowTag = cow,
                    calfTag = calf,
                    sex = sexVal!!,
                    color = colorTxt,
                    weight = weightTxt,
                    colostrum = colostrum,
                    notes = notes.ifBlank { null },
                    operatorName = operatorName,
                    createdAt = millis,        // nombre correcto
                    createdAtText = text       // nombre correcto
                )
                // Opcional: limpiar campos para siguiente registro
                resetForNew()
                showSuccess = true
            } catch (t: Throwable) {
                onError(t.message ?: "Error desconocido")
            } finally {
                saving = false
            }
        }
    }

    // --------- Sincronizar con Firestore ---------

    fun syncBirths(
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                birthRepo.syncBirthsTwoWay()
                onSuccess()
            } catch (t: Throwable) {
                onError(t.message ?: "Error al sincronizar nacimientos")
            }
        }
    }

    // --------- Reset ---------

    fun resetForNew() {
        calfTag = ""
        sex = null
        color = ""
        weight = ""
        colostrum = false
        notes = ""
        showSuccess = false
        // se mantiene cowTag para registrar varios de la misma vaca
    }

    fun resetAndClearCow() {
        resetForNew()
        cowTag = null
    }
}