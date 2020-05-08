package com.aplicativo.powercontrol.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileFacilitator {

    private const val TAG = "FILE FACILITATOR"
    var directoryDefault: File? = null
    var contextFile: Context? =null

    fun init(context: Context){
        contextFile = context
        //take the destination directory
        directoryDefault = contextFile?.getExternalFilesDir("application/accounts")
    }

    fun saveDocumentInDirectory( uri: Uri, fileName: String): Boolean {
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
}