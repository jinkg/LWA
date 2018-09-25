package com.kinglloy.album.data.repository.datasource

import android.content.Context
import android.database.Cursor
import com.kinglloy.album.data.cache.WallpaperCache
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.domain.WallpaperType
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
class LiveWallpaperDataStoreImpl(private val context: Context,
                                 private val wallpaperCache: WallpaperCache)
    : WallpaperDataStore {
    companion object {
        val TAG = "AdvanceDataStore"

        val DEFAULT_WALLPAPER_ID = "-1"
    }

    @Synchronized override fun getPreviewWallpaperEntity(): WallpaperEntity {
        throw UnsupportedOperationException("Live wallpaper data store not support get wallpaper.")
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> =
            createAdvanceWallpapersFromDB().doOnNext(wallpaperCache::putWallpapers)

    override fun getDownloadedWallpaperEntities(): Observable<List<WallpaperEntity>> {
        throw UnsupportedOperationException("Live wallpaper data store not support get downloaded wallpaper.")
    }

    override fun deleteDownloadedWallpapers(wallpapers: List<WallpaperEntity>): Observable<Boolean> {
        throw UnsupportedOperationException("Live wallpaper data store not support delete downloaded wallpaper.")
    }

    override fun selectPreviewingWallpaper():
            Observable<Boolean> {
        throw UnsupportedOperationException("Live wallpaper data store not support select previewing.")
    }

    override fun unSelectWallpaper(): Observable<Boolean> {
        throw UnsupportedOperationException("Live wallpaper data store not support unselect previewing.")
    }

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> {
        throw UnsupportedOperationException("Live wallpaper data store not support preview.")
    }

    override fun activeService(serviceType: Int): Observable<Boolean> {
        throw UnsupportedOperationException("Live wallpaper data store not support active service.")
    }

    override fun getActiveService(): Observable<Int> {
        throw UnsupportedOperationException("Live wallpaper data store not support get active service.")
    }

    fun loadWallpaperEntity(wallpaperId: String): WallpaperEntity {
        var entity: WallpaperEntity? = null
        synchronized(wallpaperCache) {
            if (!wallpaperCache.isDirty()
                    && wallpaperCache.isWallpaperCached(wallpaperId)) {
                entity = wallpaperCache.getWallpaper(wallpaperId)
            } else {
                entity = loadWallpaperEntityFromDB(wallpaperId)
            }
            if (entity == null) {
                entity = loadWallpaperEntityFromDB(wallpaperId)
            }
        }
        return entity!!
    }

    private fun loadWallpaperEntityFromDB(wallpaperId: String): WallpaperEntity? {
        var cursor: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(AlbumContract.LiveWallpaper
                    .buildWallpaperUri(wallpaperId),
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val entity = WallpaperEntity.liveWallpaperValue(cursor)
                return entity
            }
            return null
        } finally {
            cursor?.close()
        }
    }

    private fun createAdvanceWallpapersFromDB(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            var cursor: Cursor? = null
            val validWallpapers = ArrayList<WallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.LiveWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(WallpaperEntity.liveWallpaperValues(cursor))
            } finally {
                cursor?.close()
            }

            emitter.onNext(validWallpapers)
            emitter.onComplete()
        }
    }


}