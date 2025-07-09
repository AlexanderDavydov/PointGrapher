package com.example.pointgrapher.domain.repository

import android.graphics.Bitmap

interface ImageSaverRepository {
    suspend fun saveToGallery(bitmap: Bitmap, filename: String): String
}