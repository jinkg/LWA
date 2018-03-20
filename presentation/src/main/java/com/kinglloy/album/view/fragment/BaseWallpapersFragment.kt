package com.kinglloy.album.view.fragment

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.kinglloy.album.R
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.utils.WallpaperFileHelper
import com.kinglloy.album.exception.ErrorMessageFactory
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.presenter.WallpaperListPresenter
import com.kinglloy.album.util.formatSizeToString
import com.kinglloy.album.view.WallpaperListView
import com.kinglloy.album.view.activity.ADActivity
import com.kinglloy.album.view.activity.WallpaperListActivity
import com.kinglloy.album.view.component.DownloadingDialog
import kotlinx.android.synthetic.main.activity_wallpaper_list.*
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author jinyalin
 * @since 2017/10/31.
 */
abstract class BaseWallpapersFragment : Fragment(), WallpaperListView {
    companion object {
        const val TAG = "BaseWallpapersFragment"
        const val LOAD_STATE = "load_state"

        const val LOAD_STATE_NORMAL = 0
        const val LOAD_STATE_LOADING = 1
        const val LOAD_STATE_RETRY = 2
    }

    private lateinit var wallpaperList: RecyclerView
    private lateinit var loadingView: View
    private lateinit var emptyView: View
    private lateinit var failedView: View

    private lateinit var proBackground: Drawable
    private lateinit var normalBackground: Drawable

    @Inject
    internal lateinit var presenter: WallpaperListPresenter

    val wallpapers = ArrayList<WallpaperItem>()

    protected var loadState = WallpaperListActivity.LOAD_STATE_NORMAL

    private var placeHolderDrawable: ColorDrawable? = null
    private var mItemSize = 10

    private var downloadDialog: DownloadingDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wallpaperList = view.findViewById(R.id.wallpaperList)
        loadingView = view.findViewById(R.id.loading)
        emptyView = view.findViewById(android.R.id.empty)
        failedView = view.findViewById(R.id.retry)

        proBackground = ContextCompat.getDrawable(activity!!, R.drawable.pro_wallpaper_gradient)!!
        normalBackground = ColorDrawable(ContextCompat.getColor(activity!!, R.color.color_name_bg))

        initViews()
    }

    protected fun handleState() {
        when (loadState) {
            WallpaperListActivity.LOAD_STATE_NORMAL -> presenter.initialize(getWallpaperType())
            WallpaperListActivity.LOAD_STATE_LOADING ->
                presenter.loadWallpapers(getWallpaperType())
            WallpaperListActivity.LOAD_STATE_RETRY -> showRetry()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(LOAD_STATE, loadState)
        presenter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        presenter.resume()
    }

    override fun onPause() {
        super.onPause()
        presenter.pause()
    }

    override fun onDestroy() {
        presenter.destroy()
        super.onDestroy()
    }

    private fun initViews() {
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.supportsChangeAnimations = false
        wallpaperList.itemAnimator = itemAnimator

        val gridLayoutManager = GridLayoutManager(activity, 1)
        wallpaperList.layoutManager = gridLayoutManager

        btnLoadAdvanceWallpaper.setOnClickListener {
            presenter.loadWallpapers(getWallpaperType())
            Analytics.logEvent(activity!!, Event.LOAD_ADVANCES)
        }
        btnRetry.setOnClickListener {
            presenter.loadWallpapers(getWallpaperType())
            Analytics.logEvent(activity!!, Event.RETRY_LOAD_ADVANCES)
        }

        wallpaperList.viewTreeObserver
                .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val width = wallpaperList.width -
                                wallpaperList.paddingStart - wallpaperList.paddingEnd
                        if (width <= 0) {
                            return
                        }

                        // Compute number of columns
                        val maxItemWidth = resources.getDimensionPixelSize(
                                R.dimen.advance_grid_max_item_size)
                        var numColumns = 1
                        while (true) {
                            if (width / numColumns > maxItemWidth) {
                                ++numColumns
                            } else {
                                break
                            }
                        }

                        val spacing = resources.getDimensionPixelSize(
                                R.dimen.gallery_chosen_photo_grid_spacing)
                        mItemSize = (width - spacing * (numColumns - 1)) / numColumns

                        // Complete setup
                        gridLayoutManager.spanCount = numColumns
                        wallpaperList.adapter = wallpapersAdapter

                        wallpaperList.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })

        ViewCompat.setOnApplyWindowInsetsListener(wallpaperList) { v, insets ->
            val gridSpacing = resources
                    .getDimensionPixelSize(R.dimen.gallery_chosen_photo_grid_spacing)
            ViewCompat.onApplyWindowInsets(v, insets.replaceSystemWindowInsets(
                    insets.systemWindowInsetLeft + gridSpacing,
                    gridSpacing,
                    insets.systemWindowInsetRight + gridSpacing,
                    insets.systemWindowInsetBottom + insets.systemWindowInsetTop + gridSpacing))

            insets
        }

        downloadDialog = DownloadingDialog(activity!!, MaterialDialog.SingleButtonCallback { _, _ ->
            presenter.cancelCurrentDownload()
        })
    }

    override fun renderWallpapers(wallpapers: List<WallpaperItem>) {
        loadState = LOAD_STATE_NORMAL

        this.wallpapers.clear()
        this.wallpapers.addAll(wallpapers)

        if (wallpaperList.visibility == View.VISIBLE) {
            return
        }
        wallpaperList.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.GONE
        failedView.visibility = View.GONE
        wallpapersAdapter.notifyDataSetChanged()
    }

    override fun selectWallpaper(wallpaper: WallpaperItem) {
        var oldSelectedIndex = -1
        var newSelectedIndex = -1
        for ((index, value) in wallpapers.withIndex()) {
            if (value.isSelected) {
                value.isSelected = false
                oldSelectedIndex = index
            }
            if (value.wallpaperType == wallpaper.wallpaperType
                    && TextUtils.equals(value.wallpaperId, wallpaper.wallpaperId)) {
                value.isSelected = true
                newSelectedIndex = index
            }
        }

        if (oldSelectedIndex == newSelectedIndex) {
            return
        }
        if (oldSelectedIndex >= 0 && newSelectedIndex >= 0) {
            wallpapersAdapter.notifyItemChanged(oldSelectedIndex)
            wallpapersAdapter.notifyItemChanged(newSelectedIndex)
        } else if (oldSelectedIndex >= 0) {
            wallpapersAdapter.notifyItemChanged(oldSelectedIndex)
        } else {
            wallpapersAdapter.notifyItemChanged(newSelectedIndex)
        }
    }

    override fun showLoading() {
        loadState = LOAD_STATE_LOADING

        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
        failedView.visibility = View.GONE
    }

    override fun hideLoading() {

    }

    override fun showRetry() {
        loadState = LOAD_STATE_RETRY

        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.GONE
        failedView.visibility = View.VISIBLE
    }

    override fun hideRetry() {

    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showEmpty() {
        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
        failedView.visibility = View.GONE
    }

    override fun context(): Context = activity!!.applicationContext

    override fun complete() {
        activity!!.finish()
    }

    override fun wallpaperSelected(wallpaperId: String) {
        wallpapers.forEach { it ->
            it.isSelected = TextUtils.equals(it.wallpaperId, wallpaperId)
        }
        wallpapersAdapter.notifyDataSetChanged()
    }

    override fun showDownloadHintDialog(item: WallpaperItem) {
        val showAd = showAd()
        val downloadCallback =
                MaterialDialog.SingleButtonCallback { _, _ ->
                    presenter.requestDownload(item)
                    Analytics.logEvent(activity!!,
                            Event.DOWNLOAD_COMPONENT, item.name)
                    if (showAd) {
                        Analytics.logEvent(activity!!, Event.OPEN_AD_ACTIVITY)
                        startActivity(Intent(activity,
                                ADActivity::class.java))
                    }
                }
        val content =
                if (item.size > 0) {
                    if (showAd) {
                        Html.fromHtml(getString(R.string.advance_download_size_hint_ad,
                                formatSizeToString(item.size)))
                    } else {
                        Html.fromHtml(getString(R.string.advance_download_size_hint,
                                formatSizeToString(item.size)))
                    }
                } else {
                    if (showAd) {
                        Html.fromHtml(getString(R.string.advance_download_hint))
                    } else {
                        Html.fromHtml(getString(R.string.advance_download_hint_ad))
                    }
                }

        val dialogBuilder = MaterialDialog.Builder(activity!!)
                .iconRes(R.drawable.advance_wallpaper_msg)
                .title(R.string.hint)
                .content(content)
                .positiveText(R.string.advance_download_msg)
                .onPositive(downloadCallback)

        dialogBuilder.build().show()
    }

    private fun showAd(): Boolean {
//        return !PackageUtil.isUltimate(activity!!)
        return false
    }


    override fun showDownloadingDialog(item: WallpaperItem) {
        LogUtil.D(TAG, "showDownloadingDialog ${item.name}")
        downloadDialog!!.setTotalSize(item.size)
        downloadDialog!!.show()
    }

    override fun updateDownloadingProgress(downloaded: Long) {
        LogUtil.D(TAG, "updateDownloadingProgress $downloaded")
        downloadDialog!!.updateProgress(downloaded)
    }

    override fun downloadComplete(item: WallpaperItem) {
        val position = wallpapers.indices.firstOrNull {
            TextUtils.equals(wallpapers[it].wallpaperId, item.wallpaperId)
        } ?: -1
        if (position >= 0) {
            wallpapersAdapter.notifyItemChanged(position)
        }
        downloadDialog!!.dismiss()
    }

    override fun showDownloadError(item: WallpaperItem, e: Exception) {
        downloadDialog!!.dismiss()
        showError(ErrorMessageFactory.create(activity, e))
    }

    override fun deletedDownloadWallpaper(wallpaperId: String) {
        var deleteIndex = -1
        wallpapers.forEachIndexed { index, wallpaperItem ->
            if (TextUtils.equals(wallpaperItem.wallpaperId, wallpaperId)) {
                deleteIndex = index
                return@forEachIndexed
            }
        }
        if (deleteIndex >= 0) {
            wallpapersAdapter.notifyItemChanged(deleteIndex)
        }
    }

    class AdvanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkedOverlayView: View = itemView.findViewById(R.id.current_select_overlay)
        var downloadOverlayView: View = itemView.findViewById(R.id.download_overlay)
        private var thumbnailView: View = itemView.findViewById(R.id.thumbnail)
        var thumbnail: ImageView = thumbnailView as ImageView
        private var nameView: View = itemView.findViewById(R.id.tvName)
        var tvName: TextView = nameView as TextView

        var icPro: View = itemView.findViewById(R.id.icon_pro)
    }

    private val wallpapersAdapter = object : RecyclerView.Adapter<AdvanceViewHolder>() {
        override fun onBindViewHolder(holder: AdvanceViewHolder, position: Int) {
            val item = wallpapers[position]
            holder.thumbnail.layoutParams.width = mItemSize
            holder.thumbnail.layoutParams.height = mItemSize
            Glide.with(activity)
                    .load(item.iconUrl)
                    .override(mItemSize, mItemSize)
                    .placeholder(placeHolderDrawable)
                    .into(holder.thumbnail)

            if (item.isSelected) {
                holder.checkedOverlayView.visibility = View.VISIBLE
            } else {
                holder.checkedOverlayView.visibility = View.GONE
            }
            val downloadingItem = presenter.getDownloadingItem()
            if (WallpaperFileHelper.isNeedDownloadWallpaper(item.lazyDownload,
                            item.storePath) || (downloadingItem != null
                            && TextUtils.equals(downloadingItem.wallpaperId, item.wallpaperId))) {
                holder.downloadOverlayView.visibility = View.VISIBLE
            } else {
                holder.downloadOverlayView.visibility = View.GONE
            }

            holder.tvName.visibility =
                    if (item.pro || !TextUtils.isEmpty(item.name)) View.VISIBLE else View.GONE
            holder.tvName.background = if (item.pro) proBackground else normalBackground
//            holder.icPro.visibility = if (item.pro) View.VISIBLE else View.GONE
            holder.tvName.text = item.name
        }

        override fun getItemCount(): Int = wallpapers.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AdvanceViewHolder {
            val view = LayoutInflater.from(activity)
                    .inflate(R.layout.advance_chosen_wallpaper_item, parent, false)

            val vh = AdvanceViewHolder(view)
            view.setOnClickListener {
                val item = wallpapers[vh.adapterPosition]
                presenter.previewWallpaper(item)
            }

            return vh
        }

        override fun getItemId(position: Int): Long = wallpapers[position].id
    }

}