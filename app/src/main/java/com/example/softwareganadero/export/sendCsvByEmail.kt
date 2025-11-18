package com.example.softwareganadero.export

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun sendMultipleCsvByEmail(
    context: Context,
    files: List<File>,
    to: String,
    subject: String,
    body: String
) {
    val uris = files.map { file ->
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(emailIntent, "Enviar reportes")
    try {
        context.startActivity(chooser)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No hay app de correo instalada", Toast.LENGTH_LONG).show()
    }
}
