package com.kinglloy.album.engine

import android.content.Context
import android.service.wallpaper.WallpaperService
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.domain.interactor.GetPreviewWallpaper
import com.kinglloy.album.domain.interactor.settings.GetStyleWallpaperSettings
import com.kinglloy.album.engine.live.BokehRainbowWallpaper
import com.kinglloy.album.engine.log.LogUtil
import com.kinglloy.album.engine.video.VideoWallpaper
import javax.inject.Inject

/**
 * @author jinyalin
 * *
 * @since 2017/7/27.
 */

class ProxyProvider @Inject constructor(private val getPreviewWallpaper: GetPreviewWallpaper,
                                        private val getStyleWallpaperSettings: GetStyleWallpaperSettings) {
    companion object {
        private const val STYLE_PROXY_CLASS = "com.kinglloy.album.engine.style.StyleWallpaperProxy"
        private const val HD_PROXY_CLASS = "com.kinglloy.album.engine.hd.HDWallpaperProxy"
    }

    fun provideProxy(host: Context): WallpaperService {
        val previewing = getPreviewWallpaper.previewing

        LogUtil.F("ProxyProvider", previewing.name + " "
                + previewing.wallpaperType + " " + previewing.storePath + " " + previewing.isDefault)
        if (previewing.isDefault) {
            return BokehRainbowWallpaper(host)
        }
        when {
            previewing.wallpaperType == WallpaperType.STYLE -> return try {
                val constructor = Class.forName(STYLE_PROXY_CLASS)
                        .getConstructor(Context::class.java, String::class.java,
                                GetStyleWallpaperSettings::class.java)
                return constructor.newInstance(host, previewing.storePath,
                        getStyleWallpaperSettings) as WallpaperService
            } catch (e: Exception) {
                e.printStackTrace()
                BokehRainbowWallpaper(host)
            }
            previewing.wallpaperType == WallpaperType.VIDEO ->
                return VideoWallpaper(host, previewing.storePath)
            previewing.wallpaperType == WallpaperType.LIVE -> {
                val proxy = ProxyApi.getProxy(host, previewing.storePath, previewing.providerName)
                if (proxy != null) {
                    return proxy
                }
                return BokehRainbowWallpaper(host)
            }
            else -> return try {
                val constructor = Class.forName(HD_PROXY_CLASS)
                        .getConstructor(Context::class.java, String::class.java)
                return constructor.newInstance(host, previewing.storePath) as WallpaperService
            } catch (e: Exception) {
                e.printStackTrace()
                BokehRainbowWallpaper(host)
            }
        }
    }
}
