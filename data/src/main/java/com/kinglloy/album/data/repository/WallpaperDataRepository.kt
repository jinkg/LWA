package com.kinglloy.album.data.repository

import android.content.Context
import com.kinglloy.album.data.entity.mapper.WallpaperEntityMapper
import com.kinglloy.album.data.repository.datasource.WallpaperDataStoreFactory
import com.kinglloy.album.data.repository.datasource.sync.SyncHelper
import com.kinglloy.album.data.repository.datasource.sync.account.Account
import com.kinglloy.album.domain.Wallpaper
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.domain.repository.WallpaperRepository
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author jinyalin
 * @since 2017/7/27.
 */
@Singleton
class WallpaperDataRepository
@Inject constructor(val context: Context,
                    val factory: WallpaperDataStoreFactory,
                    private val wallpaperMapper: WallpaperEntityMapper)
    : WallpaperRepository {

    init {
        Account.createSyncAccount(context)
        SyncHelper.updateSyncInterval(context)
    }

    override fun getLiveWallpapers(): Observable<List<Wallpaper>> =
            factory.createLiveDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)

    override fun getStyleWallpapers(): Observable<MutableList<Wallpaper>> =
            factory.createStyleDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)

    override fun getVideoWallpapers(): Observable<MutableList<Wallpaper>> =
            factory.createVideoDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)

    override fun getHDWallpapers(): Observable<MutableList<Wallpaper>> =
            factory.createHDDataStore().getWallpaperEntities().map(wallpaperMapper::transformList)


    override fun loadLiveWallpapers(): Observable<List<Wallpaper>> {
        return factory.createRemoteLiveDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun loadStyleWallpapers(): Observable<MutableList<Wallpaper>> {
        return factory.createRemoteStyleDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun loadVideoWallpapers(): Observable<MutableList<Wallpaper>> {
        return factory.createRemoteVideoDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun loadHDWallpapers(): Observable<MutableList<Wallpaper>> {
        return factory.createRemoteHDDataStore().getWallpaperEntities()
                .map(wallpaperMapper::transformList)
    }

    override fun getDownloadedWallpapers(): Observable<MutableList<Wallpaper>> =
            factory.createManageDataStore().getDownloadedWallpaperEntities()
                    .map(wallpaperMapper::transformList)

    override fun deleteDownloadedWallpapers(wallpapers: MutableList<Wallpaper>) =
            factory.createManageDataStore().deleteDownloadedWallpapers(wallpapers.map(wallpaperMapper::transformToEntity))

    override fun previewWallpaper(wallpaperId: String, type: WallpaperType): Observable<Boolean> =
            factory.createManageDataStore().previewWallpaper(wallpaperId, type)

    override fun selectPreviewingWallpaper():
            Observable<Boolean> = factory.createManageDataStore().selectPreviewingWallpaper()

    override fun getPreviewingWallpaper(): Wallpaper =
            wallpaperMapper.transform(factory.createManageDataStore().getPreviewWallpaperEntity())

    override fun activeService(serviceType: Int): Observable<Boolean>
            = factory.createManageDataStore().activeService(serviceType)

    override fun getActiveService(): Observable<Int> = factory.createManageDataStore().getActiveService()
}