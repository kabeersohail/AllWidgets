package com.chirput.allwidgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chirput.allwidgets.databinding.ActivityMainBinding

private const val APP_WIDGET_HOST_ID = 101

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager

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
        val newAppWidgetId = appWidgetHost.allocateAppWidgetId()
        addWidgetLauncher.launch(newAppWidgetId)
    }

    private fun onNewWidgetSelected(info: AppWidgetProviderInfo?) {
        Log.d("here", "")
    }

    override fun onDestroy() {
        super.onDestroy()
        appWidgetHost.stopListening()
    }
}
