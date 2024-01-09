package com.chirput.allwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
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

            val widgetPreviewUri = Uri.parse("android.resource://${info.provider.packageName}/${info.previewImage}")
            val icon = getWidgetIcon(info.provider)

            var description = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val desc = info.loadDescription(this)
                if (!TextUtils.isEmpty(desc)) {
                    description = desc.toString()
                }
            }

            widgetItems.add(WidgetModel(info.label, info.provider, widgetPreviewUri, icon, description))
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
