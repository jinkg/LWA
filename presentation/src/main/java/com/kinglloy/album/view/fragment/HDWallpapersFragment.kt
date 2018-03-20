package com.kinglloy.album.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.R
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.view.activity.WallpaperListActivity

/**
 * @author jinyalin
 * @since 2018/03/20.
 */
class HDWallpapersFragment : BaseWallpapersFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_hd_wallpapers_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        AlbumApplication.instance.applicationComponent.inject(this)
        super.onViewCreated(view, savedInstanceState)

        presenter.setView(this)

        if (savedInstanceState != null) {
            loadState = savedInstanceState.getInt(WallpaperListActivity.LOAD_STATE)
            presenter.onRestoreInstanceState(savedInstanceState)
        }

        handleState()
    }

    override fun getWallpaperType() = WallpaperType.HD
}