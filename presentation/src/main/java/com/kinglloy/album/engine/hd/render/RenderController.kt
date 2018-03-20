package com.kinglloy.album.engine.hd.render

import android.content.Context
import android.os.Handler
import android.os.Message
import java.io.FileInputStream
import java.io.InputStream
import javax.inject.Inject

/**
 * YaLin 2016/12/30.
 */

open class RenderController @Inject
constructor(private var mContext: Context, private val wallpaperPath: String) {
    private var mRenderer: HDBlurRenderer? = null
    private var mCallbacks: Callbacks? = null
    private var mVisible: Boolean = false
    private var mQueuedBitmapRegionLoader: BitmapRegionLoader? = null

    fun onSettingsChanged() {
        mRenderer?.apply {
            recomputeMaxPrescaledBlurPixels()
            recomputeMaxDimAmount()
            recomputeGreyAmount()
            throttledForceReloadCurrentArtwork()
        }
    }

    fun setComponent(renderer: HDBlurRenderer, callbacks: Callbacks) {
        this.mRenderer = renderer
        this.mCallbacks = callbacks
        reloadCurrentWallpaper()
    }

    open fun destroy() {
        if (mQueuedBitmapRegionLoader != null) {
            mQueuedBitmapRegionLoader!!.destroy()
        }
    }

    @Throws(Exception::class)
    private fun createBitmapRegionLoader(inputStream: InputStream): BitmapRegionLoader {
        val bitmapRegionLoader = BitmapRegionLoader.newInstance(inputStream) ?:
                throw IllegalStateException("Bitmap region loader create failed.")
        return bitmapRegionLoader
    }

    fun reloadCurrentWallpaper() {
        setBitmapRegionLoader(createBitmapRegionLoader(FileInputStream(wallpaperPath)))
    }

    fun setVisible(visible: Boolean) {
        mVisible = visible
        if (visible) {
            mCallbacks?.apply {
                queueEventOnGlThread(Runnable {
                    if (mQueuedBitmapRegionLoader != null) {
                        mRenderer!!.setAndConsumeBitmapRegionLoader(mQueuedBitmapRegionLoader)
                        mQueuedBitmapRegionLoader = null
                    }
                })
                requestRender()
            }
        }
    }

    private fun setBitmapRegionLoader(bitmapRegionLoader: BitmapRegionLoader) {
        mCallbacks!!.queueEventOnGlThread(Runnable {
            if (mVisible) {
                mRenderer!!.setAndConsumeBitmapRegionLoader(bitmapRegionLoader)
            } else {
                mQueuedBitmapRegionLoader = bitmapRegionLoader
            }
        })
    }

    private fun throttledForceReloadCurrentArtwork() {
        mThrottledForceReloadHandler.removeMessages(0)
        mThrottledForceReloadHandler.sendEmptyMessageDelayed(0, 250)
    }

    private val mThrottledForceReloadHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            reloadCurrentWallpaper()
        }
    }

    interface Callbacks {

        fun queueEventOnGlThread(runnable: Runnable)

        fun requestRender()
    }

    companion object {
        private val TAG = "RenderController"
    }
}
