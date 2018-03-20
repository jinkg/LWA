package com.kinglloy.album.data.repository.datasource.io

import android.content.ContentProviderOperation
import android.content.Context
import android.database.Cursor
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.kinglloy.album.data.SyncConfig
import com.kinglloy.album.data.entity.TempHDWallpaperEntity
import com.kinglloy.album.data.entity.WallpaperEntity
import com.kinglloy.album.data.entity.mapper.WallpaperEntityMapper
import com.kinglloy.album.data.exception.NetworkConnectionException
import com.kinglloy.album.data.exception.RemoteServerException
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.data.repository.datasource.provider.AlbumContractHelper
import com.kinglloy.album.data.utils.WallpaperFileHelper
import com.kinglloy.album.domain.interactor.DefaultObserver
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.URL
import java.util.HashSet
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

/**
 * @author jinyalin
 * @since 2018/3/20.
 */
class HDWallpaperHandler(context: Context,
                         val mapper: WallpaperEntityMapper) : JSONHandler(context) {
    companion object {
        const val TAG = "AdvanceWallpaperHandler"
        val downloadLock = ReentrantLock()
    }

    private var wallpapers: ArrayList<WallpaperEntity> = ArrayList()

    override fun makeContentProviderOperations(list: ArrayList<ContentProviderOperation>) {
        val uri = AlbumContractHelper.setUriAsCalledFromSyncAdapter(
                AlbumContract.HDWallpaper.CONTENT_URI)
        list.add(ContentProviderOperation.newDelete(uri).build())

        val validFiles = HashSet<String>()
        val selectedEntities = querySelectedWallpapers()
        validFiles.addAll(getWallpaperNameSet(selectedEntities))
        for (wallpaper in this.wallpapers) {
            wallpaper.storePath = makeStorePath(wallpaper)
            if (!selectedEntities.contains(wallpaper)) {
                LogUtil.D(TAG, "download wallpaper component "
                        + " success, do output wallpaper.")
                outputWallpaper(wallpaper, list)
                validFiles.add(makeFilename(wallpaper))
            }
        }
        // delete old wallpapers
        WallpaperFileHelper.deleteOldHDWallpaper(mContext, validFiles)
    }

    override fun process(element: JsonElement) {
        val tempWallpapers = Gson().fromJson(element, Array<TempHDWallpaperEntity>::class.java)
        this.wallpapers.ensureCapacity(tempWallpapers.size)
        this.wallpapers.addAll(mapper.transformFromTempHDEntity(tempWallpapers.toCollection(ArrayList())))
    }

    private fun makeFilename(wallpaper: WallpaperEntity): String {
        val suffix = getWallpaperFileSuffix(wallpaper.downloadUrl)
        return wallpaper.hashCode().toString() + "_hd" + suffix
    }

    private fun getWallpaperFileSuffix(downloadUrl: String): String {
        val suffixStart = downloadUrl.lastIndexOf(".")
        val suffix: String
        suffix = if (suffixStart >= 0) {
            downloadUrl.substring(suffixStart)
        } else {
            ".jpg"
        }
        return suffix
    }

    private fun makeStorePath(wallpaper: WallpaperEntity): String {
        val outputDir = WallpaperFileHelper.getHDWallpaperDir(mContext)
        return File(outputDir, makeFilename(wallpaper)).absolutePath
    }

    private fun getWallpaperNameSet(entities: List<WallpaperEntity>): Set<String> {
        val ids = HashSet<String>()
        for (entity in entities) {
            ids.add(makeFilename(entity))
        }
        return ids
    }

    private fun outputWallpaper(wallpaper: WallpaperEntity,
                                list: ArrayList<ContentProviderOperation>) {
        val uri = AlbumContractHelper.setUriAsCalledFromSyncAdapter(
                AlbumContract.HDWallpaper.CONTENT_URI)
        val builder = ContentProviderOperation.newInsert(uri)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_WALLPAPER_ID, wallpaper.wallpaperId)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_DOWNLOAD_URL, wallpaper.downloadUrl)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_ICON_URL, wallpaper.iconUrl)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_NAME, wallpaper.name)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_CHECKSUM, wallpaper.checkSum)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_STORE_PATH, wallpaper.storePath)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_SELECTED, 0)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_PREVIEWING, 0)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_SIZE, wallpaper.size)
        builder.withValue(AlbumContract.HDWallpaper.COLUMN_NAME_PRO, wallpaper.pro)

        list.add(builder.build())
    }

    private fun querySelectedWallpapers(): List<WallpaperEntity> {
        var cursor: Cursor? = null
        try {
            cursor = mContext.contentResolver.query(
                    AlbumContract.HDWallpaper.CONTENT_SELECTED_URI, null, null, null, null)
            return WallpaperEntity.hdWallpaperValues(cursor)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    private fun downloadHDWallpaper(wallpaper: WallpaperEntity): Boolean {
        return downloadHDWallpaper(wallpaper, null)
    }

    fun downloadHDWallpaper(wallpaper: WallpaperEntity,
                            observer: DefaultObserver<Long>?): Boolean {
        observer?.onNext(0)
        LogUtil.D(TAG, "Start download HD wallpaper to " + wallpaper.storePath)
        val outputFile = File(wallpaper.storePath)
        if (outputFile.exists()) {
            if (WallpaperFileHelper.ensureChecksumValid(mContext,
                            wallpaper.checkSum, wallpaper.storePath)) {
                observer?.onComplete()
                return true
            }
        }
        synchronized(downloadLock) {
            var os: OutputStream? = null
            var _is: InputStream? = null
            try {
                if (outputFile.exists()) {
                    if (WallpaperFileHelper.ensureChecksumValid(mContext,
                                    wallpaper.checkSum, wallpaper.storePath)) {
                        observer?.onComplete()
                        return true
                    } else {
                        outputFile.delete()
                    }
                }

                val storePath = outputFile.parentFile
                storePath.mkdirs()
                os = FileOutputStream(outputFile)
                val httpClient = OkHttpClient.Builder()
                        .connectTimeout(SyncConfig.DEFAULT_CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                        .readTimeout(SyncConfig.DEFAULT_DOWNLOAD_TIMEOUT.toLong(), TimeUnit.SECONDS)
                        .build()
                val request = Request.Builder().url(URL(wallpaper.downloadUrl)).build()

                val response = httpClient.newCall(request).execute()
                val responseCode = response.code()
                if (responseCode in 200..299) {
                    _is = response.body().byteStream()
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    var writeLength = 0L
                    bytesRead = _is.read(buffer)
                    while (bytesRead > 0) {
                        os.write(buffer, 0, bytesRead)
                        writeLength += bytesRead
                        observer?.onNext(writeLength)
                        bytesRead = _is.read(buffer)
                    }
                    os.flush()
                    observer?.onComplete()
                    return true
                } else {
                    LogUtil.E(TAG, "Download wallpaper component " + wallpaper.name + " failed.")
                    observer?.onError(RemoteServerException())
                    return false

                }
            } catch (e: IOException) {
                e.printStackTrace()
                observer?.onError(NetworkConnectionException())
                LogUtil.E(TAG, "Download wallpaper component" + wallpaper.name + " failed.", e)
                return false
            } finally {
                ensureChecksum(outputFile, wallpaper.checkSum)
                try {
                    os?.close()
                    _is?.close()
                } catch (e: IOException) {
                    // ignore
                }
            }
        }
    }

    private fun ensureChecksum(file: File, checkSum: String) {
        if (file.exists()) {
            if (!WallpaperFileHelper.ensureChecksumValid(mContext,
                            checkSum, file.absolutePath)) {
                file.delete()
            }
        }
    }
}