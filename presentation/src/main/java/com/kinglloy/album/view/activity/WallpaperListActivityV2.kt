package com.kinglloy.album.view.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Html
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.R
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import com.kinglloy.album.data.utils.STYLE_SETTINGS_MAX_BLUR
import com.kinglloy.album.data.utils.STYLE_SETTINGS_MAX_DIM
import com.kinglloy.album.data.utils.STYLE_SETTINGS_MAX_GREY
import com.kinglloy.album.model.StyleSettingsItem
import com.kinglloy.album.presenter.SettingsPresenter
import com.kinglloy.album.util.PackageUtil
import com.kinglloy.album.view.SettingsView
import com.kinglloy.album.view.component.MySecondarySwitchDrawerItem
import com.kinglloy.album.view.component.OnProgressChangedListener
import com.kinglloy.album.view.component.SecondarySeekDrawerItem
import com.kinglloy.album.view.fragment.*
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import kotlinx.android.synthetic.main.activity_wallpaper_list2.*
import java.util.*
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
class WallpaperListActivityV2 : AppCompatActivity(), SettingsView {

    companion object {
        val titleArray: IntArray = intArrayOf(
                R.string.live_type_title,
                R.string.hd_type_title,
                R.string.style_type_title,
                R.string.video_type_title
        )

        const val SP_NAME = "WallpaperListActivity"
        const val SELECT_INDEX_KEY = "select_index"

        const val ID_VIDEO_PROBLEM = 1000L
        const val ID_LIVE_PROBLEM = 1001L

        const val ID_SWITCH = 2000L
        const val ID_BLUR = 2001L
        const val ID_DIM = 2002L
        const val ID_GREY = 2003L

        const val ID_SEE_AD = 3000L
        const val ID_DONATE = 3001L

        const val ID_ABOUT = 4000L

        const val ID_MY_WALLPAPERS = 5000L

        const val ID_GET_PRO = 6000L

        const val ID_RATE = 7000L
    }

    @Inject
    internal lateinit var presenter: SettingsPresenter

    private lateinit var viewPager: ViewPager
    private lateinit var drawer: Drawer

    private var adRandom: Random? = null

    private val onCheckedChangeListener = OnCheckedChangeListener { _, _, isChecked ->
        presenter.enableStyleEffect(isChecked)
        styleSettingsSeekBars.forEach {
            it.withSeekEnable(isChecked)
            drawer.updateItem(it)
        }
    }

    private val onProgressChangedListener = OnProgressChangedListener { item, _, progress ->
        when (item.identifier) {
            ID_BLUR ->
                presenter.setStyleBlur(progress)
            ID_DIM ->
                presenter.setStyleDim(progress)
            ID_GREY ->
                presenter.setStylegrey(progress)
        }
    }

    private val styleSettingsSwitch = MySecondarySwitchDrawerItem().withName(R.string.drawer_item_apply_style_effect)
            .withLevel(2)
            .withChecked(true)
            .withSelectable(false)
            .withOnCheckedChangeListener(onCheckedChangeListener)


    private val drawerItemClick = Drawer.OnDrawerItemClickListener { _, _, drawerItem ->
        when (drawerItem.identifier) {
            ID_SEE_AD -> {
                Analytics.logEvent(this, Event.OPEN_AD_ACTIVITY)
                startActivity(Intent(this,
                        ADActivity::class.java))
            }
            ID_DONATE -> {
                val dialogBuilder = MaterialDialog.Builder(this)
                        .iconRes(R.drawable.advance_wallpaper_msg)
                        .title(R.string.hint)
                        .content(Html.fromHtml(getString(R.string.donate_hint)))
                        .positiveText(R.string.confirm)

                dialogBuilder.build().show()
            }
            ID_RATE -> {
                val appPackageName = "com.kinglloy.album"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            ID_VIDEO_PROBLEM -> {
                val dialogBuilder = MaterialDialog.Builder(this)
                        .iconRes(R.drawable.advance_wallpaper_msg)
                        .title(R.string.hint)
                        .content(Html.fromHtml(getString(R.string.video_hint)))
                        .positiveText(R.string.confirm)

                dialogBuilder.build().show()
            }
            ID_LIVE_PROBLEM -> {
                val dialogBuilder = MaterialDialog.Builder(this)
                        .iconRes(R.drawable.advance_wallpaper_msg)
                        .title(R.string.hint)
                        .content(Html.fromHtml(getString(R.string.advance_hint)))
                        .positiveText(R.string.confirm)

                dialogBuilder.build().show()
            }
            ID_ABOUT -> {
                startActivity(Intent(this,
                        AboutActivity::class.java))
            }
            ID_MY_WALLPAPERS -> {
                startActivity(Intent(this,
                        MyWallpapersActivity::class.java))
            }
            ID_GET_PRO -> {
                val appPackageName = "com.kinglloy.album.ultimate"
                try {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }

            }
        }
        return@OnDrawerItemClickListener true
    }
    private val styleSettingsSeekBars =
            arrayOf(
                    SecondarySeekDrawerItem().withName(R.string.drawer_item_style_blur)
                            .withLevel(3)
                            .withMax(STYLE_SETTINGS_MAX_BLUR)
                            .withSelectable(false)
                            .withOnSeekBarChangeListener(onProgressChangedListener),
                    SecondarySeekDrawerItem().withName(R.string.drawer_item_style_dim)
                            .withLevel(3)
                            .withMax(STYLE_SETTINGS_MAX_DIM)
                            .withSelectable(false)
                            .withOnSeekBarChangeListener(onProgressChangedListener),
                    SecondarySeekDrawerItem().withName(R.string.drawer_item_style_grey)
                            .withLevel(3)
                            .withMax(STYLE_SETTINGS_MAX_GREY)
                            .withSelectable(false)
                            .withOnSeekBarChangeListener(onProgressChangedListener))

    private val liveWallpaperDrawer = ExpandableBadgeDrawerItem()
            .withName(R.string.drawer_item_live_wallpaper)
            .withIcon(R.drawable.ic_drawer_live).withIdentifier(1)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withSelectable(false).withSubItems(
            SecondaryDrawerItem().withName(R.string.drawer_item_need_help)
                    .withDescription(R.string.drawer_item_need_help_dsc)
                    .withSelectable(false)
                    .withIdentifier(ID_LIVE_PROBLEM)
                    .withLevel(2)
                    .withOnDrawerItemClickListener(drawerItemClick))

    private val styleWallpaperDrawer = ExpandableBadgeDrawerItem()
            .withName(R.string.drawer_item_style_wallpaper_settings)
            .withIcon(R.drawable.ic_drawer_style).withIdentifier(2)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withSelectable(false).withSubItems(
            styleSettingsSwitch.withIdentifier(ID_SWITCH),
            styleSettingsSeekBars[0].withIdentifier(ID_BLUR),
            styleSettingsSeekBars[1].withIdentifier(ID_DIM),
            styleSettingsSeekBars[2].withIdentifier(ID_GREY))

    private val makeMeBetterAD = ExpandableBadgeDrawerItem().withName(R.string.drawer_item_make_me_better)
            .withIcon(R.drawable.ic_drawer_money).withIdentifier(3)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withSelectable(false).withSubItems(
            SecondaryDrawerItem().withName(R.string.drawer_item_see_ad)
                    .withDescription(R.string.drawer_item_see_ad_dsc)
                    .withSelectable(false)
                    .withIdentifier(ID_SEE_AD)
                    .withLevel(2)
                    .withOnDrawerItemClickListener(drawerItemClick),
            SecondaryDrawerItem().withName(R.string.drawer_item_donate)
                    .withDescription(R.string.drawer_item_donate_dsc)
                    .withSelectable(false)
                    .withIdentifier(ID_DONATE)
                    .withLevel(2)
                    .withOnDrawerItemClickListener(drawerItemClick),
            SecondaryDrawerItem().withName(R.string.drawer_item_rate)
                    .withDescription(R.string.drawer_item_rate_dsc)
                    .withSelectable(false)
                    .withIdentifier(ID_RATE)
                    .withLevel(2)
                    .withOnDrawerItemClickListener(drawerItemClick))

    private val makeMeBetter = ExpandableBadgeDrawerItem().withName(R.string.drawer_item_make_me_better)
            .withIcon(R.drawable.ic_drawer_money).withIdentifier(3)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withSelectable(false).withSubItems(
            SecondaryDrawerItem().withName(R.string.drawer_item_see_ad)
                    .withDescription(R.string.drawer_item_see_ad_dsc)
                    .withSelectable(false)
                    .withIdentifier(ID_SEE_AD)
                    .withLevel(2)
                    .withOnDrawerItemClickListener(drawerItemClick),
            SecondaryDrawerItem().withName(R.string.drawer_item_rate)
                    .withDescription(R.string.drawer_item_rate_dsc)
                    .withSelectable(false)
                    .withIdentifier(ID_RATE)
                    .withLevel(2)
                    .withOnDrawerItemClickListener(drawerItemClick))

    private val getPro = PrimaryDrawerItem().withName(R.string.drawer_item_remove_ad)
            .withDescription(R.string.drawer_item_remove_ad_dsc)
            .withIcon(R.drawable.ic_drawer_ad)
            .withIdentifier(ID_GET_PRO)
            .withSelectable(false)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withOnDrawerItemClickListener(drawerItemClick)

    private val myWallpapers = PrimaryDrawerItem().withName(R.string.drawer_item_my_wallpapers)
            .withDescription(R.string.drawer_item_my_wallpapers_dsc)
            .withIcon(R.drawable.ic_drawer_my_wallpapers)
            .withIdentifier(ID_MY_WALLPAPERS)
            .withSelectable(false)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withOnDrawerItemClickListener(drawerItemClick)

    private val rate = PrimaryDrawerItem().withName(R.string.drawer_item_rate)
            .withDescription(R.string.drawer_item_rate_dsc)
            .withIcon(R.drawable.ic_drawer_rate)
            .withIdentifier(ID_RATE)
            .withSelectable(false)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withOnDrawerItemClickListener(drawerItemClick)

    private val aboutDrawer = PrimaryDrawerItem().withName(R.string.drawer_item_about)
            .withDescription(R.string.drawer_item_about_dsc)
            .withIcon(R.drawable.ic_drawer_about)
            .withIdentifier(ID_ABOUT)
            .withSelectable(false)
            .withIconTintingEnabled(true)
            .withIconColorRes(R.color.colorPrimary)
            .withOnDrawerItemClickListener(drawerItemClick)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallpaper_list2)
        AlbumApplication.instance.applicationComponent.inject(this)

        presenter.setView(this)

        if (PackageUtil.isUltimate(this)) {
            initDrawerUltimate(savedInstanceState)
        } else {
            MobileAds.initialize(this, getString(R.string.app_ad_id))
            initDrawerWithAd(savedInstanceState)
            initAdBanner()
        }

        val tabLayout = findViewById<TabLayout>(R.id.wallpaper_types_tab)
        viewPager = findViewById(R.id.wallpaper_types)

        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = WallpaperTypesAdapter(supportFragmentManager)
        if (savedInstanceState == null) {
            viewPager.currentItem = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
                    .getInt(SELECT_INDEX_KEY, 0)
        } else {
            viewPager.currentItem = savedInstanceState.getInt(SELECT_INDEX_KEY, 0)
        }

        presenter.initialize()

        maybeOpenAdActivity()
    }

    override fun onDestroy() {
        adView.pause()
        super.onDestroy()
        getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit()
                .putInt(SELECT_INDEX_KEY, viewPager.currentItem).apply()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle?) {
        val newOutState = drawer.saveInstanceState(outState)
        newOutState.putInt(SELECT_INDEX_KEY, viewPager.currentItem)
        super.onSaveInstanceState(newOutState, outPersistentState)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    private fun maybeOpenAdActivity() {
        val ultimate = PackageUtil.isUltimate(this)
        if (ultimate) {
            return
        }
        if (adRandom == null) {
            adRandom = Random()
        }
        val num = adRandom!!.nextInt(10) + 1
        if (num % 2 == 0) {
            startActivity(Intent(this,
                    ADActivity::class.java))
        }
    }

    private fun initDrawerWithAd(savedInstanceState: Bundle?) {
        val toolBar = findViewById<Toolbar>(R.id.appBar)
        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolBar)
                .withHasStableIds(true)
                .withHeader(R.layout.layout_drawer_header)
                .withSelectedItem(-1)
                .addDrawerItems(
                        liveWallpaperDrawer,
                        styleWallpaperDrawer,
                        ExpandableBadgeDrawerItem().withName(R.string.drawer_item_video_wallpaper)
                                .withIcon(R.drawable.ic_drawer_video).withIdentifier(1)
                                .withIconTintingEnabled(true)
                                .withIconColorRes(R.color.colorPrimary)
                                .withSelectable(false).withSubItems(
                                SecondaryDrawerItem().withName(R.string.drawer_item_video_need_help)
                                        .withDescription(R.string.drawer_item_video_need_help_dsc)
                                        .withSelectable(false)
                                        .withIdentifier(ID_VIDEO_PROBLEM)
                                        .withLevel(2)
                                        .withOnDrawerItemClickListener(drawerItemClick)
                        ),
                        DividerDrawerItem(),
                        makeMeBetter,
                        getPro,
                        DividerDrawerItem(),
                        myWallpapers,
                        DividerDrawerItem(),
                        rate,
                        DividerDrawerItem(),
                        aboutDrawer)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build()


    }

    private fun initAdBanner() {
        adGroup.visibility = View.VISIBLE
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun initDrawerUltimate(savedInstanceState: Bundle?) {
        val toolBar = findViewById<Toolbar>(R.id.appBar)
        drawer = DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolBar)
                .withHasStableIds(true)
                .withHeader(R.layout.layout_drawer_header)
                .withSelectedItem(-1)
                .addDrawerItems(
                        liveWallpaperDrawer,
                        styleWallpaperDrawer,
                        DividerDrawerItem(),
                        makeMeBetter,
                        DividerDrawerItem(),
                        myWallpapers,
                        DividerDrawerItem(),
                        rate,
                        DividerDrawerItem(),
                        aboutDrawer)
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build()
    }

    override fun renderStyleSettings(styleSettingsItem: StyleSettingsItem) {
        styleSettingsSwitch.withChecked(styleSettingsItem.enableEffect)
        drawer.updateItem(styleSettingsSwitch)
        styleSettingsSeekBars[0].withProgress(styleSettingsItem.blur)
                .withSeekEnable(styleSettingsItem.enableEffect)
        styleSettingsSeekBars[1].withProgress(styleSettingsItem.dim)
                .withSeekEnable(styleSettingsItem.enableEffect)
        styleSettingsSeekBars[2].withProgress(styleSettingsItem.grey)
                .withSeekEnable(styleSettingsItem.enableEffect)
        styleSettingsSeekBars.forEach {
            drawer.updateItem(it)
        }
    }

    private inner class WallpaperTypesAdapter(fragmentManager: FragmentManager)
        : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> LiveWallpapersFragment()
            1 -> HDWallpapersFragment()
            2 -> StyleWallpapersFragment()
            else -> VideoWallpapersFragment()
        }

        override fun getCount() = titleArray.size

        override fun getPageTitle(position: Int): CharSequence = getString(titleArray[position])
    }
}