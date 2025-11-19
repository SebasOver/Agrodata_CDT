package com.example.softwareganadero.export

import android.content.Context
import android.os.Environment
import com.example.softwareganadero.domain.potrerosDomain.AgroRepository
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

class CsvExporter(
    private val context: Context,
    private val repository: AgroRepository,
    private val io: CoroutineDispatcher = Dispatchers.IO,

    ) {
    private val SEP = ';'
    private fun yesNo(value: Boolean): String = if (value) "Si" else "No"

    private fun esc(s: String): String = s.replace("\"", "\"\"")

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

    // --- POTREROS ---
    // 1) Precipitaciones
    suspend fun exportPrecipitationsCsv(
        fileName: String = "precipitaciones.csv"
    ): File = withContext(io) {
        val dir = dirPotreros()
        val file = File(dir, fileName)
        val data = repository.listPrecipitations()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Milimetros",
                    ).joinToString(SEP.toString())
                )
                data.forEach { p ->
                    bw.append(esc(p.createdAtText)).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Lote",
                        "Sanas",
                        "Enfermas",
                        "Total"
                    ).joinToString(SEP.toString())
                )
                data.forEach { inv ->
                    bw.append(esc(inv.createdAtText)).append(SEP)
                        .append(inv.lot.toString()).append(SEP)
                        .append(inv.healthy.toString()).append(SEP)
                        .append(inv.sick.toString()).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Rotacion",
                        "Potrero",
                        "Altura entrada",
                        "Altura salida",
                        "Color entrada",
                        "Color salida"
                    ).joinToString(SEP.toString())
                )
                data.forEach { ev ->
                    bw.append(esc(ev.createdAtText)).append(SEP)
                        .append(esc(ev.rotation ?: "")).append(SEP)
                        .append(esc(ev.paddock ?: "")).append(SEP)
                        .append(esc(ev.heightEntry ?: "")).append(SEP)
                        .append(esc(ev.heightExit ?: "")).append(SEP)
                        .append(esc(ev.colorEntry ?: "")).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Disponibilidad",
                        "Temperatura(C)",
                    ).joinToString(SEP.toString())
                )
                data.forEach { w ->
                    bw.append(esc(w.createdAtText)).append(SEP)
                        .append(esc(w.availability)).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Rotacion",
                        "Potrero",
                        "Volteos",
                        "Observaciones"
                    ).joinToString(SEP.toString())
                )
                data.forEach { f ->
                    bw.append(esc(f.createdAtText)).append(SEP)
                        .append(esc(f.rotacion)).append(SEP)
                        .append(esc(f.potrero)).append(SEP)
                        .append(esc(f.volteos)).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Rotacion",
                        "Lote",
                        "Numero animales",
                        "Nombre suplemento",
                        "Cantidad"
                    ).joinToString(SEP.toString())
                )
                data.forEach { s ->
                    bw.append(esc(s.createdAtText)).append(SEP)
                        .append(esc(s.rotation)).append(SEP)
                        .append(esc(s.lot)).append(SEP)
                        .append(s.animalsCount.toString()).append(SEP)
                        .append(esc(s.name)).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Vaca",
                        "Cria",
                        "Sexo",
                        "Color",
                        "Peso",
                        "Calostro(Si/No)",
                        "Observaciones"
                    ).joinToString(SEP.toString())
                )
                data.forEach { b ->
                    bw.append(esc(b.createdAtText)).append(SEP)
                        .append(esc(b.cowTag)).append(SEP)
                        .append(esc(b.calfTag)).append(SEP)
                        .append(esc(b.sex)).append(SEP)
                        .append(esc(b.color ?: "")).append(SEP)
                        .append(esc(b.weight ?: "")).append(SEP)
                        .append(esc(yesNo(b.colostrum))).append(SEP)
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
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Celo(Si/No)",
                        "Vaca",
                        "Observaciones",
                    ).joinToString(SEP.toString())
                )
                data.forEach { h ->
                    bw.append(esc(h.createdAtText)).append(SEP)
                        .append(esc(yesNo(h.inHeat))).append(SEP)
                        .append(esc(h.cowTag ?: "")).append(SEP)
                        .append(h.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }
    // --- CORRALES ---
    suspend fun exportHealthControlCsv(
        fileName: String = "control_salud.csv"
    ): File = withContext(io) {
        val dir = dirCorrales()
        val file = File(dir, fileName)
        val data =
            repository.listHealthControls()   // DAO: SELECT * FROM health_control ORDER BY created_at_text ASC

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Tratamiento",
                        "Animal",
                        "Medicamentos",
                        "Dosis",
                        "Cantidad",
                        "Observaciones"
                    ).joinToString(SEP.toString())
                )
                data.forEach { h ->
                    bw.append(esc(h.createdAtText)).append(SEP)
                        .append(esc(h.treatment)).append(SEP)
                        .append(esc(h.animal)).append(SEP)
                        .append(esc(h.medicines)).append(SEP)
                        .append(esc(h.dose)).append(SEP)
                        .append(esc(h.quantity)).append(SEP)
                        .append(h.observations?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // B) Palpaciones
    suspend fun exportPalpationsCsv(
        fileName: String = "palpaciones.csv"
    ): File = withContext(io) {
        val dir = dirCorrales()
        val file = File(dir, fileName)
        val data =
            repository.listPalpations()   // DAO: SELECT * FROM palpations ORDER BY created_at_text ASC

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Numero animal",
                        "Dias gestacion",
                        "Observaciones",
                    ).joinToString(SEP.toString())
                )
                data.forEach { p ->
                    bw.append(esc(p.createdAtText)).append(SEP)
                        .append(esc(p.animalNumber)).append(SEP)
                        .append(p.pregnancyDays.toString()).append(SEP)
                        .append(p.observations?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // C) Triage
    suspend fun exportTriageCsv(
        fileName: String = "triage.csv"
    ): File = withContext(io) {
        val dir = dirCorrales()
        val file = File(dir, fileName)
        val data =
            repository.listTriageRecords()   // DAO: SELECT * FROM triage_records ORDER BY created_at_text ASC

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Numero animal",
                        "Temperatura",
                        "Locomocion",
                        "Color mucosa",
                        "Observaciones"
                    ).joinToString(SEP.toString())
                )
                data.forEach { t ->
                    bw.append(esc(t.createdAtText)).append(SEP)
                        .append(esc(t.animalNumber)).append(SEP)
                        .append(t.temperature.toString()).append(SEP)
                        .append(esc(t.locomotion)).append(SEP)
                        .append(esc(t.mucosaColor)).append(SEP)
                        .append(t.observations?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // D) Pesajes
    suspend fun exportWeighingsCsv(
        fileName: String = "pesajes.csv"
    ): File = withContext(io) {
        val dir = dirCorrales()
        val file = File(dir, fileName)
        val data =
            repository.listWeighings()   // DAO: SELECT * FROM weighings ORDER BY created_at_text ASC

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Sexo",
                        "Numero animal",
                        "Raza",
                        "Color",
                        "Condicion corporal",
                        "Observaciones",
                    ).joinToString(SEP.toString())
                )
                data.forEach { w ->
                    bw.append(esc(w.createdAtText)).append(SEP)
                        .append(esc(w.sex)).append(SEP)
                        .append(esc(w.animalNumber)).append(SEP)
                        .append(esc(w.breed)).append(SEP)
                        .append(esc(w.color)).append(SEP)
                        .append(esc(w.bodyCondition)).append(SEP)
                        .append(w.observations?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }
    // --- CULTIVOS ---
    suspend fun exportCropsCsv(
        fileName: String = "cultivos.csv"
    ): File = withContext(io) {
        val dir = dirCultivos()
        val file = File(dir, fileName)
        val data = repository.listCropRecords()

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha",
                        "Lote",
                        "Especie",
                        "Plagas (Si/No)",
                        "Enfermedades (Si/No)",
                        "Observaciones"
                    ).joinToString(SEP.toString())
                )
                data.forEach { c ->
                    bw.append(esc(c.createdAtText)).append(SEP)
                        .append(esc(c.lot)).append(SEP)
                        .append(esc(c.species)).append(SEP)
                        .append(esc(yesNo(c.hasPests))).append(SEP)
                        .append(esc(yesNo(c.hasDiseases))).append(SEP)
                        .append(c.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }
    // --- VISITAS ---
    suspend fun exportInstitutionVisitsCsv(
        fileName: String = "visitas_institucionales.csv"
    ): File = withContext(io) {
        val dir = dirVisitas()
        val file = File(dir, fileName)
        val data = repository.listInstitutionRecords()   // ver sección 2

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha entrada",
                        "Hora salida",
                        "Nombre visitante",
                        "Motivo",
                        "Observaciones",
                    ).joinToString(SEP.toString())
                )

                data.forEach { r ->
                    val fechaEntrada = r.createdAtText           // texto que ya generas al guardar
                    val horaSalida = r.closedAtText ?: ""        // puede ser null
                    bw.append(esc(fechaEntrada)).append(SEP)
                        .append(esc(horaSalida)).append(SEP)
                        .append(esc(r.visitorName)).append(SEP)
                        .append(esc(r.reason)).append(SEP)
                        .append(r.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    // B) Visitas particulares
    suspend fun exportParticularVisitsCsv(
        fileName: String = "visitas_particulares.csv"
    ): File = withContext(io) {
        val dir = dirVisitas()
        val file = File(dir, fileName)
        val data = repository.listParticularRecords()    // ver sección 2

        FileWriter(file, false).use { fw ->
            BufferedWriter(fw).use { bw ->
                bw.appendLine(
                    listOf(
                        "Fecha entrada",
                        "Hora salida",
                        "Nombre visitante",
                        "Motivo",
                        "Observaciones",
                    ).joinToString(SEP.toString())
                )

                data.forEach { r ->
                    val fechaEntrada = r.createdAtText
                    val horaSalida = r.closedAtText ?: ""
                    bw.append(esc(fechaEntrada)).append(SEP)
                        .append(esc(horaSalida)).append(SEP)
                        .append(esc(r.visitorName)).append(SEP)
                        .append(esc(r.reason)).append(SEP)
                        .append(r.notes?.let(::esc) ?: "")
                        .append('\n')
                }
            }
        }
        file
    }

    suspend fun zipAgrodataDirectory(
        zipName: String = "reporte.zip"
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