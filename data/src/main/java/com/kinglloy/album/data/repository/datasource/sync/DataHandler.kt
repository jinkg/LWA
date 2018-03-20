package com.kinglloy.album.data.repository.datasource.sync

import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.os.RemoteException
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import com.kinglloy.album.data.entity.mapper.WallpaperEntityMapper
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.io.*
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import java.io.IOException
import java.io.StringReader
import java.util.ArrayList
import java.util.HashMap

/**
 * @author jinyalin
 * @since 2017/11/6.
 */
class DataHandler(val context: Context) {
    companion object {
        private const val TAG = "DataHandler"
        private const val DATA_KEY_LIVE_WALLPAPER = "live_wallpapers"
        private const val DATA_KEY_STYLE_WALLPAPER = "style_wallpapers"
        private const val DATA_KEY_VIDEO_WALLPAPER = "video_wallpapers"
        private const val DATA_KEY_HD_WALLPAPER = "hd_wallpapers"

        private val DATA_KEYS_IN_ORDER = arrayOf(DATA_KEY_LIVE_WALLPAPER,
                DATA_KEY_STYLE_WALLPAPER, DATA_KEY_VIDEO_WALLPAPER)

    }

    private val liveWallpaperHandler = LiveWallpaperHandler(context)
    private val styleWallpaperHandler = StyleWallpaperHandler(context, WallpaperEntityMapper())
    private val videoWallpaperHandler = VideoWallpaperHandler(context, WallpaperEntityMapper())
    private val hdWallpaperHandler = HDWallpaperHandler(context, WallpaperEntityMapper())
    private val handlerForKey: HashMap<String, JSONHandler> = HashMap()

    init {
        handlerForKey[DATA_KEY_LIVE_WALLPAPER] = liveWallpaperHandler
        handlerForKey[DATA_KEY_STYLE_WALLPAPER] = styleWallpaperHandler
        handlerForKey[DATA_KEY_VIDEO_WALLPAPER] = videoWallpaperHandler
        handlerForKey[DATA_KEY_HD_WALLPAPER] = hdWallpaperHandler
    }

    fun applyData(dataBodies: Array<String>) {
        for (i in dataBodies.indices) {
            LogUtil.D(TAG, "Processing json object #" + (i + 1) + " of " + dataBodies.size)
            processDataBody(dataBodies[i])
        }
        val batch = ArrayList<ContentProviderOperation>()
        for (key in DATA_KEYS_IN_ORDER) {
            LogUtil.D(TAG, "Building content provider operations for: " + key)
            handlerForKey[key]!!.makeContentProviderOperations(batch)
            LogUtil.D(TAG, "Content provider operations so far: " + batch.size)
        }

        LogUtil.D(TAG, "Applying " + batch.size + " content provider operations.")
        try {
            val operations = batch.size
            if (operations > 0) {
                context.contentResolver.applyBatch(AlbumContract.AUTHORITY, batch)
            }
            LogUtil.D(TAG, "Successfully applied $operations content provider operations.")
        } catch (ex: RemoteException) {
            LogUtil.D(TAG, "RemoteException while applying content provider operations.")
            throw RuntimeException("Error executing content provider batch operation", ex)
        } catch (ex: OperationApplicationException) {
            LogUtil.D(TAG, "OperationApplicationException while applying content provider operations.")
            throw RuntimeException("Error executing content provider batch operation", ex)
        }


        LogUtil.D(TAG, "Notifying changes on all top-level paths on Content Resolver.")
        val resolver = context.contentResolver
        AlbumContract.TOP_LEVEL_PATHS
                .map { AlbumContract.BASE_CONTENT_URI.buildUpon().appendPath(it).build() }
                .forEach { resolver.notifyChange(it, null) }

        LogUtil.D(TAG, "Done applying conference data.")
    }

    @Throws(IOException::class)
    private fun processDataBody(dataBody: String) {
        val parser = JsonParser()
        JsonReader(StringReader(dataBody)).use { reader ->
            reader.isLenient = true // To err is human

            // the whole file is a single JSON object
            reader.beginObject()

            while (reader.hasNext()) {
                val key = reader.nextName()
                if (handlerForKey.containsKey(key)) {
                    LogUtil.D(TAG, "Processing key in conference data json: " + key)
                    handlerForKey[key]!!.process(parser.parse(reader))
                } else {
                    LogUtil.D(TAG, "Skipping unknown key in conference data json: " + key)
                    reader.skipValue()
                }
            }
            reader.endObject()
        }
    }
}