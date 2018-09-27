package com.kinglloy.album.view.activity

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.text.HtmlCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.kinglloy.album.AlbumApplication

import com.kinglloy.album.R
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import com.kinglloy.album.data.log.LogUtil
import com.kinglloy.album.data.utils.WallpaperFileHelper
import com.kinglloy.album.domain.WallpaperType
import com.kinglloy.album.exception.ErrorMessageFactory
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.presenter.MainWallpaperListPresenter
import com.kinglloy.album.view.WallpaperListView
import com.kinglloy.album.view.component.DownloadingDialog
import kotlinx.android.synthetic.main.activity_wallpaper_list.*
import org.jetbrains.anko.toast
import java.util.ArrayList
import javax.inject.Inject

class WallpaperListActivity : AppCompatActivity(), WallpaperListView {

  companion object {
    const val TAG = "AdvanceSettingActivity"
    const val LOAD_STATE = "load_state"

    const val LOAD_STATE_NORMAL = 0
    const val LOAD_STATE_LOADING = 1
    const val LOAD_STATE_RETRY = 2
  }

  @Inject internal lateinit var presenter: MainWallpaperListPresenter

  val wallpapers = ArrayList<WallpaperItem>()

  private var loadState = LOAD_STATE_NORMAL

  private var placeHolderDrawable: ColorDrawable? = null
  private var mItemSize = 10

  private var downloadDialog: DownloadingDialog? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AlbumApplication.instance.applicationComponent.inject(this)
    setContentView(R.layout.activity_wallpaper_list)

    setSupportActionBar(appBar)

    placeHolderDrawable = ColorDrawable(
      ContextCompat.getColor(
        this,
        R.color.gallery_chosen_photo_placeholder
      )
    )
    initViews()

    presenter.setView(this)

    if (savedInstanceState != null) {
      loadState = savedInstanceState.getInt(LOAD_STATE)
      presenter.onRestoreInstanceState(savedInstanceState)
    }

    handleState()
  }

  private fun handleState() {
    when (loadState) {
      LOAD_STATE_NORMAL -> presenter.initialize()
      LOAD_STATE_LOADING -> presenter.loadAdvanceWallpaper()
      LOAD_STATE_RETRY -> showRetry()
    }
  }

  private fun initViews() {
    val itemAnimator = DefaultItemAnimator()
    itemAnimator.supportsChangeAnimations = false
    wallpaperList.itemAnimator = itemAnimator

    val gridLayoutManager = GridLayoutManager(this, 1)
    wallpaperList.layoutManager = gridLayoutManager

    btnLoadAdvanceWallpaper.setOnClickListener {
      presenter.loadAdvanceWallpaper()
      Analytics.logEvent(this@WallpaperListActivity, Event.LOAD_ADVANCES)
    }
    btnRetry.setOnClickListener {
      presenter.loadAdvanceWallpaper()
      Analytics.logEvent(this@WallpaperListActivity, Event.RETRY_LOAD_ADVANCES)
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
            R.dimen.advance_grid_max_item_size
          )
          var numColumns = 1
          while (true) {
            if (width / numColumns > maxItemWidth) {
              ++numColumns
            } else {
              break
            }
          }

          val spacing = resources.getDimensionPixelSize(
            R.dimen.gallery_chosen_photo_grid_spacing
          )
          mItemSize = (width - spacing * (numColumns - 1)) / numColumns

          // Complete setup
          gridLayoutManager.spanCount = numColumns
          advanceWallpaperAdapter.setHasStableIds(true)
          wallpaperList.adapter = advanceWallpaperAdapter

          wallpaperList.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
      })

    ViewCompat.setOnApplyWindowInsetsListener(wallpaperList) { v, insets ->
      val gridSpacing = resources
        .getDimensionPixelSize(R.dimen.gallery_chosen_photo_grid_spacing)
      ViewCompat.onApplyWindowInsets(
        v, insets.replaceSystemWindowInsets(
          insets.systemWindowInsetLeft + gridSpacing,
          gridSpacing,
          insets.systemWindowInsetRight + gridSpacing,
          insets.systemWindowInsetBottom + insets.systemWindowInsetTop + gridSpacing
        )
      )

      insets
    }

    downloadDialog = DownloadingDialog(this,
      MaterialDialog.SingleButtonCallback { _, _ ->

      })
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
    super.onDestroy()
    presenter.destroy()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    super.onCreateOptionsMenu(menu)
    menuInflater.inflate(R.menu.advance_activity, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.action_advance_hint) {
      val dialogBuilder = MaterialDialog.Builder(this)
        .iconRes(R.drawable.advance_wallpaper_msg)
        .title(R.string.hint)
        .content(
          HtmlCompat.fromHtml(
            getString(R.string.advance_hint),
            HtmlCompat.FROM_HTML_MODE_LEGACY
          )
        )
        .positiveText(R.string.confirm)

      dialogBuilder.build().show()
      return true
    }
    return super.onOptionsItemSelected(item)
  }

  override fun renderWallpapers(wallpapers: List<WallpaperItem>) {
    loadState = LOAD_STATE_NORMAL

    this.wallpapers.clear()
    this.wallpapers.addAll(wallpapers)

    if (wallpaperList.visibility == View.VISIBLE) {
      return
    }
    wallpaperList.visibility = View.VISIBLE
    empty.visibility = View.GONE
    loading.visibility = View.GONE
    retry.visibility = View.GONE
    advanceWallpaperAdapter.notifyDataSetChanged()
  }

  override fun selectWallpaper(wallpaper: WallpaperItem) {
    var oldSelectedIndex = -1
    var newSelectedIndex = -1
    for ((index, value) in wallpapers.withIndex()) {
      if (value.isSelected) {
        value.isSelected = false
        oldSelectedIndex = index
      }
      if (TextUtils.equals(value.wallpaperId, wallpaper.wallpaperId)) {
        value.isSelected = true
        newSelectedIndex = index
      }
    }
    if (oldSelectedIndex != newSelectedIndex) {
      advanceWallpaperAdapter.notifyItemChanged(oldSelectedIndex)
      advanceWallpaperAdapter.notifyItemChanged(newSelectedIndex)
    }
  }

  override fun unSelectWallpaper(wallpaper: WallpaperItem) {
    var oldSelectedIndex = -1
    for ((index, value) in wallpapers.withIndex()) {
      if (value.isSelected) {
        value.isSelected = false
        oldSelectedIndex = index
      }
    }
    advanceWallpaperAdapter.notifyItemChanged(oldSelectedIndex)

  }

  override fun showLoading() {
    loadState = LOAD_STATE_LOADING

    wallpaperList.visibility = View.GONE
    empty.visibility = View.GONE
    loading.visibility = View.VISIBLE
    retry.visibility = View.GONE
  }

  override fun hideLoading() {

  }

  override fun showRetry() {
    loadState = LOAD_STATE_RETRY

    wallpaperList.visibility = View.GONE
    empty.visibility = View.GONE
    loading.visibility = View.GONE
    retry.visibility = View.VISIBLE
  }

  override fun hideRetry() {

  }

  override fun showError(message: String) {
    toast(message)
  }

  override fun showEmpty() {
    wallpaperList.visibility = View.GONE
    empty.visibility = View.VISIBLE
    loading.visibility = View.GONE
    retry.visibility = View.GONE
  }

  override fun context(): Context {
    return applicationContext
  }

  override fun complete() {
    finish()
  }

  override fun wallpaperSelected(wallpaperId: String) {
    wallpapers.forEach { it ->
      it.isSelected = TextUtils.equals(it.wallpaperId, wallpaperId)
    }
    advanceWallpaperAdapter.notifyDataSetChanged()
  }

  override fun showDownloadHintDialog(item: WallpaperItem) {
    val downloadCallback =
      MaterialDialog.SingleButtonCallback { _, _ ->
        presenter.requestDownload(item)
        Analytics.logEvent(
          this@WallpaperListActivity,
          Event.DOWNLOAD_COMPONENT, item.name
        )
      }
    val dialogBuilder = MaterialDialog.Builder(this)
      .iconRes(R.drawable.advance_wallpaper_msg)
      .title(R.string.hint)
      .content(
        HtmlCompat.fromHtml(
          getString(R.string.advance_download_hint),
          HtmlCompat.FROM_HTML_MODE_LEGACY
        )
      )
      .positiveText(R.string.advance_download_msg)
      .onPositive(downloadCallback)

    dialogBuilder.build().show()
  }

  override fun showDownloadingDialog(item: WallpaperItem) {
    LogUtil.D(TAG, "showDownloadingDialog ${item.name}")
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
      advanceWallpaperAdapter.notifyItemChanged(position)
    }
    downloadDialog!!.dismiss()
  }

  override fun showDownloadError(item: WallpaperItem, e: Exception) {
    downloadDialog!!.dismiss()
    showError(ErrorMessageFactory.create(this, e))
  }

  override fun deletedDownloadWallpaper(wallpaperId: String) {

  }

  override fun getWallpaperType() = WallpaperType.LIVE

  class AdvanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var checkedOverlayView: View = itemView.findViewById(R.id.current_select_overlay)
    var downloadOverlayView: View = itemView.findViewById(R.id.download_overlay)
    var thumbnailView: View = itemView.findViewById(R.id.thumbnail)
    var thumbnail: ImageView = thumbnailView as ImageView
    var nameView: View = itemView.findViewById(R.id.tvName)
    var tvName: TextView = nameView as TextView
  }

  private val advanceWallpaperAdapter = object : RecyclerView.Adapter<AdvanceViewHolder>() {
    override fun onBindViewHolder(holder: AdvanceViewHolder, position: Int) {
      val item = wallpapers[position]
      holder.thumbnail.layoutParams.width = mItemSize
      holder.thumbnail.layoutParams.height = mItemSize
      Glide.with(this@WallpaperListActivity)
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
      if (WallpaperFileHelper.isNeedDownloadWallpaper(
          item.lazyDownload,
          item.storePath
        ) || (downloadingItem != null
            && TextUtils.equals(downloadingItem.wallpaperId, item.wallpaperId))
      ) {
        holder.downloadOverlayView.visibility = View.VISIBLE
      } else {
        holder.downloadOverlayView.visibility = View.GONE
      }

      holder.tvName.text = item.name
    }

    override fun getItemCount(): Int {
      return wallpapers.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvanceViewHolder {
      val view = LayoutInflater.from(this@WallpaperListActivity)
        .inflate(R.layout.advance_chosen_wallpaper_item, parent, false)

      val vh = AdvanceViewHolder(view)
      view.setOnClickListener {
        val item = wallpapers[vh.adapterPosition]
        presenter.previewWallpaper(item, WallpaperType.LIVE)
      }

      return vh
    }

    override fun getItemId(position: Int): Long {
      return wallpapers[position].id
    }
  }
}