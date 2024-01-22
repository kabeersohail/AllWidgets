package com.chirput.allwidgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chirput.allwidgets.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val APP_WIDGET_HOST_ID = 101

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var binding: ActivityMainBinding
        private lateinit var appWidgetHost: AppWidgetHost
        private lateinit var appWidgetManager: AppWidgetManager
        private var newAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        lateinit var hostView: AppWidgetHostView

        fun resize(
            container: ResizableFrameLayout = binding.widgetContainer,
            width: Int,
            height: Int,
            hostView: AppWidgetHostView
        ) {

            container.removeAllViews()

            // Add it to your layout
            val layoutParams = FrameLayout.LayoutParams(width, height)
            container.addView(hostView, layoutParams)
        }

    }



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

        CoroutineScope(Dispatchers.Main).launch {
            createWidget(widgetView.appWidgetInfo, binding.widgetContainer, 100, 100)
        }

    }

    private suspend fun createWidget(info: AppWidgetProviderInfo, container: ResizableFrameLayout, width: Int, height: Int) {
        // Get the list of installed widgets
        // Create Widget
        val appWidgetId: Int = appWidgetHost.allocateAppWidgetId()
        hostView =
            appWidgetHost.createView(this@MainActivity, appWidgetId, info)
        hostView.setAppWidget(appWidgetId, info)

        // Add it to your layout
        val layoutParams = FrameLayout.LayoutParams(width, height)
        container.addView(hostView, layoutParams)

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

        resize(width = 400, height = 400, hostView = hostView)

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
