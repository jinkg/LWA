package com.kinglloy.album.data.repository.datasource.provider.settings

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.kinglloy.album.data.utils.readStyleSettings
import com.kinglloy.album.data.utils.notifyChange
import com.kinglloy.album.data.utils.updateStyleSettings

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
class SettingsProvider : ContentProvider() {
    lateinit var uriMatcher: SettingsProviderUriMatcher

    override fun insert(uri: Uri, values: ContentValues): Uri? = null

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return if (context == null) null else when (uriMatcher.matchUri(uri)) {
            SettingsUriEnum.STYLE_WALLPAPER_SETTINGS -> {
                readStyleSettings(context!!)
            }
        }
    }

    override fun onCreate(): Boolean {
        uriMatcher = SettingsProviderUriMatcher()
        return true
    }

    override fun update(uri: Uri, values: ContentValues, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        return when (uriMatcher.matchUri(uri)) {
            SettingsUriEnum.STYLE_WALLPAPER_SETTINGS -> {
                updateStyleSettings(context, values)
                notifyChange(context, SettingsContract.StyleWallpaperSettings.CONTENT_URI)
                1
            }
        }
    }

    override fun delete(uri: Uri?, p1: String?, p2: Array<out String>?) = 0

    override fun getType(uri: Uri?): String? = null


}