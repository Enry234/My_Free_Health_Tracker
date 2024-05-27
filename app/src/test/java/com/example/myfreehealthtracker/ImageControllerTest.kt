package com.example.myfreehealthtracker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)

class ImageControllerTest {

    private lateinit var context: Context
    private lateinit var imageController: ImageController
    private lateinit var testUri: Uri

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        imageController = ImageController()

        // Create a sample bitmap to use as a test image
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // Create a file in the internal storage directory
        val file = File(context.filesDir, "testFileBmmp")

        // Open an output stream to the file
        val outputStream = FileOutputStream(file)

        // Compress the bitmap to the output stream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        // Flush and close the output stream
        outputStream.flush()
        outputStream.close()

        // Get the URI using FileProvider
        testUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        // Convert the file to a URI

    }

    @Test
    fun testSaveAndLoad() {
        val filename = "saved_image.png"

        // Save the image to internal storage
        val result = imageController.saveImageToInternalStorage(context, testUri, filename)
        if (!result)
            assert(false)
        val imageUri = imageController.getImageFromInternalStorage(context, filename)
        // Verify the result
        assert(testUri == imageUri)

        // Verify that the file was created in the internal storage

    }


}