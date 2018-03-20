package com.kinglloy.album.data.repository.datasource.net

import android.content.Context
import com.kinglloy.album.data.BuildConfig

/**
 * @author jinyalin
 * @since 2018/3/20.
 */
class RemoteHDWallpaperFetcher(val context: Context) : DataFetcher(context) {
    override fun getUrl(): String {
        return BuildConfig.SERVER_WALLPAPER_ENDPOINT + "/hd_wallpaper"
    }

}