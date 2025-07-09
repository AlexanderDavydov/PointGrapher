package com.example.pointgrapher.data.repository

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.example.pointgrapher.domain.repository.ImageSaverRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageSaverRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageSaverRepository {


    private val directoryPath = Environment.DIRECTORY_PICTURES + "/PointGrapher"
    override suspend fun saveToGallery(bitmap: Bitmap, filename: String): String =
        withContext(Dispatchers.IO) {

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/PointGrapher"
                )
            }

            val uri = context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let { imageUri ->
                context.contentResolver
                    .openOutputStream(imageUri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                "$directoryPath/$filename"
            } ?: throw IOException("Failed to save chart image")
        }
}
