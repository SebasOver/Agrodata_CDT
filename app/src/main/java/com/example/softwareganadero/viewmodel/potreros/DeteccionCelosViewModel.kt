package com.example.softwareganadero.viewmodel.potreros

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.domain.potrerosDomain.HeatDetectionRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
class DeteccionCelosViewModel(
    private val db: AgroDatabase,
    private val repo: HeatDetectionRepository
) : ViewModel() {

    val lightBlue = 0xFFE6F0FA

    var inHeat by mutableStateOf(false)
        private set

    var cows by mutableStateOf<List<String>>(emptyList())
        private set

    var cowSelected by mutableStateOf<String?>(null)
        private set

    var notes by mutableStateOf("")
        private set

    var saving by mutableStateOf(false)
        private set

    var showSuccess by mutableStateOf(false)
        private set

    init {
        // cargar vacas al iniciar
        viewModelScope.launch {
            cows = try {
                repo.listCows()
            } catch (_: Throwable) {
                emptyList()
            }
        }
    }

    // ---- setters para la UI ----
    fun onInHeatChanged(value: Boolean) {
        inHeat = value
        if (!value) cowSelected = null
    }

    fun onCowSelected(tag: String) {
        cowSelected = tag
    }

    fun onNotesChanged(text: String) {
        notes = text
    }


    private fun nowPair(): Pair<Long, String> {
        val ts = System.currentTimeMillis()
        val tsText = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()))
        return ts to tsText
    }

    fun save(onError: (String) -> Unit) {
        val cow = if (inHeat) cowSelected?.trim().orEmpty() else null
        val n = notes.trim()

        if (inHeat) {
            if (cow.isNullOrEmpty()) {
                onError("Selecciona la vaca en celo"); return
            }
        } else {
            if (n.isEmpty()) {
                onError("Ingresa observaciones"); return
            }
        }
        if (saving) return
        saving = true

        val (ts, tsText) = nowPair()

        viewModelScope.launch {
            try {
                repo.save(
                    inHeat = inHeat,
                    cowTag = cow,
                    notes = if (n.isEmpty()) null else n,
                    ts = ts,
                    tsText = tsText
                )
                // limpiar tras guardar
                inHeat = false
                cowSelected = null
                notes = ""
                showSuccess = true
            } catch (t: Throwable) {
                onError(t.message ?: "Error al guardar")
            } finally {
                saving = false
            }
        }
    }

    fun dismissSuccess() {
        showSuccess = false
    }
}