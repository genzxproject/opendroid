package com.opendroid.ai.core.widget

import android.app.PendingIntent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.opendroid.ai.BuildConfig
import com.opendroid.ai.R

/**
 * Quick Settings tile — one tap opens the app; long-press goes to settings.
 * State (AUTO/OFF/YOLO) reflects the current approval mode from the widget store.
 */
class OpenDroidTileService : TileService() {

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        // Launch the main app on tap
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pi = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pi)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val mode = WidgetStateStore.getMode(this)
        tile.state = Tile.STATE_ACTIVE
        tile.label = "OpenDroid · $mode"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = "v${BuildConfig.VERSION_NAME}"
        }
        tile.icon = androidx.core.graphics.drawable.IconCompat.createWithResource(this, R.drawable.ic_launcher_monochrome)
            .toIcon(this)
        tile.updateTile()
    }
}
