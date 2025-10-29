package com.example.softwareganadero.data

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class CsvExporter(
    private val context: Context,
    private val repository: AgroRepository,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    /**
     * Exporta productores a un archivo CSV en la carpeta "Documents/Agrodata".
     * Devuelve el File generado.
     */
    suspend fun exportProducersCsv(
        fileName: String = "producers_${System.currentTimeMillis()}.csv"
    ): File = withContext(io) {
        val data = repository.listProducers()

        // Directorio público en Documents para fácil acceso del usuario
        val docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val dir = File(docs, "Agrodata").apply { if (!exists()) mkdirs() }
        val file = File(dir, fileName)

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                // Cabeceras
                bw.appendLine("id,name,created_at")
                // Filas
                data.forEach { p ->
                    // Escapar comas, comillas dobles y saltos de línea
                    fun esc(s: String) = "\"" + s.replace("\"", "\"\"") + "\""
                    bw.append(p.id.toString())
                        .append(',')
                        .append(esc(p.name))
                        .append(',')
                        .append(p.createdAt.toString())
                        .append('\n')
                }
            }
        }
        file
    }
}