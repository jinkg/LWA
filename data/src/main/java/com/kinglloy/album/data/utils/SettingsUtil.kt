package com.kinglloy.album.data.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import com.kinglloy.album.data.repository.datasource.provider.settings
        .SettingsContract.StyleWallpaperSettings
import com.kinglloy.album.data.repository.datasource.provider.settings
        .SettingsContract.StyleWallpaperSettingsColumns.Companion.COLUMN_NAME_BLUR
import com.kinglloy.album.data.repository.datasource.provider.settings
        .SettingsContract.StyleWallpaperSettingsColumns.Companion.COLUMN_NAME_DIM
import com.kinglloy.album.data.repository.datasource.provider.settings
        .SettingsContract.StyleWallpaperSettingsColumns.Companion.COLUMN_NAME_ENABLE_EFFECT
import com.kinglloy.album.data.repository.datasource.provider.settings
        .SettingsContract.StyleWallpaperSettingsColumns.Companion.COLUMN_NAME_GREY

/**
 * @author jinyalin
 * @since 2017/11/4.
 */
const val STYLE_SETTINGS_MAX_BLUR = 500
const val STYLE_SETTINGS_MAX_DIM = 128
const val STYLE_SETTINGS_MAX_GREY = 128

fun updateStyleSettings(context: Context, value: ContentValues) {
    val enable = value.getAsBoolean(COLUMN_NAME_ENABLE_EFFECT)
    val blur = value.getAsInteger(COLUMN_NAME_BLUR)
    val dim = value.getAsInteger(COLUMN_NAME_DIM)
    val grey = value.getAsInteger(COLUMN_NAME_GREY)

    val editor = context.getSharedPreferences(
            StyleWallpaperSettings.SP_NAME, Context.MODE_PRIVATE).edit()
    if (enable != null) {
        editor.putBoolean(COLUMN_NAME_ENABLE_EFFECT, enable)
    }
    if (blur != null) {
        editor.putInt(COLUMN_NAME_BLUR, blur)
    }
    if (dim != null) {
        editor.putInt(COLUMN_NAME_DIM, dim)
    }
    if (grey != null) {
        editor.putInt(COLUMN_NAME_GREY, grey)
    }
    editor.apply()
}

fun readStyleSettings(context: Context): Cursor {
    val sp = context.getSharedPreferences(
            StyleWallpaperSettings.SP_NAME, Context.MODE_PRIVATE)
    val enable = sp.getBoolean(COLUMN_NAME_ENABLE_EFFECT, true)
    val blur = sp.getInt(COLUMN_NAME_BLUR, 150)
    val dim = sp.getInt(COLUMN_NAME_DIM, 64)
    val grey = sp.getInt(COLUMN_NAME_GREY, 0)

    val cursor = MatrixCursor(arrayOf(COLUMN_NAME_ENABLE_EFFECT, COLUMN_NAME_BLUR,
            COLUMN_NAME_DIM, COLUMN_NAME_GREY), 1)
    cursor.addRow(arrayOf(if (enable) 1 else 0, blur, dim, grey))
    return cursor
}