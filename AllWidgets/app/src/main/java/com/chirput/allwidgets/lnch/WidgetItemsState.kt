package com.chirput.allwidgets.lnch


internal class WidgetItemsState {
    private val appWidgets: MutableList<AppWidget?> = ArrayList()
    var isResizeMode = false
        private set

    fun setResizeMode(resizeMode: Boolean, forceResize: Boolean) {
        isResizeMode = resizeMode
        for (appWidget in appWidgets) {
            appWidget!!.resizeMode = resizeMode
            appWidget.forceResize = resizeMode && forceResize
        }
    }

    fun addWidget(appWidget: AppWidget) {
        appWidget.resizeMode = isResizeMode
        appWidgets.add(appWidget)
    }

    fun clearWidgets() {
        appWidgets.clear()
        isResizeMode = false
    }

    fun removeWidgetById(appWidgetId: Int): Int {
        var i = 0
        val iterator = appWidgets.iterator()
        while (iterator.hasNext()) {
            if (iterator.next()!!.appWidgetId === appWidgetId) {
                iterator.remove()
                return i
            }
            i++
        }
        return -1
    }

    fun swapWidgets(from: Int, to: Int) {
        ListUtils.move(appWidgets, from, to)
    }

    val items: List<AppWidget?>
        get() = appWidgets

    fun setWidgetsOrder(order: List<Int?>) {
        if (order.isEmpty()) {
            return
        }
        val map: MutableMap<Int?, AppWidget?> = LinkedHashMap(appWidgets.size)
        for (appWidget in appWidgets) {
            map[appWidget!!.appWidgetId] = appWidget
        }
        appWidgets.clear()
        for (id in order) {
            val appWidget = map.remove(id)
            if (appWidget != null) {
                appWidgets.add(appWidget)
            }
        }
        appWidgets.addAll(map.values)
    }
}
