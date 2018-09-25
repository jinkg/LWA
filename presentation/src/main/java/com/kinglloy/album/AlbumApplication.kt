package com.kinglloy.album

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
//import com.facebook.stetho.Stetho
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event.RXJAVA_ERROR
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.extensions.DelegatesExt
import com.kinglloy.album.injection.component.ApplicationComponent
import com.kinglloy.album.injection.component.DaggerApplicationComponent
import com.kinglloy.album.injection.modules.ApplicationModule
import io.reactivex.plugins.RxJavaPlugins

/**
 * @author jinyalin
 * @since 2017/9/26.
 */
class AlbumApplication : MultiDexApplication() {

  companion object {
    private val TAG = "StyleApplication"

    var instance: AlbumApplication by DelegatesExt.notNullSingleValue()
  }

  val applicationComponent: ApplicationComponent by lazy { initializeInjector() }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  override fun onCreate() {
    super.onCreate()
    instance = this

    applicationComponent.inject(this)

    resetExceptionHandler()

    Analytics.init(this)

    RxJavaPlugins.setErrorHandler {
      Analytics.logEvent(this, RXJAVA_ERROR)
    }

//        if (BuildConfig.DEMO_MODE) {
//            Stetho.initialize(
//                    Stetho.newInitializerBuilder(this)
//                            .enableDumpapp(
//                                    Stetho.defaultDumperPluginsProvider(this))
//                            .enableWebKitInspector(
//                                    Stetho.defaultInspectorModulesProvider(this))
//                            .build())
//        }
  }

  private fun initializeInjector() = DaggerApplicationComponent.builder()
    .applicationModule(ApplicationModule(this))
    .build()

  private fun resetExceptionHandler() {
    val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
      LogUtil.F(TAG, "exception", e)
      exceptionHandler.uncaughtException(t, e)
    }
  }
}