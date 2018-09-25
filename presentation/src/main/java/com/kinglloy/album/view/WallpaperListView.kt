package com.kinglloy.album.view

import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.model.WallpaperItem

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
interface WallpaperListView : LoadingDataView {
  fun renderWallpapers(wallpapers: List<WallpaperItem>)

  fun selectWallpaper(wallpaper: WallpaperItem)

  fun unSelectWallpaper(wallpaper: WallpaperItem)

  fun showEmpty()

  fun complete()

  fun wallpaperSelected(wallpaperId: String)

  fun showDownloadHintDialog(item: WallpaperItem)

  fun showDownloadingDialog(item: WallpaperItem)

  fun updateDownloadingProgress(downloaded: Long)

  fun downloadComplete(item: WallpaperItem)

  fun showDownloadError(item: WallpaperItem, e: Exception)

  fun getWallpaperType(): WallpaperType

  fun deletedDownloadWallpaper(wallpaperId: String)
}