package com.example.softwareganadero.data

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.text.append

class CsvExporter(
    private val context: Context,
    private val repository: AgroRepository,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {

    private fun yesNo(value: Boolean): String = if (value) "Sí" else "No"

    private fun esc(s: String): String = "\"" + s.replace("\"", "\"\"") + "\""

    private fun dirAgrodata(): File {
        val docs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        return File(docs, "Agrodata").apply { if (!exists()) mkdirs() }
    }

    // Carpeta para potreros, por ejemplo
    private fun dirPotreros(): File =
        File(dirAgrodata(), "potreros").apply { if (!exists()) mkdirs() }

    private fun dirCorrales(): File =
        File(dirAgrodata(), "corrales").apply { if (!exists()) mkdirs() }

    private fun dirVisitas(): File =
        File(dirAgrodata(), "visitas").apply { if (!exists()) mkdirs() }

    private fun dirCultivos(): File =
        File(dirAgrodata(), "cultivos").apply { if (!exists()) mkdirs() }

    private fun cleanupCsvInDir(dir: File) {
        dir.listFiles()?.forEach { f ->
            if (f.isFile && f.extension.equals("csv", ignoreCase = true)) {
                f.delete()
            }
        }
    }
    fun cleanupLooseCsvFiles() {
        val root = dirAgrodata()
        root.listFiles()?.forEach { f ->
            if (f.isFile && f.extension.equals("csv", ignoreCase = true)) {
                f.delete()
            }
        }
    }

    // 1) Precipitaciones
    suspend fun exportPrecipitationsCsv(
        fileName: String = "precipitaciones.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listPrecipitations()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Milímetros")
                data.forEach { p ->
                    bw.append(esc(p.createdAtText)).append(',')
                        .append(p.amountMm.toString())
                        .append('\n')
                }
            }
        }
        file
    }

    // 2) Inventario praderas
    suspend fun exportPastureInventoriesCsv(
        fileName: String = "inventario_praderas.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listPastureInventories()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Lote,Sanas,Enfermas,Total")
                data.forEach { inv ->
                    bw.append(esc(inv.createdAtText)).append(',')
                        .append(inv.lot.toString()).append(',')
                        .append(inv.healthy.toString()).append(',')
                        .append(inv.sick.toString()).append(',')
                        .append(inv.total.toString())
                        .append('\n')
                }
            }
        }
        file
    }

    // 3) Evaluación pradera
    suspend fun exportPastureEvaluationsCsv(
        fileName: String = "evaluacion_pradera.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listPastureEvaluations()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Rotación,Potrero,Altura entrada,Altura salida,Color entrada,Color salida")
                data.forEach { ev ->
                    bw.append(esc(ev.createdAtText)).append(',')
                        .append(esc(ev.rotation ?: "")).append(',')
                        .append(esc(ev.paddock ?: "")).append(',')
                        .append(esc(ev.heightEntry ?: "")).append(',')
                        .append(esc(ev.heightExit ?: "")).append(',')
                        .append(esc(ev.colorEntry ?: "")).append(',')
                        .append(esc(ev.colorExit ?: ""))
                        .append('\n')
                }
            }
        }
        file
    }

    // 4) Evaluación agua
    suspend fun exportWaterEvaluationsCsv(
        fileName: String = "evaluacion_agua.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listWaterEvaluations()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Disponibilidad,Temperatura (°C)")
                data.forEach { w ->
                    bw.append(esc(w.createdAtText)).append(',')
                        .append(esc(w.availability)).append(',')
                        .append(w.temperature.toString())
                        .append('\n')
                }
            }
        }
        file
    }

    // 5) Cercas y potreros
    suspend fun exportPastureFenceLogsCsv(
        fileName: String = "cercas_potreros.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listPastureFenceLogs()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Rotación,Potrero,Volteos,Observaciones")
                data.forEach { f ->
                    bw.append(esc(f.createdAtText)).append(',')
                        .append(esc(f.rotacion)).append(',')
                        .append(esc(f.potrero)).append(',')
                        .append(esc(f.volteos)).append(',')
                        .append(f.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // 6) Suplementos
    suspend fun exportSupplementsCsv(
        fileName: String = "suplementos.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listSupplements()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Rotación,Lote,Número animales,Nombre suplemento,Cantidad")
                data.forEach { s ->
                    bw.append(esc(s.createdAtText)).append(',')
                        .append(esc(s.rotation)).append(',')
                        .append(esc(s.lot)).append(',')
                        .append(s.animalsCount.toString()).append(',')
                        .append(esc(s.name)).append(',')
                        .append(s.quantity.toString())
                        .append('\n')
                }
            }
        }
        file
    }

    // 7) Nacimientos
    suspend fun exportBirthRecordsCsv(
        fileName: String = "nacimientos.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listBirthRecords()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Vaca,Cría,Sexo,Color,Peso,Colostro (Sí/No),Observaciones")
                data.forEach { b ->
                    bw.append(esc(b.createdAtText)).append(',')
                        .append(esc(b.cowTag)).append(',')
                        .append(esc(b.calfTag)).append(',')
                        .append(esc(b.sex)).append(',')
                        .append(esc(b.color ?: "")).append(',')
                        .append(esc(b.weight ?: "")).append(',')
                        .append(esc(yesNo(b.colostrum))).append(',')
                        .append(b.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // 8) Detección celos
    suspend fun exportHeatDetectionsCsv(
        fileName: String = "deteccion_celos.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listHeatDetections()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,En celo (Sí/No),Vaca,Observaciones")
                data.forEach { h ->
                    bw.append(esc(h.createdAtText)).append(',')
                        .append(esc(yesNo(h.inHeat))).append(',')
                        .append(esc(h.cowTag ?: "")).append(',')
                        .append(h.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // 9) Cultivos
    suspend fun exportCropsCsv(
        fileName: String = "cultivos.csv"
    ): File = withContext(io) {
        val dir = dirCultivos()
        val file = File(dir, fileName)
        val data = repository.listCropRecords()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine("Fecha,Lote,Especie,Plagas (Sí/No),Enfermedades (Sí/No),Observaciones")
                data.forEach { c ->
                    bw.append(esc(c.createdAtText)).append(',')
                        .append(esc(c.lot)).append(',')
                        .append(esc(c.species)).append(',')
                        .append(esc(yesNo(c.hasPests))).append(',')
                        .append(esc(yesNo(c.hasDiseases))).append(',')
                        .append(c.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    suspend fun zipAgrodataDirectory(
        zipName: String = "reporte_${System.currentTimeMillis()}.zip"
    ): File = withContext(Dispatchers.IO) {
        val rootDir = dirAgrodata()
        val zipFile = File(rootDir.parentFile, zipName)  // mismo nivel que Agrodata

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            fun addFileToZip(file: File, base: String) {
                val entryName = base + file.name
                zos.putNextEntry(ZipEntry(entryName))
                file.inputStream().use { input ->
                    input.copyTo(zos)
                }
                zos.closeEntry()
            }

            fun walk(dir: File, basePath: String = "") {
                dir.listFiles()?.forEach { f ->
                    if (f.isDirectory) {
                        walk(f, basePath + f.name + "/")
                    } else {
                        addFileToZip(f, basePath)
                    }
                }
            }

            // basePath empieza con "Agrodata/"
            walk(rootDir, "Agrodata/")
        }

        zipFile
    }
    suspend fun cleanupAllReportCsv() = withContext(io) {
        // Limpia CSV sueltos en la raíz de Agrodata (primer experimento)
        cleanupLooseCsvFiles()

        // Limpia CSV de cada carpeta de vistas
        cleanupCsvInDir(dirPotreros())
        cleanupCsvInDir(dirCultivos())
        cleanupCsvInDir(dirCorrales())
        cleanupCsvInDir(dirVisitas())
    }
}

