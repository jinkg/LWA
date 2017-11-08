package com.kinglloy.album.data.repository.datasource

import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.content.Context
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.RemoteException
import com.google.gson.JsonParser
import com.kinglloy.album.data.R
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.entity.mapper.WallpaperEntityMapper
import com.kinglloy.album.data.exception.NetworkConnectionException
import com.kinglloy.album.data.exception.NoContentException
import com.kinglloy.album.data.exception.RemoteServerException
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.io.VideoWallpaperHandler
import com.kinglloy.album.data.repository.datasource.net.RemoteVideoWallpaperFetcher
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.repository.datasource.sync.SyncHelper
import com.kinglloy.album.data.repository.datasource.sync.account.Account
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.domain.interactor.DefaultObserver
import io.reactivex.Observable

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class VideoRemoteWallpaperDataStore(val context: Context,
                                    private val localDataStore: VideoWallpaperDataStoreImpl)
    : WallpaperDataStore {

    companion object {
        val TAG = "VideoRemoteWallpaperDataStore"
    }

    private val wallpaperHandler = VideoWallpaperHandler(context, WallpaperEntityMapper())

    override fun getPreviewWallpaperEntity(): WallpaperEntity {
        throw UnsupportedOperationException("Remote video wallpaper data store not support get preview.")
    }

    override fun getWallpaperEntities(): Observable<List<WallpaperEntity>> {
        return Observable.create { emitter ->
            if (!SyncHelper.isOnline(context)) {
                emitter.onError(NetworkConnectionException())
                return@create
            }
            val account = Account.getAccount()
            val authority = context.getString(R.string.authority)
            ContentResolver.cancelSync(account, authority)

            val batch = ArrayList<ContentProviderOperation>()
            try {
                val wallpapers = RemoteVideoWallpaperFetcher(context).fetchDataIfNewer()
                val parser = JsonParser()
                val handler = VideoWallpaperHandler(context, WallpaperEntityMapper())
                handler.process(parser.parse(wallpapers))
                handler.makeContentProviderOperations(batch)
            } catch (e: Exception) {
                emitter.onError(RemoteServerException())
                return@create
            }

            try {
                val operations = batch.size
                if (operations > 0) {
                    context.contentResolver.applyBatch(AlbumContract.AUTHORITY, batch)
                }
            } catch (ex: RemoteException) {
                LogUtil.D(TAG, "RemoteException while applying content provider operations.")
                throw RuntimeException("Error executing content provider batch operation", ex)
            } catch (ex: OperationApplicationException) {
                LogUtil.D(TAG, "OperationApplicationException while applying content provider operations.")
                throw RuntimeException("Error executing content provider batch operation", ex)
            }

            var cursor: Cursor? = null
            val validWallpapers = ArrayList<WallpaperEntity>()
            try {
                val contentResolver = context.contentResolver
                cursor = contentResolver.query(AlbumContract.VideoWallpaper.CONTENT_URI,
                        null, null, null, null)
                validWallpapers.addAll(WallpaperEntity.videoWallpaperValues(cursor))
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }

            if (validWallpapers.isEmpty()) {
                emitter.onError(NoContentException())
            } else {
                emitter.onNext(validWallpapers)
            }
            emitter.onComplete()
        }
    }

    override fun selectPreviewingWallpaper(): Observable<Boolean> {
        throw UnsupportedOperationException("Remote video wallpaper data store not support select preview.")
    }

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> {
        throw UnsupportedOperationException("Remote video wallpaper data store not support previewing wallpaper.")
    }

    override fun downloadWallpaper(wallpaperId: String): Observable<Long> {
        return Observable.create { emitter ->
            val entity = localDataStore.loadWallpaperEntity(wallpaperId)
            wallpaperHandler.downloadVideoWallpaper(entity, object : DefaultObserver<Long>() {
                override fun onNext(downloadedLength: Long) {
                    emitter.onNext(downloadedLength)
                }

                override fun onComplete() {
                    emitter.onComplete()
                }

                override fun onError(exception: Throwable) {
                    emitter.onError(exception)
                }

            })
        }
    }

    override fun activeService(serviceType: Int): Observable<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getActiveService(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}