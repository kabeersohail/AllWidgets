package com.chirput.allwidgets

import android.appwidget.AppWidgetProviderInfo


interface Listener {
    fun onWidgetSelected(info: AppWidgetProviderInfo?)
}
