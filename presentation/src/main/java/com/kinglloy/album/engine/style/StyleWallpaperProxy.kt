package com.kinglloy.album.engine.style

import android.app.KeyguardManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.os.UserManagerCompat
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.ViewConfiguration
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.repository.datasource.provider.settings.SettingsContract
import com.kinglloy.album.domain.StyleWallpaperSettings
import com.kinglloy.album.domain.interactor.DefaultObserver
import com.kinglloy.album.domain.interactor.settings.GetStyleWallpaperSettings
import com.kinglloy.album.engine.style.render.RenderController
import com.kinglloy.album.engine.style.render.StyleBlurRenderer
import com.kinglloy.album.presenter.MainWallpaperListPresenter
import com.yalin.style.engine.GLWallpaperServiceProxy

/**
 * @author jinyalin
 * @since 2017/11/1.
 */
class StyleWallpaperProxy(host: Context, val wallpaperPath: String,
                          private val getStyleWallpaperSettings: GetStyleWallpaperSettings)
    : GLWallpaperServiceProxy(host) {

    companion object {
        private const val TEMPORARY_FOCUS_DURATION_MILLIS: Long = 3000
    }

    private var mInitialized = false
    private var mUnlockReceiver: BroadcastReceiver? = null

    var mStyleWallpaperSettings: StyleWallpaperSettings? = null

    override fun onCreateEngine(): Engine = StyleWallpaperEngine()

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

    inner class StyleWallpaperEngine : GLActiveEngine(),
            StyleBlurRenderer.Callbacks, RenderController.Callbacks {

        private var mRenderer: StyleBlurRenderer? = null

        lateinit var mRenderController: RenderController

        private val mGestureDetector: GestureDetectorCompat =
                GestureDetectorCompat(host,
                        object : GestureDetector.SimpleOnGestureListener() {
                            override fun onDown(e: MotionEvent): Boolean = true

                            override fun onDoubleTap(e: MotionEvent): Boolean {
                                if (mWallpaperDetailMode) {
                                    return true
                                }

                                mValidDoubleTap = true
                                mMainThreadHandler.removeCallbacks(mDoubleTapTimeoutRunnable)
                                val timeout = ViewConfiguration.getTapTimeout()
                                mMainThreadHandler.postDelayed(mDoubleTapTimeoutRunnable,
                                        timeout.toLong())
                                return true
                            }
                        })

        private val mMainThreadHandler = Handler()

        private var mVisible = true

        // is MainActivity visible
        private var mWallpaperDetailMode = false

        // is last double tab valid
        private var mValidDoubleTap = false

        private var mIsLockScreenVisibleReceiverRegistered = false

        private val mLockScreenVisibleReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent != null) {
                    if (Intent.ACTION_USER_PRESENT == intent.action) {
                        lockScreenVisibleChanged(false)
                    } else if (Intent.ACTION_SCREEN_OFF == intent.action) {
                        lockScreenVisibleChanged(true)
                    } else if (Intent.ACTION_SCREEN_ON == intent.action) {
                        val kgm = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                        if (!kgm.inKeyguardRestrictedInputMode()) {
                            lockScreenVisibleChanged(false)
                        }
                    }
                }
            }
        }

        private fun getSettings() {
            getStyleWallpaperSettings.execute(object : DefaultObserver<StyleWallpaperSettings>() {
                override fun onNext(styleSettings: StyleWallpaperSettings) {
                    mStyleWallpaperSettings = styleSettings
                    mRenderController.onSettingsChanged()
                }
            }, null)
        }

        private val mContentObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean, uri: Uri) {
                getSettings()
            }
        }


        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)

            mRenderController = RenderController(this@StyleWallpaperProxy, wallpaperPath)

            mRenderer = StyleBlurRenderer(this@StyleWallpaperProxy, this).apply {
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

            getSettings()

            contentResolver.registerContentObserver(
                    SettingsContract.StyleWallpaperSettings.CONTENT_URI,
                    true, mContentObserver)

        }

        override fun onDestroy() {
            if (mIsLockScreenVisibleReceiverRegistered) {
                host.unregisterReceiver(mLockScreenVisibleReceiver)
            }
            queueEvent {
                mRenderer?.destroy()
            }
            mRenderController.destroy()
            contentResolver.unregisterContentObserver(mContentObserver)
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

        override fun onTouchEvent(event: MotionEvent) {
            super.onTouchEvent(event)
            mGestureDetector.onTouchEvent(event)
            delayBlur()
        }

        override fun onCommand(action: String, x: Int, y: Int, z: Int, extras: Bundle?,
                               resultRequested: Boolean): Bundle? {
            if (WallpaperManager.COMMAND_TAP == action && mValidDoubleTap) {
                queueEvent {
                    mRenderer?.setIsBlurred(!mRenderer!!.isBlurred, false)
                    delayBlur()
                }
                mValidDoubleTap = false
            }
            return super.onCommand(action, x, y, z, extras, resultRequested)
        }

        override fun queueEventOnGlThread(runnable: Runnable) {
            queueEvent(runnable)
        }

        override fun requestRender() {
            if (mVisible) {
                super.requestRender()
            }
        }

        private fun lockScreenVisibleChanged(isLockScreenVisible: Boolean) {
            cancelDelayedBlur()
            queueEvent { mRenderer?.setIsBlurred(!isLockScreenVisible, false) }
        }

        private fun cancelDelayedBlur() {
            mMainThreadHandler.removeCallbacks(mBlurRunnable)
        }

        private fun delayBlur() {
            if (mWallpaperDetailMode || mRenderer!!.isBlurred) {
                return
            }
            cancelDelayedBlur()
            mMainThreadHandler.postDelayed(mBlurRunnable, TEMPORARY_FOCUS_DURATION_MILLIS)
        }

        private val mBlurRunnable = Runnable {
            queueEvent {
                mRenderer?.setIsBlurred(true, false)
            }
        }

        private val mDoubleTapTimeoutRunnable = Runnable { mValidDoubleTap = false }
    }
}