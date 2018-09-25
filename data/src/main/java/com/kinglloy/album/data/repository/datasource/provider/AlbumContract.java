package com.kinglloy.album.data.repository.datasource.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.kinglloy.album.data.BuildConfig;

/**
 * @author jinyalin
 * @since 2017/9/26.
 */

public class AlbumContract {
  public static final String AUTHORITY = BuildConfig.AUTHORITY;

  private static final String SCHEME = "content://";

  public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

  private static final String PATH_LIVE_WALLPAPER = "live_wallpaper";
  private static final String PATH_STYLE_WALLPAPER = "style_wallpaper";
  private static final String PATH_VIDEO_WALLPAPER = "video_wallpaper";
  private static final String PATH_HD_WALLPAPER = "hd_wallpaper";
  private static final String PATH_ACTIVE_SERVICE = "active_service";
  private static final String PATH_PREVIEWING_WALLPAPER = "previewing_wallpaper";
  public static final String[] TOP_LEVEL_PATHS = {
      PATH_LIVE_WALLPAPER,
      PATH_STYLE_WALLPAPER,
      PATH_HD_WALLPAPER,
      PATH_VIDEO_WALLPAPER
  };

  interface LiveWallpaperColumns {
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_WALLPAPER_ID = "wallpaper_id";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_ICON_URL = "icon_url";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_NAME = "name";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_LINK = "link";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_AUTHOR = "author";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_DOWNLOAD_URL = "download_url";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_CHECKSUM = "checksum";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_STORE_PATH = "store_path";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_PROVIDER_NAME = "provider_name";
    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SELECTED = "selected";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_LAZY_DOWNLOAD = "lazy_download";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PREVIEWING = "previewing";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SIZE = "size";

    /**
     * Type: REAL
     */
    String COLUMN_NAME_PRICE = "price";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PRO = "pro";
  }

  interface StyleWallpaperColumns {
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_WALLPAPER_ID = "wallpaper_id";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_ICON_URL = "icon_url";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_NAME = "name";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_DOWNLOAD_URL = "download_url";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_CHECKSUM = "checksum";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_STORE_PATH = "store_path";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SELECTED = "selected";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PREVIEWING = "previewing";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SIZE = "size";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PRO = "pro";
  }

  interface VideoWallpaperColumns {
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_WALLPAPER_ID = "wallpaper_id";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_ICON_URL = "icon_url";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_NAME = "name";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_DOWNLOAD_URL = "download_url";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_CHECKSUM = "checksum";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_STORE_PATH = "store_path";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SELECTED = "selected";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PREVIEWING = "previewing";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SIZE = "size";

    /**
     * Type: REAL
     */
    String COLUMN_NAME_PRICE = "price";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PRO = "pro";
  }

  interface HDWallpaperColumns {
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_WALLPAPER_ID = "wallpaper_id";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_ICON_URL = "icon_url";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_NAME = "name";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_DOWNLOAD_URL = "download_url";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_CHECKSUM = "checksum";
    /**
     * Type: TEXT
     */
    String COLUMN_NAME_STORE_PATH = "store_path";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SELECTED = "selected";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PREVIEWING = "previewing";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_SIZE = "size";

    /**
     * Type: INTEGER
     */
    String COLUMN_NAME_PRO = "pro";
  }

  interface ActiveServiceColumns {
    /**
     * Type: INTEGER range: 0 1 2
     */
    String COLUMN_NAME_SERVICE_ID = "service_id";
  }

  interface PreviewingWallpaperColumns {
    /**
     * Type: INTEGER range: {@link com.kinglloy.album.domain.WallpaperType}
     */
    String COLUMN_NAME_WALLPAPER_TYPE = "wallpaper_type";

    /**
     * Type: TEXT
     */
    String COLUMN_NAME_WALLPAPER_ID = "wallpaper_id";
  }

  public static final class LiveWallpaper implements LiveWallpaperColumns, BaseColumns {
    public static final String TABLE_NAME = "advance_wallpaper";

    public static final String PATH_SELECTED_WALLPAPER = "selected";
    public static final String PATH_PREVIEWING_WALLPAPER = "previewing";
    public static final String PATH_SELECT_PREVIEWING_WALLPAPER = "select_previewing";
    public static final String PATH_UNSELECT_WALLPAPER = "unselect";
    public static final String PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER = "item_deleted";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_LIVE_WALLPAPER).build();

    public static final Uri CONTENT_SELECTED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIVE_WALLPAPER)
            .appendPath(PATH_SELECTED_WALLPAPER).build();

    public static final Uri CONTENT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIVE_WALLPAPER)
            .appendPath(PATH_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_SELECT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIVE_WALLPAPER)
            .appendPath(PATH_SELECT_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_UNSELECT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIVE_WALLPAPER)
            .appendPath(PATH_UNSELECT_WALLPAPER).build();

    public static final Uri CONTENT_DOWNLOAD_ITEM_DELETED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIVE_WALLPAPER)
            .appendPath(PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER).build();

    public static Uri buildWallpaperUri(String wallpaperId) {
      return CONTENT_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static Uri buildDeletedWallpaperUri(String wallpaperId) {
      return CONTENT_DOWNLOAD_ITEM_DELETED_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static String getDeletedWallpaperId(Uri uri) {
      return uri.getPathSegments().get(2);
    }

    public static String getWallpaperId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  public static final class StyleWallpaper implements StyleWallpaperColumns, BaseColumns {
    public static final String TABLE_NAME = "style_wallpaper";

    public static final String PATH_SELECTED_WALLPAPER = "selected";
    public static final String PATH_PREVIEWING_WALLPAPER = "previewing";
    public static final String PATH_SELECT_PREVIEWING_WALLPAPER = "select_previewing";
    public static final String PATH_UNSELECT_WALLPAPER = "unselect";
    public static final String PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER = "item_deleted";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_STYLE_WALLPAPER).build();

    public static final Uri CONTENT_SELECTED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_STYLE_WALLPAPER)
            .appendPath(PATH_SELECTED_WALLPAPER).build();

    public static final Uri CONTENT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_STYLE_WALLPAPER)
            .appendPath(PATH_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_SELECT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_STYLE_WALLPAPER)
            .appendPath(PATH_SELECT_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_UNSELECT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_STYLE_WALLPAPER)
            .appendPath(PATH_UNSELECT_WALLPAPER).build();

    public static final Uri CONTENT_DOWNLOAD_ITEM_DELETED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_STYLE_WALLPAPER)
            .appendPath(PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER).build();

    public static Uri buildWallpaperUri(String wallpaperId) {
      return CONTENT_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static Uri buildDeletedWallpaperUri(String wallpaperId) {
      return CONTENT_DOWNLOAD_ITEM_DELETED_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static String getDeletedWallpaperId(Uri uri) {
      return uri.getPathSegments().get(2);
    }

    public static String getWallpaperId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  public static final class VideoWallpaper implements VideoWallpaperColumns, BaseColumns {
    public static final String TABLE_NAME = "video_wallpaper";

    public static final String PATH_SELECTED_WALLPAPER = "selected";
    public static final String PATH_PREVIEWING_WALLPAPER = "previewing";
    public static final String PATH_SELECT_PREVIEWING_WALLPAPER = "select_previewing";
    public static final String PATH_UNSELECT_WALLPAPER = "unselect";
    public static final String PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER = "item_deleted";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_VIDEO_WALLPAPER).build();

    public static final Uri CONTENT_SELECTED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO_WALLPAPER)
            .appendPath(PATH_SELECTED_WALLPAPER).build();

    public static final Uri CONTENT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO_WALLPAPER)
            .appendPath(PATH_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_SELECT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO_WALLPAPER)
            .appendPath(PATH_SELECT_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_UNSELECT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO_WALLPAPER)
            .appendPath(PATH_UNSELECT_WALLPAPER).build();

    public static final Uri CONTENT_DOWNLOAD_ITEM_DELETED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO_WALLPAPER)
            .appendPath(PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER).build();

    public static Uri buildWallpaperUri(String wallpaperId) {
      return CONTENT_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static Uri buildDeletedWallpaperUri(String wallpaperId) {
      return CONTENT_DOWNLOAD_ITEM_DELETED_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static String getDeletedWallpaperId(Uri uri) {
      return uri.getPathSegments().get(2);
    }

    public static String getWallpaperId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  public static final class HDWallpaper implements HDWallpaperColumns, BaseColumns {
    public static final String TABLE_NAME = "hd_wallpaper";

    public static final String PATH_SELECTED_WALLPAPER = "selected";
    public static final String PATH_PREVIEWING_WALLPAPER = "previewing";
    public static final String PATH_SELECT_PREVIEWING_WALLPAPER = "select_previewing";
    public static final String PATH_UNSELECT_WALLPAPER = "unselect";
    public static final String PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER = "item_deleted";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_HD_WALLPAPER).build();

    public static final Uri CONTENT_SELECTED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_HD_WALLPAPER)
            .appendPath(PATH_SELECTED_WALLPAPER).build();

    public static final Uri CONTENT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_HD_WALLPAPER)
            .appendPath(PATH_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_SELECT_PREVIEWING_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_HD_WALLPAPER)
            .appendPath(PATH_SELECT_PREVIEWING_WALLPAPER).build();

    public static final Uri CONTENT_UNSELECT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_HD_WALLPAPER)
            .appendPath(PATH_UNSELECT_WALLPAPER).build();

    public static final Uri CONTENT_DOWNLOAD_ITEM_DELETED_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_HD_WALLPAPER)
            .appendPath(PATH_DOWNLOAD_ITEM_DELETED_WALLPAPER).build();

    public static Uri buildWallpaperUri(String wallpaperId) {
      return CONTENT_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static Uri buildDeletedWallpaperUri(String wallpaperId) {
      return CONTENT_DOWNLOAD_ITEM_DELETED_URI.buildUpon().appendPath(wallpaperId).build();
    }

    public static String getDeletedWallpaperId(Uri uri) {
      return uri.getPathSegments().get(2);
    }

    public static String getWallpaperId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  public static final class ActiveService implements ActiveServiceColumns, BaseColumns {
    public static final String TABLE_NAME = "active_service";
    public static final int SERVICE_NONE = 0;
    public static final int SERVICE_ORIGIN = 1;
    public static final int SERVICE_MIRROR = 2;

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_ACTIVE_SERVICE).build();
  }

  public static final class PreviewingWallpaper implements PreviewingWallpaperColumns,
      BaseColumns {
    public static final String TABLE_NAME = "previewing_wallpaper";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_PREVIEWING_WALLPAPER).build();
  }
}
