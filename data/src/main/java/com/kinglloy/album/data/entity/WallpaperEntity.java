package com.kinglloy.album.data.entity;

import android.database.Cursor;
import android.text.TextUtils;


import com.kinglloy.album.data.repository.datasource.provider.AlbumContract;
import com.kinglloy.album.domain.WallpaperType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class WallpaperEntity {
    private static final String TAG = "AdvanceWallpaperEntity";

    public long id;
    public String wallpaperId;
    public String link;
    public String name;
    public String author;
    public String iconUrl;
    public String downloadUrl;
    public String providerName;

    public boolean lazyDownload;

    public String storePath;
    public String checkSum;

    public boolean isDefault = false;

    public boolean isSelected = false;

    public boolean isPreviewing = false;

    public long size;
    public float price;
    public boolean pro;

    public WallpaperType type;

    public static List<WallpaperEntity> liveWallpaperValues(Cursor cursor) {
        List<WallpaperEntity> validWallpapers = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            WallpaperEntity wallpaperEntity = liveWallpaperValue(cursor);

            validWallpapers.add(wallpaperEntity);
        }
        return validWallpapers;
    }

    public static WallpaperEntity liveWallpaperValue(Cursor cursor) {
        WallpaperEntity wallpaperEntity = new WallpaperEntity();

        wallpaperEntity.id = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper._ID));
        wallpaperEntity.name = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_NAME));
        wallpaperEntity.wallpaperId = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_WALLPAPER_ID));
        wallpaperEntity.iconUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_ICON_URL));
        wallpaperEntity.link = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_LINK));
        wallpaperEntity.author = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_AUTHOR));
        wallpaperEntity.downloadUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_DOWNLOAD_URL));
        wallpaperEntity.checkSum = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_CHECKSUM));
        wallpaperEntity.storePath = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_STORE_PATH));
        wallpaperEntity.providerName = cursor.getString(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_PROVIDER_NAME));
        wallpaperEntity.isSelected = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_SELECTED)) == 1;
        wallpaperEntity.lazyDownload = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_LAZY_DOWNLOAD)) == 1;
        wallpaperEntity.isPreviewing = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_PREVIEWING)) == 1;
        wallpaperEntity.size = cursor.getLong(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_SIZE));
        wallpaperEntity.price = cursor.getFloat(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_PRICE));
        wallpaperEntity.pro = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.LiveWallpaper.COLUMN_NAME_PRO)) == 1;
        wallpaperEntity.type = WallpaperType.LIVE;

        return wallpaperEntity;
    }

    public static List<WallpaperEntity> styleWallpaperValues(Cursor cursor) {
        List<WallpaperEntity> validWallpapers = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            WallpaperEntity wallpaperEntity = styleWallpaperValue(cursor);
            validWallpapers.add(wallpaperEntity);
        }
        return validWallpapers;
    }

    public static WallpaperEntity styleWallpaperValue(Cursor cursor) {
        WallpaperEntity wallpaperEntity = new WallpaperEntity();

        wallpaperEntity.id = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper._ID));
        wallpaperEntity.name = cursor.getString(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_NAME));
        wallpaperEntity.wallpaperId = cursor.getString(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_WALLPAPER_ID));
        wallpaperEntity.iconUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_ICON_URL));
        wallpaperEntity.downloadUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_DOWNLOAD_URL));
        wallpaperEntity.checkSum = cursor.getString(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_CHECKSUM));
        wallpaperEntity.storePath = cursor.getString(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_STORE_PATH));
        wallpaperEntity.isSelected = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_SELECTED)) == 1;
        wallpaperEntity.isPreviewing = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_PREVIEWING)) == 1;
        wallpaperEntity.size = cursor.getLong(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_SIZE));
        wallpaperEntity.pro = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.StyleWallpaper.COLUMN_NAME_PRO)) == 1;
        wallpaperEntity.type = WallpaperType.STYLE;

        return wallpaperEntity;
    }

    public static List<WallpaperEntity> videoWallpaperValues(Cursor cursor) {
        List<WallpaperEntity> validWallpapers = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            WallpaperEntity wallpaperEntity = videoWallpaperValue(cursor);
            validWallpapers.add(wallpaperEntity);
        }
        return validWallpapers;
    }

    public static WallpaperEntity videoWallpaperValue(Cursor cursor) {
        WallpaperEntity wallpaperEntity = new WallpaperEntity();
        wallpaperEntity.id = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper._ID));
        wallpaperEntity.name = cursor.getString(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_NAME));
        wallpaperEntity.wallpaperId = cursor.getString(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_WALLPAPER_ID));
        wallpaperEntity.iconUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_ICON_URL));
        wallpaperEntity.downloadUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_DOWNLOAD_URL));
        wallpaperEntity.checkSum = cursor.getString(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_CHECKSUM));
        wallpaperEntity.storePath = cursor.getString(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_STORE_PATH));
        wallpaperEntity.isSelected = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_SELECTED)) == 1;
        wallpaperEntity.isPreviewing = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_PREVIEWING)) == 1;
        wallpaperEntity.size = cursor.getLong(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_SIZE));
        wallpaperEntity.price = cursor.getFloat(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_PRICE));
        wallpaperEntity.pro = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.VideoWallpaper.COLUMN_NAME_PRO)) == 1;

        wallpaperEntity.type = WallpaperType.VIDEO;

        return wallpaperEntity;
    }

    public static List<WallpaperEntity> hdWallpaperValues(Cursor cursor) {
        List<WallpaperEntity> validWallpapers = new ArrayList<>();
        while (cursor != null && cursor.moveToNext()) {
            WallpaperEntity wallpaperEntity = hdWallpaperValue(cursor);
            validWallpapers.add(wallpaperEntity);
        }
        return validWallpapers;
    }

    public static WallpaperEntity hdWallpaperValue(Cursor cursor) {
        WallpaperEntity wallpaperEntity = new WallpaperEntity();

        wallpaperEntity.id = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.HDWallpaper._ID));
        wallpaperEntity.name = cursor.getString(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_NAME));
        wallpaperEntity.wallpaperId = cursor.getString(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_WALLPAPER_ID));
        wallpaperEntity.iconUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_ICON_URL));
        wallpaperEntity.downloadUrl = cursor.getString(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_DOWNLOAD_URL));
        wallpaperEntity.checkSum = cursor.getString(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_CHECKSUM));
        wallpaperEntity.storePath = cursor.getString(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_STORE_PATH));
        wallpaperEntity.isSelected = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_SELECTED)) == 1;
        wallpaperEntity.isPreviewing = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_PREVIEWING)) == 1;
        wallpaperEntity.size = cursor.getLong(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_SIZE));
        wallpaperEntity.pro = cursor.getInt(cursor.getColumnIndex(
                AlbumContract.HDWallpaper.COLUMN_NAME_PRO)) == 1;
        wallpaperEntity.type = WallpaperType.HD;

        return wallpaperEntity;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WallpaperEntity) {
            if (TextUtils.equals(((WallpaperEntity) obj).name, name)
                    && TextUtils.equals(((WallpaperEntity) obj).checkSum, checkSum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + checkSum.hashCode();
        return result;
    }
}
