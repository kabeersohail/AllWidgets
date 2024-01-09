package com.chirput.allwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

            val icon = getWidgetIcon(info.provider)
            widgetItems.add(WidgetModel(info.label, info.provider, icon))
        }

        recyclerView.adapter = WidgetAdapter(this, widgetItems)
    }

    private fun getWidgetIcon(provider: ComponentName): Drawable? {
        return try {
            val appInfo = packageManager.getApplicationInfo(provider.packageName, 0)
            appInfo.loadIcon(packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ContextCompat.getDrawable(this, R.drawable.ic_widget_no_preview)
        }
    }
}
