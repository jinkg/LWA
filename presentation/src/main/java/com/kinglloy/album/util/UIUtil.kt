package com.kinglloy.album.util

import android.content.Context


/**
 * @author jinyalin
 * @since 2018/3/28.
 */
object UIUtils {
    fun getGridSize(context: Context, gridCount: Int): Int {
        val screenWidth = context.resources.displayMetrics.widthPixels
        return screenWidth / gridCount
    }
}