package com.opendroid.ai.core.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest

/**
 * In-app self-update from GitHub Releases.
 * Checks releases/latest, compares versionName, downloads APK with SHA-256
 * verification, hands it to the system installer.
 */
object Updater {
    private const val GITHUB_API = "https://api.github.com/repos/genzxproject/opendroid/releases/latest"

    data class UpdateInfo(
        val hasUpdate: Boolean,
        val latestVersion: String = "",
        val apkUrl: String = "",
        val notes: String = "",
        val sha256: String = ""
    )

    suspend fun checkForUpdate(currentVersion: String): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val conn = java.net.URL(GITHUB_API).openConnection() as java.net.HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.setRequestProperty("User-Agent", "OpenDroid")
            if (conn.responseCode != 200) return@withContext UpdateInfo(false)
            val body = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(body)
            val tag = json.optString("tag_name", "").removePrefix("v")
            var apkUrl = ""
            var sha = ""
            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val a = assets.getJSONObject(i)
                    if (a.optString("name").endsWith(".apk")) {
                        apkUrl = a.optString("browser_download_url")
                        sha = a.optJSONObject("digest")?.optString("sha256", "") ?: ""
                        break
                    }
                }
            }
            UpdateInfo(
                hasUpdate = tag.isNotBlank() && compareVersions(tag, currentVersion) > 0,
                latestVersion = tag,
                apkUrl = apkUrl,
                notes = json.optString("body", ""),
                sha256 = sha
            )
        } catch (e: Exception) {
            UpdateInfo(false)
        }
    }

    /** Returns >0 if a > b, 0 if equal, <0 if a < b. */
    fun compareVersions(a: String, b: String): Int {
        val pa = a.split(".").map { it.toIntOrNull() ?: 0 }
        val pb = b.split(".").map { it.toIntOrNull() ?: 0 }
        for (i in 0 until maxOf(pa.size, pb.size)) {
            val x = pa.getOrElse(i) { 0 }
            val y = pb.getOrElse(i) { 0 }
            if (x != y) return if (x > y) 1 else -1
        }
        return 0
    }

    suspend fun downloadApk(
        apkUrl: String,
        context: Context,
        expectedSha256: String = "",
        onProgress: (Float) -> Unit = {}
    ): File? = withContext(Dispatchers.IO) {
        try {
            val conn = java.net.URL(apkUrl).openConnection() as java.net.HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 120000
            conn.setRequestProperty("User-Agent", "OpenDroid")
            val total = conn.contentLengthLong
            val target = File(context.cacheDir, "opendroid-update.apk")
            conn.inputStream.use { input ->
                target.outputStream().use { output ->
                    val buf = ByteArray(64 * 1024)
                    var downloaded = 0L
                    while (true) {
                        val n = input.read(buf)
                        if (n < 0) break
                        output.write(buf, 0, n)
                        downloaded += n
                        if (total > 0) onProgress(downloaded.toFloat() / total)
                    }
                }
            }
            if (expectedSha256.isNotBlank()) {
                val actual = sha256(target)
                if (!actual.equals(expectedSha256, ignoreCase = true)) {
                    target.delete()
                    return@withContext null
                }
            }
            target
        } catch (e: Exception) {
            null
        }
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buf = ByteArray(64 * 1024)
            while (true) {
                val n = input.read(buf)
                if (n < 0) break
                digest.update(buf, 0, n)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun installApk(context: Context, apkFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun canRequestInstall(context: Context): Boolean =
        context.packageManager.canRequestPackageInstalls()

    fun openInstallSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            data = Uri.parse("package:${context.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
