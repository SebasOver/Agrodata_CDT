package com.example.softwareganadero.viewmodel.visitas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.visitasData.InstitutionRecord
import com.example.softwareganadero.data.visitasData.ParticularRecord
import com.example.softwareganadero.domain.visitasDomains.InstitutionRepository
import com.example.softwareganadero.domain.visitasDomains.ParticularRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class VisitType { INSTITUTION, PARTICULAR }

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class VisitasInstitucionesParticularesViewModel(
    db: AgroDatabase
) : ViewModel() {
    private val institutionRepo = InstitutionRepository(db)
    private val particularRepo = ParticularRepository(db)

    var selectedType by mutableStateOf<VisitType?>(null)
    var visitorName by mutableStateOf("")
    var reason by mutableStateOf("")
    var notes by mutableStateOf("")

    var openInstituciones by mutableStateOf<List<InstitutionRecord>>(emptyList())
    var openParticulares by mutableStateOf<List<ParticularRecord>>(emptyList())
    var selectedIdForExit by mutableStateOf<Long?>(null)

    var saving by mutableStateOf(false)
        private set
    var showSuccess by mutableStateOf(false)
        private set

    val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
    val nameOk: Boolean
        get() = visitorName.isNotBlank() && visitorName.matches(letters)
    val reasonOk: Boolean
        get() = reason.isNotBlank() && reason.matches(letters)

    init {
        reloadOpen()
    }

    fun onSelectType(type: VisitType) {
        selectedType = type
        selectedIdForExit = null
        reloadOpen()
    }

    fun onVisitorChanged(s: String) { if (s.isEmpty() || s.matches(letters)) visitorName = s }
    fun onReasonChanged(s: String) { if (s.isEmpty() || s.matches(letters)) reason = s }
    fun onNotesChanged(s: String) { notes = s }

    fun onSelectIdForExit(id: Long) {
        selectedIdForExit = id
    }

    fun reloadOpen() {
        viewModelScope.launch {
            when (selectedType) {
                VisitType.INSTITUTION -> openInstituciones = institutionRepo.getOpenVisits()
                VisitType.PARTICULAR -> openParticulares = particularRepo.getOpenVisits()
                null -> { openInstituciones = emptyList(); openParticulares = emptyList() }
            }
            selectedIdForExit = null
        }
    }

    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        val type = selectedType ?: run { onError("Selecciona tipo de visita"); return }
        if (!nameOk) { onError("Nombre de visitante requerido (solo letras)"); return }
        if (!reasonOk) { onError("Motivo requerido (solo letras)"); return }
        if (saving || showSuccess) return
        saving = true

        val (ts, tsText) = nowPair()
        viewModelScope.launch {
            try {
                when (type) {
                    VisitType.INSTITUTION -> institutionRepo.saveEntry(visitorName.trim(), reason.trim(), notes.ifBlank { null }, ts, tsText)
                    VisitType.PARTICULAR -> particularRepo.saveEntry(visitorName.trim(), reason.trim(), notes.ifBlank { null }, ts, tsText)
                }
                visitorName = ""
                reason = ""
                notes = ""
                reloadOpen()
                showSuccess = true
            } catch (e: Throwable) {
                onError(e.message ?: "Error al guardar")
            } finally {
                saving = false
            }
        }
    }

    fun closeVisit(onError: (String) -> Unit) {
        val type = selectedType ?: run { onError("Selecciona tipo de visita"); return }
        val id = selectedIdForExit ?: run { onError("Selecciona una visita pendiente"); return }
        val (ts, tsText) = nowPair()
        viewModelScope.launch {
            try {
                when (type) {
                    VisitType.INSTITUTION -> institutionRepo.closeVisit(id, ts, tsText)
                    VisitType.PARTICULAR -> particularRepo.closeVisit(id, ts, tsText)
                }
                selectedIdForExit = null
                reloadOpen()
            } catch (e: Throwable) {
                onError(e.message ?: "Error al registrar salida")
            }
        }
    }

    fun dismissSuccess() { showSuccess = false }
}