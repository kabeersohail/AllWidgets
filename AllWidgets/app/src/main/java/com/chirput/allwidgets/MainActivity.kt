package com.chirput.allwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.LauncherApps.PinItemRequest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.chirput.allwidgets.databinding.ActivityMainBinding

private const val APP_WIDGET_HOST_ID = 101

private const val ACTION_PIN_APPWIDGET = "android.content.pm.action.CONFIRM_PIN_APPWIDGET"


class MainActivity : AppCompatActivity(), IntentQueue.OnIntentAction {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appWidgetHost: LauncherAppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager
    private var newAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var widgetSizeHelper: WidgetSizeHelper
    private lateinit var adapter: WidgetAdapter
    private lateinit var cellSize: CellSize
    private lateinit var widgetsList: RecyclerView
    private val preferences: Preferences? = null

    private val addWidgetLauncher = registerForActivityResult<Int, AppWidgetProviderInfo>(
        WidgetGalleryActivity.SelectContract()
    ) { info: AppWidgetProviderInfo? ->
        onNewWidgetSelected(info)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_widgets)

        appWidgetHost = LauncherAppWidgetHost(this, APP_WIDGET_HOST_ID)
        appWidgetHost.startListening()
        appWidgetManager = this.getSystemService(APPWIDGET_SERVICE) as AppWidgetManager
        widgetSizeHelper = WidgetSizeHelper(this)

        widgetsList = findViewById(R.id.widgets_list)


        adapter = WidgetAdapter(appWidgetHost, cellSize)
        widgetsList.adapter = adapter

        bindWidgets()


        binding.start.setOnClickListener {
            startAddNewWidget()
        }
    }

    private fun bindWidgets() {
        val widgets: List<Preferences.Widget> = preferences.get(Preferences.WIDGETS_DATA)
        val widgetsMap: MutableMap<Int, Preferences.Widget> = HashMap(widgets.size)
        val widgetsOrder: MutableList<Int> = ArrayList(widgets.size)
        for (widget in widgets) {
            widgetsMap[widget.appWidgetId] = widget
            widgetsOrder.add(widget.appWidgetId)
        }
        widgetItemsState.clearWidgets()
        for (appWidgetId in appWidgetHost.appWidgetIds) {
            val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
            if (info != null) {
                addWidgetToScreen(appWidgetId, info, widgetsMap[appWidgetId])
            } else {
                appWidgetHost.deleteAppWidgetId(appWidgetId)
            }
        }
        widgetItemsState.setWidgetsOrder(widgetsOrder)
        adapter.setItems(widgetItemsState.getItems())
        adapter.notifyDataSetChanged()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

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

        val widgetContainer = binding.widgetContainer
        widgetContainer.addView(widgetView)
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

    override fun onIntent(intent: Intent): Boolean {
        if (ACTION_PIN_APPWIDGET != intent.action) {
            return false
        }
        val context: Context = this
        val launcherApps = context.getSystemService(LAUNCHER_APPS_SERVICE) as LauncherApps
        val request = launcherApps.getPinItemRequest(intent)
        if (request == null || request.requestType != PinItemRequest.REQUEST_TYPE_APPWIDGET || !request.isValid) {
            return false
        }
        val info = request.getAppWidgetProviderInfo(context)
        if (info != null) {
            newAppWidgetId = appWidgetHost.allocateAppWidgetId()
            val options = Bundle()
            options.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, newAppWidgetId)
            if (request.accept(options)) {
                addNewWidget(info)
            } else {
                cancelAddNewWidget(newAppWidgetId)
            }
        }
        return true
    }
}
