package com.kinglloy.album.analytics


/**
 * @author jinyalin
 * *
 * @since 2017/5/3.
 */

object Event {
    const val WALLPAPER_CREATED = "wallpaper_created"
    const val WALLPAPER_DESTROYED = "wallpaper_destroyed"
    const val DEVICE_UNSUPPORTED = "device_unsupported"

    const val LOAD_ADVANCES = "load_advance_wallpapers"
    const val RETRY_LOAD_ADVANCES = "retry_load_advance_wallpapers"
    const val DOWNLOAD_COMPONENT = "download_component"
    const val DOWNLOAD_AD_COMPONENT = "download_ad_component"
    const val DOWNLOAD_RATE_COMPONENT = "download_rate_component"

    const val OPEN_AD_ACTIVITY = "open_ad_activity"
    const val TODAY_NOT_SHOW = "today_not_show"
    const val WATCH_AD_AGAIN = "watch_ad_again"
    const val LOAD_INTER_AD_FAILED = "load_inter_ad_failed"
    const val LOAD_VIDEO_AD_FAILED = "load_video_ad_failed"
    const val OPEN_INTER_AD = "open_inter_ad"
    const val OPEN_VIDEO_AD = "open_video_ad"

    const val INTER_AD_LOADED = "inter_ad_loaded"
    const val VIDEO_AD_LOADED = "video_ad_loaded"

    const val CHANNEL_KEY = "channel"
}
