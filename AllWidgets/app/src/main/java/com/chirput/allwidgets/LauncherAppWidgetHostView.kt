package com.chirput.allwidgets

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.os.Build
import android.view.View

class LauncherAppWidgetHostView(
    context: Context
): AppWidgetHostView(context), WidgetHostView{
    override fun getView(): View {
        return this
    }

    override fun setAppWidget(appWidgetId: Int, info: AppWidgetProviderInfo?) {
        super.setAppWidget(appWidgetId, info)
        val p = resources.getDimensionPixelSize(R.dimen.widget_padding)
        setPadding(p, p, p, p)
    }

    override fun resizeMode(): Int {
        return appWidgetInfo.resizeMode
    }

    override fun isReconfigurable(): Boolean {
        val info = appWidgetInfo
        if (info.configure == null) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return info.widgetFeatures and AppWidgetProviderInfo.WIDGET_FEATURE_RECONFIGURABLE != 0
        }
        return true
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        try {
            super.onLayout(changed, left, top, right, bottom)
        } catch (e: RuntimeException) {

        }
    }

}