package com.chirput.widgetsexample

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                val widgetResult = appWidgetManager.installedProviders

                for (info in widgetResult) {
                    Log.d("WIDGET", "Name: " + info.label)
                    Log.d("WIDGET", "Provider Name: " + info.provider)
                    Log.d("WIDGET", "Configure Name: " + info.configure)
                }

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appWidgetHost = AppWidgetHost(this, 123)
        appWidgetManager = AppWidgetManager.getInstance(this)

        appWidgetHost.startListening()

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, componentName)
            // This is the options bundle described in the preceding section.
        }

        startForResult.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        appWidgetHost.stopListening()
    }
}
