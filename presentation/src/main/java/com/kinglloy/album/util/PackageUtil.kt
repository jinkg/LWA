package com.kinglloy.album.util

import android.content.Context
import android.text.TextUtils
import com.kinglloy.album.R

/**
 * @author jinyalin
 * @since 2017/11/27.
 */
object PackageUtil {
  fun isPro(context: Context): Boolean {
    return TextUtils.equals(
      context.packageName,
      context.getString(R.string.pro_version_package_name)
    )
  }
}