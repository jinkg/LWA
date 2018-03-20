package com.kinglloy.album.data.repository.datasource.provider;

/**
 * YaLin 2016/12/30.
 */

public enum AlbumUriEnum {
    LIVE_WALLPAPER(100, "live_wallpaper", AlbumDatabase.Tables.LIVE_WALLPAPER),
    LIVE_WALLPAPER_SELECTED(102, "live_wallpaper/selected", null),
    LIVE_WALLPAPER_PREVIEWING(103, "live_wallpaper/previewing", null),
    LIVE_WALLPAPER_ID(101, "live_wallpaper/*", null),

    STYLE_WALLPAPER(300, "style_wallpaper", AlbumDatabase.Tables.STYLE_WALLPAPER),
    STYLE_WALLPAPER_SELECTED(302, "style_wallpaper/selected", null),
    STYLE_WALLPAPER_PREVIEWING(303, "style_wallpaper/previewing", null),
    STYLE_WALLPAPER_ID(301, "style_wallpaper/*", null),

    VIDEO_WALLPAPER(500, "video_wallpaper", AlbumDatabase.Tables.VIDEO_WALLPAPER),
    VIDEO_WALLPAPER_SELECTED(502, "video_wallpaper/selected", null),
    VIDEO_WALLPAPER_PREVIEWING(503, "video_wallpaper/previewing", null),
    VIDEO_WALLPAPER_ID(501, "video_wallpaper/*", null),

    HD_WALLPAPER(600, "hd_wallpaper", AlbumDatabase.Tables.HD_WALLPAPER),
    HD_WALLPAPER_SELECTED(602, "hd_wallpaper/selected", null),
    HD_WALLPAPER_PREVIEWING(603, "hd_wallpaper/previewing", null),
    HD_WALLPAPER_ID(601, "hd_wallpaper/*", null),

    ACTIVE_SERVICE(200, "active_service", AlbumDatabase.Tables.ACTIVE_SERVICE),

    PREVIEWING_WALLPAPER(400, "previewing_wallpaper", AlbumDatabase.Tables.PREVIEWING_WALLPAPER);


    public int code;
    public String path;
    public String table;

    AlbumUriEnum(int code, String path, String table) {
        this.code = code;
        this.path = path;
        this.table = table;
    }
}
