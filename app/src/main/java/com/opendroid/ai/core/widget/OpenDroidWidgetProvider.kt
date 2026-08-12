package com.opendroid.ai.core.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.opendroid.ai.MainActivity
import com.opendroid.ai.R

/**
 * Home screen widget — quick status: agent mode (AUTO/YOLO/ASK) + app version.
 * Tap opens the app. Data comes from a lightweight broadcast sent by the app.
 */
class OpenDroidWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_opendroid)
            val mode = WidgetStateStore.getMode(context)
            views.setTextViewText(R.id.widget_mode, "Mode: $mode")
            views.setTextViewText(R.id.widget_version, "v${WidgetStateStore.getVersion(context)}")

            val intent = Intent(context, MainActivity::class.java)
            val pi = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pi)
            appWidgetManager.updateAppWidget(id, views)
        }
    }
}
