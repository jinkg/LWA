package com.kinglloy.album.analytics

import android.content.Context
import android.os.Bundle

import com.google.firebase.analytics.FirebaseAnalytics
import com.kinglloy.album.BuildConfig
import com.kinglloy.album.analytics.Event.CHANNEL_KEY

/**
 * @author jinyalin
 * *
 * @since 2017/5/3.
 */

object Analytics : IAnalytics {
    override fun init(context: Context) {
        setUserProperty(context, CHANNEL_KEY, BuildConfig.CHANNEL)
    }


    override fun setUserProperty(context: Context, key: String, value: String) {
        FirebaseAnalytics.getInstance(context)
                .setUserProperty(key, value)
    }

    override fun onStartSession(context: Context) {
    }

    override fun onEndSession(context: Context) {
    }

    override fun logEvent(context: Context, event: String) {
        FirebaseAnalytics.getInstance(context).logEvent(event, null)
    }

    override fun logEvent(context: Context, event: String, vararg params: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, params[0])
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
    }

    override fun logEvent(context: Context, event: String, bundle: Bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
    }
}
