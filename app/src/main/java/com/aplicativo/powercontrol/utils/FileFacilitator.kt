package com.aplicativo.powercontrol.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileFacilitator {

    private const val TAG = "FILE FACILITATOR"
    var directoryDefault: File? = null
    var contextFile: Context? = null

    fun init(context: Context) {
        contextFile = context
        //take the destination directory
        directoryDefault = contextFile?.getExternalFilesDir("application/accounts")
    }

    fun saveDocumentInDirectory(uri: Uri): Boolean {
        val fileName = "account" + SimpleDateFormat("ddMMyyyy_HHmmss",
            Locale("pr", "BR")).format(Date()) + ".pdf"
        //take the contents of the uri
        val input = contextFile?.contentResolver!!.openInputStream(uri)
        //creates a new file
        val newFile = File(directoryDefault, fileName)
        val fileOutputStream = FileOutputStream(newFile)
        try {
            input?.let {
                FileUtils.copy(input, fileOutputStream)
                return true
            }
        } catch (ex: IOException) {
            Log.e(TAG, "Message error: ${ex.message}")
        } finally {
            input?.let {
                input.close()
            }
            fileOutputStream.close()
        }
        return false
    }

    fun getFileNameByUri(uri: Uri): String? {
        val contentResolver = contextFile?.contentResolver
        val cursor: Cursor? = contentResolver?.query(
            uri, null, null, null, null, null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return null
    }

    fun openDocument(pathDocument: String): Boolean {
        val file = File(pathDocument)
        if (file.exists()) {
            val uri = contextFile?.let {
                FileProvider.getUriForFile(
                    it,
                    contextFile!!.applicationContext.packageName + ".com.aplicativo.powercontrol.provider",
                    file
                )
            }
            return try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                contextFile?.startActivity(intent)
                true
            } catch (e: Exception) {
                Log.e("HOME FRAGMENT", "ERROR: ${e.message}")
                false
            }
        }
        return false
    }

    fun deleteFile(path: String): Boolean {
        val file = File(path)
        if (file.exists()) {
            file.delete()
            return true
        }
        return false
    }

    fun getNameFileByPath(path: String): String {
        return path.substring(path.lastIndexOf("/") + 1)
    }

}