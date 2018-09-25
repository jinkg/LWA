package com.kinglloy.album.data.repository.datasource

import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.domain.WallpaperType
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
interface WallpaperDataStore {

    fun getPreviewWallpaperEntity(): WallpaperEntity

    fun getWallpaperEntities(): Observable<List<WallpaperEntity>>
    fun getDownloadedWallpaperEntities(): Observable<List<WallpaperEntity>>

    fun deleteDownloadedWallpapers(wallpapers: List<WallpaperEntity>): Observable<Boolean>

    fun selectPreviewingWallpaper(): Observable<Boolean>
    fun unSelectWallpaper(): Observable<Boolean>
    fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean>

    fun activeService(serviceType: Int): Observable<Boolean>
    fun getActiveService(): Observable<Int>
}