package com.example.softwareganadero.domain.corralesDomains

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.corralesData.HealthControl

class ControlSanitarioRepository(private val db: AgroDatabase) {
    private val dao = db.healthControlDao()

    suspend fun save(
        treatment: String,
        animal: String,
        medicines: String,
        dose: String,
        quantity: Double,
        observations: String?,
        ts: Long,
        tsText: String
    ): Long {
        val t = treatment.trim(); require(t.isNotEmpty()) { "Tratamiento requerido" }
        val a = animal.trim(); require(a.isNotEmpty()) { "Animal requerido" }
        val m = medicines.trim(); require(m.isNotEmpty()) { "Medicamentos requeridos" }
        val d = dose.trim(); require(d.isNotEmpty()) { "Dosis requerida" }
        require(quantity >= 0.0) { "Cantidad inválida" }

        return dao.insert(
            HealthControl(
                treatment = t,
                animal = a,
                medicines = m,
                dose = d,
                quantity = quantity.toString(),
                observations = observations,
                createdAt = ts,
                createdAtText = tsText
            )
        )
    }
}
