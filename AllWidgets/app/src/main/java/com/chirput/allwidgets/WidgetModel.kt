package com.chirput.allwidgets

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class WidgetModel(
    val widgetName: String,
    val provider: ComponentName,
    val icon: Drawable? // Add this property to hold the widget icon
)
