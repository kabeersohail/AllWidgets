package com.chirput.allwidgets

import android.content.ComponentName
import android.net.Uri

data class WidgetModel(
    val widgetName: String,
    val provider: ComponentName,
    val previewImageUri: Uri?
)
