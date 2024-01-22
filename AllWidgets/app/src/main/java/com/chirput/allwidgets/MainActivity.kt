package com.chirput.allwidgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
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

        createWidget(widgetView.appWidgetInfo)
    }

    private fun createWidget(info: AppWidgetProviderInfo): Boolean {
        // Get the list of installed widgets
        // Create Widget
        val appWidgetId: Int = appWidgetHost.allocateAppWidgetId()
        val hostView: AppWidgetHostView =
            appWidgetHost.createView(this@MainActivity, appWidgetId, info)
        hostView.setAppWidget(appWidgetId, info)

        // Add it to your layout
        val widgetLayout = binding.widgetContainer
        widgetLayout.addView(hostView)

        // And bind widget IDs to make them actually work
        val allowed: Boolean = appWidgetManager.bindAppWidgetIdIfAllowed(
            appWidgetId,
            info.provider
        )
        if (!allowed) {
            // Request permission - https://stackoverflow.com/a/44351320/1816603
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.putExtra(
                AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,
                info.provider
            )
            val REQUEST_BIND_WIDGET = 1987
            startActivityForResult(intent, REQUEST_BIND_WIDGET)
        }
        return true
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
