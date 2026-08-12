package com.opendroid.ai.core.widget

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
        startActivityAndCollapse(intent)
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val mode = WidgetStateStore.getMode(this)
        tile.state = Tile.STATE_ACTIVE
        tile.label = "OpenDroid · $mode"
        tile.subtitle = "v${BuildConfig.VERSION_NAME}"
        tile.icon = androidx.core.graphics.drawable.IconCompat.createWithResource(this, R.drawable.ic_launcher_monochrome)
            .toIcon(this)
        tile.updateTile()
    }
}
