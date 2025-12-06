package com.example.softwareganadero.data.sync

import com.example.softwareganadero.domain.potrerosDomain.BirthRepository

class SyncManager(
    private val birthRepository: BirthRepository
    // añade aquí otros repos cuando los prepares
) {

    // Sincroniza TODO lo que ya tenga lógica de sync
    suspend fun syncAll() {
        // nacimientos
        birthRepository.syncBirthsTwoWay()

        // cuando implementes otros repos con syncTwoWay(), los llamas aquí:
        // precipitationRepository.syncTwoWay()
        // pastureEvaluationRepository.syncTwoWay()
        // ...
    }
}