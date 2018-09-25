package com.kinglloy.album

import android.service.wallpaper.WallpaperService
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract
import com.kinglloy.album.domain.interactor.DefaultObserver
import com.kinglloy.album.domain.interactor.SelectPreviewingWallpaper
import com.kinglloy.album.domain.interactor.SetActiveService
import com.kinglloy.album.domain.interactor.UnSelectedWallpaper
import com.kinglloy.album.engine.ProxyProvider
import com.kinglloy.album.engine.WallpaperActiveCallback

import javax.inject.Inject

/**
 * YaLin 2016/12/30.
 */
open class AlbumWallpaperService :
  WallpaperService(), WallpaperActiveCallback {
  @Inject lateinit var proxyProvider: ProxyProvider
  @Inject lateinit var selectedWallpaper: SelectPreviewingWallpaper
  @Inject lateinit var setActiveService: SetActiveService
  @Inject lateinit var unSelectWallpaper: UnSelectedWallpaper

  private var proxy: WallpaperService? = null

  init {
    AlbumApplication.instance.applicationComponent.inject(this)
  }

  override fun onCreateEngine(): WallpaperService.Engine = proxy!!.onCreateEngine()

  override fun onCreate() {
    super.onCreate()
    proxy = proxyProvider.provideProxy(this)
    proxy?.onCreate()
  }

  override fun onDestroy() {
    super.onDestroy()
    proxy?.onDestroy()
  }

  override fun onWallpaperActivate() {
    setActiveService.execute(
      object : DefaultObserver<Boolean>() {},
      SetActiveService.Params.setActiveService(getActiveState())
    )
    selectedWallpaper.execute(object : DefaultObserver<Boolean>() {}, null)
    Analytics.logEvent(this, Event.WALLPAPER_CREATED)
  }

  override fun onWallpaperDeactivate() {
    unSelectWallpaper.execute(object : DefaultObserver<Boolean>() {}, null)
    Analytics.logEvent(this, Event.WALLPAPER_DESTROYED)
  }

  open fun getActiveState(): Int = AlbumContract.ActiveService.SERVICE_ORIGIN
}
