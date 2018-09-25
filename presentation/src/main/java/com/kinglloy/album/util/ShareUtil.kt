package com.kinglloy.album.util

import android.content.Context
import android.content.Intent
import com.kinglloy.album.R

/**
 * @author jinyalin
 * @since 2018/9/25.
 */
class ShareUtil {
  companion object {
    private fun getShareString(context: Context) = with(context) {
      val packageName =
        if (PackageUtil.isPro(this)) context.getString(R.string.pro_version_package_name)
        else context.getString(R.string.ad_version_package_name)

      val result = String.format(
        context.getString(R.string.share_text), packageName
      )
      result
    }

    fun createShareIntent(context: Context): Intent {
      var shareIntent = Intent(Intent.ACTION_SEND)
      shareIntent.type = "text/plain"
      shareIntent.putExtra(
        Intent.EXTRA_TEXT, ShareUtil.getShareString(context)
      )
      shareIntent = Intent.createChooser(
        shareIntent,
        context.getString(R.string.share_title)
      )
      return shareIntent
    }
  }
}