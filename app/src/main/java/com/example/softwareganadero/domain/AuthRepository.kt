package com.example.softwareganadero.domain

import com.example.softwareganadero.data.AgroDatabase
import com.example.softwareganadero.data.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val db: AgroDatabase,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    private val users = db.userDao()
    suspend fun authenticateByName(input: String): User? = withContext(io) {
        // Comparación exacta por ahora; podrías normalizar con trim y case fold
        users.findActiveByName(input.trim())
    }
}