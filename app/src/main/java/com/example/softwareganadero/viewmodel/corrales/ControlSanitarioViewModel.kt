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
import com.example.softwareganadero.domain.corralesDomains.ControlSanitarioRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class ControlSanitarioViewModel(
    private val db: AgroDatabase,
    private val repo: ControlSanitarioRepository
) : ViewModel() {

    var tratamiento by mutableStateOf("")
        private set
    var animal by mutableStateOf("")
        private set
    var medicamentos by mutableStateOf("")
        private set
    var dosis by mutableStateOf("")
        private set
    var cantidad by mutableStateOf("")
        private set
    var observaciones by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    val cantidadOk: Boolean
        get() = cantidad.isNotBlank() && cantidad.matches(Regex("""\d+(\.\d{1,2})?"""))

    // setters para la UI
    fun onTratamientoChanged(text: String) { tratamiento = text }
    fun onAnimalChanged(text: String) { animal = text }
    fun onMedicamentosChanged(text: String) { medicamentos = text }
    fun onDosisChanged(text: String) { dosis = text }
    fun onCantidadChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d+(\.\d{0,2})?"""))) cantidad = text
    }
    fun onObservacionesChanged(text: String) { observaciones = text }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        val t = tratamiento.trim()
        val a = animal.trim()
        val m = medicamentos.trim()
        val d = dosis.trim()
        val c = cantidad.trim().toDoubleOrNull()
        if (t.isEmpty()) { onError("Tratamiento requerido"); return }
        if (a.isEmpty()) { onError("Animal requerido"); return }
        if (m.isEmpty()) { onError("Medicamentos requeridos"); return }
        if (d.isEmpty()) { onError("Dosis requerida"); return }
        if (c == null) { onError("Cantidad numérica requerida"); return }
        if (saving || showSuccess) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    treatment = t,
                    animal = a,
                    medicines = m,
                    dose = d,
                    quantity = c,
                    observations = observaciones.ifBlank { null },
                    ts = ts,
                    tsText = tsText
                )
                tratamiento = ""
                animal = ""
                medicamentos = ""
                dosis = ""
                cantidad = ""
                observaciones = ""
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