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
import com.example.softwareganadero.domain.potrerosDomain.PrecipitacionRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
class PrecipitacionViewModel(
    private val db: AgroDatabase,
    private val repo: PrecipitacionRepository,
    private val operatorName: String
) : ViewModel() {

    // ---------- Estado: Precipitación ----------
    var precipMm by mutableStateOf("")
        private set
    var savingPrecip by mutableStateOf(false)
        private set
    var showPrecipSuccess by mutableStateOf(false)
        private set

    // ---------- Estado: Inventario ----------
    var lot by mutableStateOf("")
        private set
    var healthy by mutableStateOf("")
        private set
    var sick by mutableStateOf("")
        private set
    var savingInv by mutableStateOf(false)
        private set
    var showInvSuccess by mutableStateOf(false)
        private set

    // -------- set de campos --------

    fun onPrecipChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d*\.?\d*"""))) {
            precipMm = text
        }
    }

    fun onLotChanged(text: String) {
        lot = text
    }

    fun onHealthyChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d+"""))) healthy = text
    }

    fun onSickChanged(text: String) {
        if (text.isEmpty() || text.matches(Regex("""\d+"""))) sick = text
    }

    private fun nowPair(): Pair<Long, String> {
        val nowMillis = System.currentTimeMillis()
        val nowText = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .format(Instant.ofEpochMilli(nowMillis).atZone(ZoneId.systemDefault()))
        return nowMillis to nowText
    }

    // -------- helpers de validación --------

    val precipValid: Boolean
        get() = precipMm.isNotBlank() && precipMm.toDoubleOrNull() != null

    private val lotInt: Int?
        get() = lot.toIntOrNull()
    private val hInt: Int?
        get() = healthy.toIntOrNull()
    private val sInt: Int?
        get() = sick.toIntOrNull()
    val total: Int
        get() = (hInt ?: 0) + (sInt ?: 0)

    val inventoryValid: Boolean
        get() = lot.isNotBlank() && hInt != null && sInt != null

    // -------- acciones --------

    fun savePrecip(onError: (String) -> Unit) {
        if (!precipValid) {
            onError("Cantidad de precipitación inválida")
            return
        }
        if (savingPrecip) return

        savingPrecip = true
        val (millis, text) = nowPair()

        viewModelScope.launch {
            try {
                repo.savePrecipitation(
                    amountMm = precipMm.toDouble(),
                    operator = operatorName.trim(),
                    atText = text,
                    atMillis = millis
                )
                precipMm = ""
                showPrecipSuccess = true
            } catch (e: Exception) {
                onError(e.message ?: "Error al guardar precipitación")
            } finally {
                savingPrecip = false
            }
        }
    }

    fun saveInventory(onError: (String) -> Unit) {
        if (!inventoryValid) {
            onError("Completa lote, sanos y enfermos con datos válidos")
            return
        }
        if (savingInv) return

        val lotVal = lotInt ?: 0
        val hVal = hInt!!
        val sVal = sInt!!
        val totalVal = hVal + sVal

        savingInv = true
        val (millis, text) = nowPair()

        viewModelScope.launch {
            try {
                repo.savePastureInventory(
                    lot = lotVal,
                    healthy = hVal,
                    sick = sVal,
                    total = totalVal,
                    operator = operatorName.trim(),
                    atText = text,
                    atMillis = millis
                )
                lot = ""
                healthy = ""
                sick = ""
                showInvSuccess = true
            } catch (e: Exception) {
                onError(e.message ?: "Error al guardar inventario")
            } finally {
                savingInv = false
            }
        }
    }

    // -------- reset / dismiss --------

    fun dismissPrecipSuccess() {
        showPrecipSuccess = false
    }

    fun dismissInvSuccess() {
        showInvSuccess = false
    }
}