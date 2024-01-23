package com.chirput.allwidgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chirput.allwidgets.databinding.ActivityMainBinding
import com.chirput.allwidgets.lnch.AppWidget
import com.chirput.allwidgets.lnch.CellSize
import com.chirput.allwidgets.lnch.LauncherAppWidgetHost
import com.chirput.allwidgets.lnch.PreviewWidgetHostView
import com.chirput.allwidgets.lnch.WidgetAdapter
import com.chirput.allwidgets.lnch.WidgetHelper
import com.chirput.allwidgets.lnch.WidgetItemsState
import com.chirput.allwidgets.lnch.WidgetResizeFrame
import com.chirput.allwidgets.lnch.WidgetSizeHelper

private const val APP_WIDGET_HOST_ID = 101

class MainActivity : AppCompatActivity(), WidgetAdapter.WidgetActionListener {


    lateinit var binding: ActivityMainBinding
    private lateinit var appWidgetHost: LauncherAppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager
    private var newAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    lateinit var hostView: AppWidgetHostView
    private val gridSize = 8
    private val heightRatio = 2f
    private val widgetItemsState: WidgetItemsState = WidgetItemsState()
    private var adapter: WidgetAdapter? = null
    private val widgetsList: RecyclerView? = null
    private var cellSize: CellSize? = null

    private var itemTouchHelper: ItemTouchHelper? = null
    private val callback: com.italankin.lnch.feature.widgets.WidgetsFragment.Callback? = null


//        fun resize(
//            container: ResizableFrameLayout = binding.widgetContainer,
//            width: Int,
//            height: Int,
//            hostView: AppWidgetHostView
//        ) {
//
//            container.removeAllViews()
//
//            // Add it to your layout
//            val layoutParams = FrameLayout.LayoutParams(width, height)
//            container.addView(hostView, layoutParams)
//        }




    private lateinit var widgetHostView: PreviewWidgetHostView
    private val addWidgetLauncher = registerForActivityResult<Int, AppWidgetProviderInfo>(
        WidgetGalleryActivity.SelectContract()
    ) { info: AppWidgetProviderInfo? ->
        onNewWidgetSelected(info)
    }

    private val reconfigureWidgetLauncher: ActivityResultLauncher<ConfigureWidgetContract.Input?> =
        registerForActivityResult(
            ConfigureWidgetContract(),
            ActivityResultCallback<Boolean?> { o: Boolean? -> })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        appWidgetHost = LauncherAppWidgetHost(this, APP_WIDGET_HOST_ID)
        appWidgetHost.startListening()
        appWidgetManager = AppWidgetManager.getInstance(this)

        binding.widgetFrame.setResizeMode(true, false)
        widgetHostView = PreviewWidgetHostView(this)
        updateWidgetPreview()

        val dragDirs = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT


        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(dragDirs, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.bindingAdapterPosition
                val to = target.bindingAdapterPosition
                widgetItemsState.swapWidgets(from, to)
                adapter?.notifyItemMoved(from, to)
                return true
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })

        val widgetLongClickListener = WidgetResizeFrame.OnStartDragListener { resizeFrame ->
            val holder: RecyclerView.ViewHolder? = widgetsList?.findContainingViewHolder(resizeFrame)
            if(holder != null) {
                itemTouchHelper?.startDrag(holder)
            }
        }

        val size = WidgetSizeHelper.calculateSizeForCell(
            this, gridSize,
            heightRatio
        )
        val maxHeightCells = WidgetSizeHelper.calculateMaxHeightCells(this, size.height)

        cellSize = CellSize(size.width, size.height, gridSize, maxHeightCells)

        adapter = WidgetAdapter(appWidgetHost, cellSize, widgetLongClickListener, this)

        binding.start.setOnClickListener {
            startAddNewWidget()
        }
    }

    private fun updateWidgetPreview() {
        val size: Size = WidgetSizeHelper.calculateSizeForCell(this, gridSize, heightRatio)
        val cellSize = CellSize(size.width, size.height, gridSize, 2)
        val appWidgetSize: AppWidget.Size =
            AppWidget.Size(cellSize.maxAvailableWidth(), cellSize.maxAvailableHeight())
        val appWidget = AppWidget(AppWidgetManager.INVALID_APPWIDGET_ID, null, Bundle(), appWidgetSize)
        binding.widgetFrame.bindAppWidget(appWidget, widgetHostView)
        binding.widgetFrame.setCellSize(cellSize)
        val lp: ViewGroup.LayoutParams = binding.widgetFrame.layoutParams
        lp.width = appWidgetSize.width
        lp.height = appWidgetSize.height
        binding.widgetFrame.layoutParams = lp
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

//        CoroutineScope(Dispatchers.Main).launch {
//            createWidget(widgetView.appWidgetInfo, binding.widgetContainer, 100, 100)
//        }

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

    override fun onWidgetReconfigure(appWidget: AppWidget?) {
        reconfigureWidget(appWidget!!.appWidgetId, appWidget.providerInfo)
    }

    override fun onWidgetDelete(appWidget: AppWidget?) {
        TODO("Not yet implemented")
    }

    private fun reconfigureWidget(appWidgetId: Int, info: AppWidgetProviderInfo) {
        if (info.configure == null) {
            return
        }
        if (WidgetHelper.isConfigureActivityExported(this, info)) {
            try {
                reconfigureWidgetLauncher.launch(
                    ConfigureWidgetContract.Input(
                        appWidgetId,
                        info.configure
                    )
                )
                exitEditModeOnStop = false
            } catch (e: Exception) {
            }
        } else if (callback != null) {
            try {
                callback.startAppWidgetConfigureActivity(appWidgetHost, appWidgetId)
                exitEditModeOnStop = false
            } catch (e: Exception) {
                Timber.e(e, "startAppWidgetConfigureActivityForResult: %s", e.message)
            }
        }
    }


    interface Callback {
        fun startAppWidgetConfigureActivity(appWidgetHost: AppWidgetHost?, appWidgetId: Int)
    }


    private class ConfigureWidgetContract :
        ActivityResultContract<ConfigureWidgetContract.Input?, Boolean?>() {
        override fun createIntent(context: Context, input: Input?): Intent {
            return Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
                .setComponent(input?.configure)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, input?.appWidgetId)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return resultCode != RESULT_CANCELED
        }

        internal class Input(val appWidgetId: Int, val configure: ComponentName)
    }


}
