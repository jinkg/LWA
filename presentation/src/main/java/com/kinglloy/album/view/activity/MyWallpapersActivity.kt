package com.kinglloy.album.view.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kinglloy.album.AlbumApplication
import com.kinglloy.album.R
import com.kinglloy.album.model.WallpaperItem
import com.kinglloy.album.presenter.MyWallpapersPresenter
import com.kinglloy.album.util.MultiSelectionController
import com.kinglloy.album.util.UIUtils
import com.kinglloy.album.view.MyWallpapersView
import kotlinx.android.synthetic.main.activity_my_wallpapers.*
import java.util.*
import javax.inject.Inject

/**
 * YaLin
 * On 2017/11/8.
 */
class MyWallpapersActivity : AppCompatActivity(), MyWallpapersView {
    companion object {
        private const val STATE_SELECTION = "selection"

        private const val SELECTION_MODE = "selection_mode"
    }

    private lateinit var wallpaperList: RecyclerView
    private lateinit var loadingView: View
    private lateinit var emptyView: View

    private lateinit var proBackground: Drawable
    private lateinit var normalBackground: Drawable

    @Inject
    internal lateinit var presenter: MyWallpapersPresenter

    private var placeHolderDrawable: ColorDrawable? = null
    private var mItemSize = 10

    private var selectionMode = false

    val wallpapers = LinkedList<WallpaperItem>()

    private val currentDeleting = ArrayList<WallpaperItem>()

    private val mMultiSelectionController =
            MultiSelectionController<WallpaperItem>(STATE_SELECTION)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_wallpapers)
        AlbumApplication.instance.applicationComponent.inject(this)
        presenter.setView(this)

        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBar.setNavigationOnClickListener {
            onBackPressed()
        }

        setupMultiSelect()

        initView()
        presenter.initialize()

        if (savedInstanceState != null) {
            selectionMode = !savedInstanceState.getBoolean(SELECTION_MODE)
            tryUpdateSelection(false)
        }
        mMultiSelectionController.restoreInstanceState(savedInstanceState)
    }

    override fun onBackPressed() {
        if (selectionMode) {
            updatePosition = -1
            mMultiSelectionController.reset(true)
            tryUpdateSelection(true)
        } else {
            super.onBackPressed()
        }
    }

    private fun initView() {
        wallpaperList = findViewById(R.id.wallpaperList)
        loadingView = findViewById(R.id.loading)
        emptyView = findViewById(android.R.id.empty)

        proBackground = ContextCompat.getDrawable(this, R.drawable.pro_wallpaper_gradient)!!
        normalBackground = ColorDrawable(ContextCompat.getColor(this, R.color.color_name_bg))

        val itemAnimator = DefaultItemAnimator()
        itemAnimator.supportsChangeAnimations = false
        wallpaperList.itemAnimator = itemAnimator

        val gridLayoutManager = GridLayoutManager(this, 1)
        wallpaperList.layoutManager = gridLayoutManager

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

                        // Complete setup
                        gridLayoutManager.spanCount = numColumns
                        mItemSize = UIUtils.getGridSize(this@MyWallpapersActivity, numColumns)
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

    }

    private fun tryUpdateSelection(animation: Boolean) {
        val duration =
                if (animation)
                    resources.getInteger(android.R.integer.config_shortAnimTime)
                else 0

        if (!selectionMode) {
            selectionToolbarContainer.visibility = View.VISIBLE
            selectionToolbarContainer.translationY =
                    (-selectionToolbarContainer.height).toFloat()
            selectionToolbarContainer.animate()
                    .translationY(0f)
                    .setDuration(duration.toLong())
                    .withEndAction(null)
        } else {
            selectionToolbarContainer.animate()
                    .translationY((-selectionToolbarContainer.height).toFloat())
                    .setDuration(duration.toLong())
                    .withEndAction { selectionToolbarContainer.visibility = View.INVISIBLE }
        }
        selectionMode = !selectionMode
    }


    private fun setupMultiSelect() {
        // Set up toolbar
        selectionToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        selectionToolbar.inflateMenu(R.menu.menu_my_wallpapers_edit)
        selectionToolbar.setOnMenuItemClickListener {
            if (mMultiSelectionController.getSelectedCount() == 0) {
                true
            } else {
                updatePosition = -1
                currentDeleting.clear()
                currentDeleting.addAll(mMultiSelectionController.getSelection())
                presenter.deleteDownloadedWallpapers(currentDeleting)
                mMultiSelectionController.reset(true)
                tryUpdateSelection(true)
                removeItemFromAdapter(currentDeleting)
                true
            }
        }

        // Set up controller
        mMultiSelectionController.setCallbacks(object : MultiSelectionController.Callbacks {
            override fun onSelectionChanged(restored: Boolean, fromUser: Boolean) {
                if (updatePosition >= 0) {
                    wallpapersAdapter.notifyItemChanged(updatePosition)
                    updatePosition = -1
                } else {
                    wallpapersAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun removeItemFromAdapter(deleting: ArrayList<WallpaperItem>) {
        for (item in deleting) {
            val index = wallpapers.indexOf(item)
            wallpapers.removeAt(index)
            wallpapersAdapter.notifyItemRemoved(index)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_my_wallpapers, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                if (wallpapers.size > 0) {
                    tryUpdateSelection(true)
                }
            }
        }
        return true
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null) {
            outState.putBoolean(SELECTION_MODE, selectionMode)
            mMultiSelectionController.saveInstanceState(outState)
        }
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

    override fun renderWallpapers(wallpapers: List<WallpaperItem>) {
        this.wallpapers.clear()
        this.wallpapers.addAll(wallpapers)

        wallpaperList.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.GONE
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

    override fun showUndoDelete() {
        val snackbar = Snackbar.make(findViewById<View>(R.id.root_view),
                getString(R.string.delete_count, currentDeleting.size), Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.undo) { presenter.undoDelete() }
        snackbar.show()
    }

    override fun closeUndoDelete() {

    }

    override fun showLoading() {
        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.VISIBLE
    }

    override fun showEmpty() {
        wallpaperList.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    override fun complete() {
        finish()
    }

    override fun hideLoading() {

    }

    override fun deleteWallpapers(wallpapers: List<WallpaperItem>) {

    }

    override fun showRetry() {

    }

    override fun hideRetry() {

    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun context() = applicationContext!!

    class WallpaperViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectedOverlayView: View = itemView.findViewById(R.id.current_select_overlay)
        var checkOverlayView: View = itemView.findViewById(R.id.checked_overlay)
        private var thumbnailView: View = itemView.findViewById(R.id.thumbnail)
        var thumbnail: ImageView = thumbnailView as ImageView
        private var nameView: View = itemView.findViewById(R.id.tvName)
        var tvName: TextView = nameView as TextView
    }

    private var mLastTouchPosition: Int = 0
    private var mLastTouchX: Int = 0
    private var mLastTouchY: Int = 0

    private var updatePosition: Int = -1
    private val wallpapersAdapter = object : RecyclerView.Adapter<WallpaperViewHolder>() {
        override fun onBindViewHolder(holder: WallpaperViewHolder, position: Int) {
            val item = wallpapers[position]
            holder.thumbnail.layoutParams.width = mItemSize
            holder.thumbnail.layoutParams.height = mItemSize
            Glide.with(this@MyWallpapersActivity)
                    .load(item.iconUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .override(mItemSize, mItemSize)
                    .placeholder(placeHolderDrawable)
                    .into(holder.thumbnail)

            if (item.isSelected) {
                holder.selectedOverlayView.visibility = View.VISIBLE
            } else {
                holder.selectedOverlayView.visibility = View.GONE
            }
            val checked = mMultiSelectionController.isSelected(item)

            if (mLastTouchPosition == holder.adapterPosition
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Handler().post {
                    if (!holder.checkOverlayView.isAttachedToWindow) {
                        // Can't animate detached Views
                        holder.checkOverlayView.visibility = if (checked) View.VISIBLE else View.GONE
                        return@post
                    }
                    if (checked) {
                        holder.checkOverlayView.visibility = View.VISIBLE
                    }

                    // find the smallest radius that'll cover the item
                    val coverRadius = maxDistanceToCorner(
                            mLastTouchX, mLastTouchY,
                            0, 0, holder.itemView.width, holder.itemView.height)

                    val revealAnim = ViewAnimationUtils.createCircularReveal(
                            holder.checkOverlayView,
                            mLastTouchX,
                            mLastTouchY,
                            if (checked) 0f else coverRadius,
                            if (checked) coverRadius else 0f)
                            .setDuration(150)

                    if (!checked) {
                        revealAnim.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                holder.checkOverlayView.visibility = View.GONE
                            }
                        })
                    }
                    revealAnim.start()
                }
            } else {
                holder.checkOverlayView.visibility = if (checked) View.VISIBLE else View.GONE
            }

            holder.tvName.visibility =
                    if (item.pro || !TextUtils.isEmpty(item.name)) View.VISIBLE else View.GONE
            holder.tvName.background = if (item.pro) proBackground else normalBackground
            holder.tvName.text = item.name
        }

        override fun getItemCount(): Int = wallpapers.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallpaperViewHolder {
            val view = LayoutInflater.from(this@MyWallpapersActivity)
                    .inflate(R.layout.advance_chosen_wallpaper_item, parent, false)

            val vh = WallpaperViewHolder(view)
            view.layoutParams.height = mItemSize
            view.setOnTouchListener { _, motionEvent ->
                if (motionEvent.actionMasked != MotionEvent.ACTION_CANCEL) {
                    mLastTouchPosition = vh.adapterPosition
                    mLastTouchX = motionEvent.x.toInt()
                    mLastTouchY = motionEvent.y.toInt()
                }
                false
            }

            view.setOnClickListener {
                val item = wallpapers[vh.adapterPosition]
                if (selectionMode) {
                    updatePosition = vh.adapterPosition
                    if (updatePosition != RecyclerView.NO_POSITION
                            && !wallpapers[updatePosition].isSelected) {
                        mMultiSelectionController.toggle(wallpapers[updatePosition], true)
                    }
                } else {
                    presenter.previewWallpaper(item)
                }
            }

            return vh
        }

        private fun maxDistanceToCorner(x: Int, y: Int, left: Int, top: Int,
                                        right: Int, bottom: Int): Float {
            var maxDistance = 0f
            maxDistance = Math.max(maxDistance,
                    Math.hypot((x - left).toDouble(), (y - top).toDouble()).toFloat())
            maxDistance = Math.max(maxDistance,
                    Math.hypot((x - right).toDouble(), (y - top).toDouble()).toFloat())
            maxDistance = Math.max(maxDistance,
                    Math.hypot((x - left).toDouble(), (y - bottom).toDouble()).toFloat())
            maxDistance = Math.max(maxDistance,
                    Math.hypot((x - right).toDouble(), (y - bottom).toDouble()).toFloat())
            return maxDistance
        }

        override fun getItemId(position: Int): Long = wallpapers[position].id
    }
}