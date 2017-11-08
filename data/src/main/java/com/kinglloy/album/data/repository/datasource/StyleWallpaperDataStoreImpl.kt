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
 * @since 2017/10/31.
 */
class StyleWallpaperDataStoreImpl(private val context: Context,
                                  private val wallpaperCache: WallpaperCache)
    : WallpaperDataStore {
    override fun getPreviewWallpaperEntity(): WallpaperEntity {
        throw UnsupportedOperationException("Style wallpaper data store not support get wallpaper.")
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> =
            createStyleWallpapersFromDB().doOnNext(wallpaperCache::putWallpapers)

    override fun selectPreviewingWallpaper(): Observable<Boolean> {
        throw UnsupportedOperationException("Style wallpaper data store not support select previewing.")
    }

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> {
        throw UnsupportedOperationException("Style wallpaper data store not support preview.")
    }

    override fun downloadWallpaper(wallpaperId: String): Observable<Long> {
        throw UnsupportedOperationException("Style wallpaper data store not support preview.")
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

    override fun activeService(serviceType: Int): Observable<Boolean> {
        throw UnsupportedOperationException("Style wallpaper data store not support active service.")
    }

    override fun getActiveService(): Observable<Int> {
        throw UnsupportedOperationException("Style wallpaper data store not support get active service.")
    }

    private fun loadWallpaperEntityFromDB(wallpaperId: String): WallpaperEntity? {
        var cursor: Cursor? = null
        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(AlbumContract.StyleWallpaper
                    .buildWallpaperUri(wallpaperId),
                    null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                return WallpaperEntity.styleWallpaperValue(cursor)
            }
            return null
        } finally {
            cursor?.close()
        }
    }

    private fun createStyleWallpapersFromDB(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            var cursor: Cursor? = null
            val validWallpapers = ArrayList<WallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.StyleWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(WallpaperEntity.styleWallpaperValues(cursor))
            } finally {
                cursor?.close()
            }

            emitter.onNext(validWallpapers)
            emitter.onComplete()
        }
    }
}
