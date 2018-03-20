package com.kinglloy.album.injection.component

import android.content.Context
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.AlbumWallpaperService
import com.kinglloy.album.view.activity.WallpaperListActivity
import com.kinglloy.album.injection.modules.ApplicationModule
import com.kinglloy.album.view.activity.MyWallpapersActivity
import com.kinglloy.album.view.activity.WallpaperListActivityV2
import com.kinglloy.album.view.fragment.*

import javax.inject.Singleton

import dagger.Component

/**
 * @author jinyalin
 * *
 * @since 2017/4/18.
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun inject(application: AlbumApplication)
    fun inject(albumWallpaperService: AlbumWallpaperService)

    fun inject(videoWallpapersFragment: VideoWallpapersFragment)
    fun inject(liveWallpapersFragment: LiveWallpapersFragment)
    fun inject(styleWallpapersFragment: StyleWallpapersFragment)
    fun inject(hdWallpapersFragment: HDWallpapersFragment)

    fun inject(mainActivity: WallpaperListActivity)
    fun inject(mainActivity: WallpaperListActivityV2)
    fun inject(myWallpapersActivity: MyWallpapersActivity)

    //Exposed to sub-graphs.
    fun context(): Context
}
