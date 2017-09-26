package com.kinglloy.album.data.cache

import android.text.TextUtils
import com.kinglloy.album.data.entity.AdvanceWallpaperEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/8/4.
 */
@Singleton
class AdvanceWallpaperCacheImpl @Inject constructor() : AdvanceWallpaperCache {
    private var wallpapers: List<AdvanceWallpaperEntity>? = null

    @Synchronized override fun put(wallpapers: List<AdvanceWallpaperEntity>) {
        this.wallpapers = wallpapers
    }

    override fun selectPreviewingWallpaper() {
        if (isDirty()) {
            throw IllegalStateException("Cache is dirty.")
        }
        for (wallpaper in wallpapers!!) {
            wallpaper.isSelected = wallpaper.isPreviewing
        }
    }

    override fun previewWallpaper(wallpaperId: String) {
        if (isDirty()) {
            throw IllegalStateException("Cache is dirty.")
        }
        for (wallpaper in wallpapers!!) {
            wallpaper.isPreviewing = TextUtils.equals(wallpaperId, wallpaper.wallpaperId)
        }
    }

    override fun getWallpapers(): List<AdvanceWallpaperEntity> {
        if (isDirty()) {
            throw IllegalStateException("Cache is dirty.")
        }
        return ArrayList(wallpapers!!)
    }

    override fun getWallpaper(wallpaperId: String): AdvanceWallpaperEntity? {
        if (!isCached(wallpaperId)) {
            throw IllegalStateException("WallpaperId $wallpaperId is not cached.")
        }
        return wallpapers!!.firstOrNull { TextUtils.equals(wallpaperId, it.wallpaperId) }
    }

    @Synchronized override fun evictAll() {
        wallpapers = null
    }

    override fun isCached(wallpaperId: String): Boolean {
        if (isDirty()) {
            return false
        }
        return wallpapers!!.any { TextUtils.equals(wallpaperId, it.wallpaperId) }
    }

    override fun isDirty(): Boolean {
        return wallpapers == null || wallpapers!!.isEmpty()
    }

    override fun makeDirty() {
        evictAll()
    }

}