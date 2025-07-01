package com.example.pointgrapher.presentation.xmlui

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.pointgrapher.data.model.UIFrameworkDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UIFrameworkDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "ui_framework_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_FRAMEWORK = "selected_framework"
    }

    fun saveSelectedFramework(framework: UIFrameworkDto) {
        prefs.edit(commit = true) { putString(KEY_FRAMEWORK, framework.name) }
    }

    fun getSelectedFramework(): UIFrameworkDto {
        val frameworkName = prefs.getString(KEY_FRAMEWORK, UIFrameworkDto.NONE.name)
        return UIFrameworkDto.valueOf(frameworkName ?: UIFrameworkDto.NONE.name)
    }

    fun clearSelection() {
        prefs.edit { remove(KEY_FRAMEWORK) }
    }
}