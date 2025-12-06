package com.example.softwareganadero.data.remote.potreros.births

import com.example.softwareganadero.data.potrerosData.BirthRecord
import com.example.softwareganadero.data.remote.potreros.BirthRemoteDto

// Room -> DTO para subir a Firestore
fun BirthRecord.toRemoteDto(): BirthRemoteDto =
    BirthRemoteDto(
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
        updatedAtMillis = updatedAtMillis,
        deleted = deleted
    )

// DTO + remoteId -> entidad Room para insertar/actualizar con datos que vienen de Firestore
fun BirthRemoteDto.toLocalEntity(
    remoteId: String,
    localId: Long = 0L
): BirthRecord =
    BirthRecord(
        id = localId,
        remoteId = remoteId,
        updatedAtMillis = updatedAtMillis,
        pendingSync = false, // viene del servidor, ya está sincronizado
        deleted = deleted,
        cowTag = cowTag,
        calfTag = calfTag,
        sex = sex,
        color = color,
        weight = weight,
        colostrum = colostrum,
        notes = notes,
        operatorName = operatorName,
        createdAt = createdAt,
        createdAtText = createdAtText
    )