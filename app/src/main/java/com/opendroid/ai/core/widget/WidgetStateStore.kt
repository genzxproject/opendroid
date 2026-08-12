package com.opendroid.ai.core.widget

import android.content.Context
import androidx.core.content.edit

/** Tiny SharedPreferences bridge between the app and the widget. */
object WidgetStateStore {
    private const val PREFS = "opendroid_widget"
    private const val KEY_MODE = "mode"
    private const val KEY_VERSION = "version"

    fun getMode(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_MODE, "ASK") ?: "ASK"

    fun setMode(context: Context, mode: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit { putString(KEY_MODE, mode) }
    }

    fun getVersion(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_VERSION, "") ?: ""

    fun setVersion(context: Context, version: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit { putString(KEY_VERSION, version) }
    }
}
