package com.kinglloy.album.engine.hd

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.v4.os.UserManagerCompat
import android.view.SurfaceHolder
import com.kinglloy.album.engine.hd.render.RenderController
import com.kinglloy.album.engine.hd.render.HDBlurRenderer
import com.yalin.style.engine.GLWallpaperServiceProxy

/**
 * @author jinyalin
 * @since 2018/03/20.
 */
class HDWallpaperProxy(host: Context, val wallpaperPath: String)
    : GLWallpaperServiceProxy(host) {

    private var mInitialized = false
    private var mUnlockReceiver: BroadcastReceiver? = null

    override fun onCreateEngine(): Engine = HDWallpaperEngine()

    override fun onCreate() {
        super.onCreate()

        if (UserManagerCompat.isUserUnlocked(this)) {
            initialize()
        } else if (Build.VERSION.SDK_INT >= 24) {
            mUnlockReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    initialize()
                    unregisterReceiver(this)
                }
            }
            val filter = IntentFilter(Intent.ACTION_USER_UNLOCKED)
            registerReceiver(mUnlockReceiver, filter)
        }
    }

    private fun initialize() {
        mInitialized = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mInitialized) {
            //todo
        } else {
            unregisterReceiver(mUnlockReceiver)
        }
    }

    inner class HDWallpaperEngine : GLActiveEngine(),
            HDBlurRenderer.Callbacks, RenderController.Callbacks {

        private var mRenderer: HDBlurRenderer? = null

        lateinit var mRenderController: RenderController

        private var mVisible = true

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            mRenderController = RenderController(this@HDWallpaperProxy, wallpaperPath)

            mRenderer = HDBlurRenderer(this@HDWallpaperProxy, this).apply {
                setIsPreview(isPreview)
            }

            setEGLContextClientVersion(2)
            setEGLConfigChooser(8, 8, 8, 0, 0, 0)
            setRenderer(mRenderer)
            renderMode = GLEngine.RENDERMODE_WHEN_DIRTY
            requestRender()

            mRenderController.setComponent(mRenderer!!, this)

            setTouchEventsEnabled(true)
            setOffsetNotificationsEnabled(true)

        }

        override fun onDestroy() {
            queueEvent {
                mRenderer?.destroy()
            }
            mRenderController.destroy()
            super.onDestroy()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            mRenderController.reloadCurrentWallpaper()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            mVisible = visible
            mRenderController.setVisible(visible)
        }

        override fun onOffsetsChanged(xOffset: Float, yOffset: Float, xOffsetStep: Float,
                                      yOffsetStep: Float, xPixelOffset: Int, yPixelOffset: Int) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep,
                    yOffsetStep, xPixelOffset, yPixelOffset)
            mRenderer?.setNormalOffsetX(xOffset)
        }

        override fun queueEventOnGlThread(runnable: Runnable) {
            queueEvent(runnable)
        }

        override fun requestRender() {
            if (mVisible) {
                super.requestRender()
            }
        }

    }
}