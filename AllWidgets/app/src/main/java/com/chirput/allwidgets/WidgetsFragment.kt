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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chirput.allwidgets.databinding.FragmentWidgetsBinding

private const val EXTRA_RESULT = "result"
private const val APP_WIDGET_ID = 123

class WidgetsFragment : Fragment(), Listener {

    private lateinit var binding: FragmentWidgetsBinding

    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var recyclerView: RecyclerView

    private val bindWidgetLauncher: ActivityResultLauncher<Input?> =
        registerForActivityResult(
            BindWidgetContract()
        ) {
            val info = appWidgetManager.getAppWidgetInfo(APP_WIDGET_ID)
            requireActivity().setResult(
                AppCompatActivity.RESULT_OK,
                Intent().putExtra(
                    EXTRA_RESULT,
                    info
                )
            )
            requireActivity().finish()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWidgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appWidgetManager =
            requireActivity().getSystemService(AppCompatActivity.APPWIDGET_SERVICE) as AppWidgetManager

        recyclerView = binding.widgetRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        populateWidgets()

    }

    private fun populateWidgets() {
        val providers: List<AppWidgetProviderInfo> = appWidgetManager.installedProviders
        val widgetItems: MutableList<WidgetModel> = mutableListOf()

        for (info in providers) {
            Log.d("SOHAIL_BRO", "$info")

            val widgetPreviewUri =
                Uri.parse("android.resource://${info.provider.packageName}/${info.previewImage}")
            val icon = getWidgetIcon(info.provider)

            var description = ""

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val desc = info.loadDescription(requireContext())
                if (!TextUtils.isEmpty(desc)) {
                    description = desc.toString()
                }
            }

            widgetItems.add(WidgetModel(info.label, info.provider, widgetPreviewUri, icon, description, info))
        }

        recyclerView.adapter = WidgetAdapter(requireContext(), widgetItems, this)
    }

    private fun getWidgetIcon(provider: ComponentName): Drawable? {
        return try {
            val appInfo =
                requireContext().packageManager.getApplicationInfo(provider.packageName, 0)
            appInfo.loadIcon(requireContext().packageManager)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_widget_no_preview)
        }
    }

    override fun onWidgetSelected(info: AppWidgetProviderInfo?) {
        if (appWidgetManager.bindAppWidgetIdIfAllowed(APP_WIDGET_ID, info!!.provider)) {
            requireActivity().setResult(
                AppCompatActivity.RESULT_OK,
                Intent().putExtra(EXTRA_RESULT, info)
            )
            requireActivity().finish()
        } else {
            bindWidgetLauncher.launch(
                Input(APP_WIDGET_ID, info)
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
}