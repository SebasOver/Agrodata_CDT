package com.example.softwareganadero.domain.corralesDomains

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.corralesData.Weighing

class WeighingRepository(private val db: AgroDatabase) {
    private val dao = db.weighingDao()

    suspend fun save(
        sex: String,
        animalNumber: String,
        breed: String,
        color: String,
        bodyCondition: String,
        observations: String?,
        createdAt: Long,
        createdAtText: String
    ): Long {
        require(sex == "M" || sex == "H") { "Sexo inválido" }
        require(animalNumber.isNotBlank() && animalNumber.all { it.isDigit() }) { "Número animal inválido" }
        val letters = Regex("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$")
        require(breed.isNotBlank() && breed.matches(letters)) { "Raza inválida" }
        require(color.isNotBlank() && color.matches(letters)) { "Color inválido" }
        require(bodyCondition.isNotBlank()) { "C.C requerido" }

        return dao.insert(
            Weighing(
                sex = sex,
                animalNumber = animalNumber.trim(),
                breed = breed.trim(),
                color = color.trim(),
                bodyCondition = bodyCondition.trim(),
                observations = observations,
                createdAt = createdAt,
                createdAtText = createdAtText
            )
        )
    }
}