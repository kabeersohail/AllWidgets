package com.chirput.allwidgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chirput.allwidgets.databinding.ActivityMainBinding

private const val APP_WIDGET_HOST_ID = 101

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager
    private var newAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private val addWidgetLauncher = registerForActivityResult<Int, AppWidgetProviderInfo>(
        WidgetGalleryActivity.SelectContract()
    ) { info: AppWidgetProviderInfo? ->
        onNewWidgetSelected(info)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        appWidgetHost = AppWidgetHost(this, APP_WIDGET_HOST_ID)
        appWidgetHost.startListening()
        appWidgetManager = AppWidgetManager.getInstance(this)

        binding.start.setOnClickListener {
            startAddNewWidget()
        }
    }

    private fun startAddNewWidget() {
        newAppWidgetId = appWidgetHost.allocateAppWidgetId()
        addWidgetLauncher.launch(newAppWidgetId)
    }

    private fun onNewWidgetSelected(info: AppWidgetProviderInfo?) {
        if (info != null) {
            addNewWidget(info)
        } else {
            cancelAddNewWidget(newAppWidgetId)
        }
    }

    private fun addNewWidget(info: AppWidgetProviderInfo) {
        val widgetView = appWidgetHost.createView(
            this,
            APP_WIDGET_HOST_ID,
            info
        ) ?: return

        val widgetContainer = binding.widgetContainer
        widgetContainer.addView(widgetView)
    }

    private fun cancelAddNewWidget(appWidgetId: Int) {
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return
        }
        appWidgetHost.deleteAppWidgetId(newAppWidgetId)
        newAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    }

    override fun onDestroy() {
        super.onDestroy()
        appWidgetHost.stopListening()
    }
}
