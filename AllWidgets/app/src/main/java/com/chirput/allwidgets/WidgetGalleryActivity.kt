package com.chirput.allwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chirput.allwidgets.databinding.ActivityWidgetGalleryBinding

private const val EXTRA_RESULT = "result"
private const val EXTRA_APP_WIDGET_ID = "app_widget_id"

class WidgetGalleryActivity : AppCompatActivity(), Listener {

    private lateinit var binding: ActivityWidgetGalleryBinding
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var recyclerView: RecyclerView
    private var appWidgetId: Int = 0

    private val bindWidgetLauncher: ActivityResultLauncher<Input?> =
        registerForActivityResult(
            BindWidgetContract()
        ) {
            val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
            setResult(
                RESULT_OK,
                Intent().putExtra(
                    EXTRA_RESULT,
                    info
                )
            )
            finish()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_gallery)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_widget_gallery)

        appWidgetManager = getSystemService(APPWIDGET_SERVICE) as AppWidgetManager
        appWidgetId = intent.getIntExtra(
            EXTRA_APP_WIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )


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

            widgetItems.add(
                WidgetModel(info.label, info.provider, widgetPreviewUri, icon, description, info)
            )
        }

        recyclerView.adapter = WidgetAdapter(this, widgetItems, this)
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

    override fun onWidgetSelected(info: AppWidgetProviderInfo?) {
        if (appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info!!.provider)) {
            setResult(RESULT_OK, Intent().putExtra(EXTRA_RESULT, info))
            finish()
        } else {
            bindWidgetLauncher.launch(
                Input(appWidgetId, info)
            )
        }
    }


    internal class Input(val appWidgetId: Int, val info: AppWidgetProviderInfo)


    private class BindWidgetContract :
        ActivityResultContract<Input?, Any?>() {
        override fun createIntent(
            context: Context,
            input: Input?
        ): Intent {
            return Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, input?.appWidgetId)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, input?.info?.provider)
                .putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE,
                    input?.info?.profile
                )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Any? {
            return null
        }
    }


    class SelectContract : ActivityResultContract<Int?, AppWidgetProviderInfo?>() {
        override fun createIntent(context: Context, input: Int?): Intent {
            return Intent(context, WidgetGalleryActivity::class.java)
                .putExtra(EXTRA_APP_WIDGET_ID, input)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): AppWidgetProviderInfo? {
            return if (resultCode == RESULT_OK && intent != null) intent.getParcelableExtra(
                EXTRA_RESULT
            ) else null
        }
    }


}