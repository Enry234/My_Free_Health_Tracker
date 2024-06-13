package com.example.myfreehealthtracker.viewmodel.login

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageController {
    fun saveImageToInternalStorage(context: Context, uri: Uri, filename: String): Boolean {
        try {
            // Get the InputStream from the URI
            val inputStream = context.contentResolver.openInputStream(uri)
            // Get the bitmap from the InputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)

            if (bitmap != null) {
                // Open an output stream to the file
                val fileOutputStream: FileOutputStream =
                    context.openFileOutput(
                        filename,
                        Context.MODE_PRIVATE
                    )
                // Compress the bitmap to the output stream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                // Flush and close the output stream
                fileOutputStream.flush()
                fileOutputStream.close()
                // Close the input stream
                inputStream?.close()
                return true
            } else {
                Log.i("ImageController", "Bitmap not valid")
                inputStream?.close()
                return false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.i("ImageController", "Error open InputStream maybe URI")
            return false
        }
    }

    fun getImageFromInternalStorage(context: Context, filename: String): Uri? {

        val file = File(context.filesDir, filename)

        // Check if the file exists
        return if (file.exists()) {
            Log.i("ImageController", "File found")
            // Get the URI using FileProvider
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } else {
            Log.i("ImageController", "File not found")
            null
        }


    }

}