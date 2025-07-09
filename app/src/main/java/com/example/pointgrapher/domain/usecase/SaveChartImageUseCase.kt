package com.example.pointgrapher.domain.usecase

import android.graphics.Bitmap
import com.example.pointgrapher.domain.repository.ImageSaverRepository
import javax.inject.Inject

class SaveChartImageUseCase @Inject constructor(
    private val imageSaver: ImageSaverRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, chartType: String = "chart"): String {
        val filename = "pointgrapher_${chartType}_${System.currentTimeMillis()}.png"
        return imageSaver.saveToGallery(bitmap, filename)
    }
}