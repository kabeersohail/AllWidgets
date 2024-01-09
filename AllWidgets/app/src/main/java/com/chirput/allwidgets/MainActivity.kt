package com.chirput.allwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.chirput.allwidgets.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appWidgetManager: AppWidgetManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        appWidgetManager = getSystemService(APPWIDGET_SERVICE) as AppWidgetManager

        binding.fetchAllWidgets.setOnClickListener {
            Toast.makeText(this, "Fetching widgets", Toast.LENGTH_SHORT).show()
            populateWidgets()
        }
    }

    private fun populateWidgets() {
        val providers: List<AppWidgetProviderInfo> = appWidgetManager.installedProviders

        for(info in providers) {
            Log.d("SOHAIL_BRO", "$info")
        }
    }

}