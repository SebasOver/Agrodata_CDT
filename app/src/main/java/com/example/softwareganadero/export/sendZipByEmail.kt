package com.example.softwareganadero.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

fun sendZipByEmail(
    context: Context,
    file: File,
    to: String,
    subject: String,
    body: String
) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(emailIntent, "Enviar reporte"))
}