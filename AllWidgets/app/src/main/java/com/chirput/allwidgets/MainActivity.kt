package com.chirput.allwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chirput.allwidgets.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        appWidgetManager = getSystemService(APPWIDGET_SERVICE) as AppWidgetManager

        recyclerView = findViewById(R.id.widget_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        populateWidgets()
    }

    override fun onResume() {
        super.onResume()
        populateWidgets()
    }

    private fun populateWidgets() {
        val providers: List<AppWidgetProviderInfo> = appWidgetManager.installedProviders
        val widgetItems: MutableList<WidgetModel> = mutableListOf()

        for (info in providers) {
            Log.d("SOHAIL_BRO", "$info")

            val previewImageUri = Uri.parse("android.resource://${info.provider.packageName}/${info.previewImage}")
            widgetItems.add(WidgetModel(info.label, info.provider, previewImageUri))
        }

        recyclerView.adapter = WidgetAdapter(this, widgetItems)
    }
}
