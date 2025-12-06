package com.example.softwareganadero.domain.potrerosDomain

import com.example.softwareganadero.dao.BirthRecordDao
import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.potrerosData.BirthRecord
import com.example.softwareganadero.data.remote.potreros.BirthRemoteDto
import com.example.softwareganadero.data.remote.potreros.births.toLocalEntity
import com.example.softwareganadero.data.remote.potreros.births.toRemoteDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class BirthRepository(
    db: AgroDatabase,
    private val firestore: FirebaseFirestore
) {
    private val dao: BirthRecordDao = db.birthRecordDao()
    private val collection = firestore.collection("birth_records")
    suspend fun saveBirth(
        cowTag: String,
        calfTag: String,
        sex: String,
        color: String?,
        weight: String?,
        colostrum: Boolean,
        notes: String?,
        operatorName: String,
        createdAt: Long,
        createdAtText: String
    ) {
        require(cowTag.isNotBlank()) { "Vaca requerida" }
        require(calfTag.isNotBlank() && calfTag.toLongOrNull() != null) { "Cría numérica requerida" }
        require(sex == "M" || sex == "H") { "Sexo inválido" }
        val colorOk = color?.trim().orEmpty()
        require(colorOk.isNotEmpty()) { "Color requerido" }
        require(colorOk.none { it.isDigit() }) { "Color solo letras" }
        val weightOk = weight?.trim().orEmpty()
        require(weightOk.isNotEmpty() && weightOk.toDoubleOrNull() != null) { "Peso numérico requerido" }

        val now = System.currentTimeMillis()
        val entity = BirthRecord(
            cowTag = cowTag,
            calfTag = calfTag,
            sex = sex,
            color = color,
            weight = weight,
            colostrum = colostrum,
            notes = notes,
            operatorName = operatorName,
            createdAt = createdAt,
            createdAtText = createdAtText,
            updatedAtMillis = now,
            pendingSync = true,
            deleted = false
        )
        dao.insert(entity)
    }

    suspend fun getAllLocal(): List<BirthRecord> = dao.getAll()
    // --- SINCRONIZACIÓN CON FIRESTORE ---

    // Subir a Firestore todos los registros pendientes
    suspend fun syncUpBirths() {
        val pending = dao.getPendingForSync()
        if (pending.isEmpty()) return

        for (record in pending) {
            val dto = record.toRemoteDto()

            if (record.remoteId == null) {
                // No tiene remoteId -> crear documento nuevo
                val docRef = collection.add(dto).await()
                val newRemoteId = docRef.id
                dao.update(
                    record.copy(
                        remoteId = newRemoteId,
                        pendingSync = false
                    )
                )
            } else {
                // Ya tiene remoteId -> actualizar documento existente
                collection.document(record.remoteId)
                    .set(dto)
                    .await()
                dao.update(
                    record.copy(
                        pendingSync = false
                    )
                )
            }
        }
    }

    // Bajar datos desde Firestore y fusionarlos en Room (última escritura gana)
    suspend fun syncDownBirths() {
        val snapshot: QuerySnapshot = collection.get().await()

        for (doc in snapshot.documents) {
            val dto = doc.toObject(BirthRemoteDto::class.java) ?: continue
            val remoteId = doc.id

            // Buscar si ya existe localmente
            val local = dao.getByRemoteId(remoteId)
            if (local == null) {
                // No existe local -> insertar
                val newEntity = dto.toLocalEntity(remoteId = remoteId)
                dao.insert(newEntity)
            } else {
                // Existe local -> resolver por updatedAtMillis
                if (dto.updatedAtMillis > local.updatedAtMillis) {
                    val updated = dto.toLocalEntity(
                        remoteId = remoteId,
                        localId = local.id
                    )
                    dao.update(updated)
                }
                // Si local es más nuevo, lo subiremos en el próximo syncUp
            }
        }
    }

    // Función cómoda para hacer up y luego down
    suspend fun syncBirthsTwoWay() {
        syncUpBirths()
        syncDownBirths()
    }
}
