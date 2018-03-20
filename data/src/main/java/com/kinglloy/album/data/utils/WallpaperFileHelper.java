package com.kinglloy.album.data.utils;

import android.content.Context;
import android.text.TextUtils;


import com.kinglloy.common.utils.NativeFileHelper;

import java.io.File;
import java.util.Set;

/**
 * YaLin On 2017/1/2.
 */

public class WallpaperFileHelper {

    private static final String LIVE_WALLPAPER_FOLDER = "component";
    private static final String STYLE_WALLPAPER_FOLDER = "style_wallpaper";
    private static final String VIDEO_WALLPAPER_FOLDER = "video_wallpaper";
    private static final String HD_WALLPAPER_FOLDER = "hd_wallpaper";

    public static void deleteOldLiveComponent(Context context, Set<String> excludeNames) {
        File dir = getLiveWallpaperDir(context);
        deleteFiles(context, dir, excludeNames);
    }

    public static void deleteOldStyleWallpaper(Context context, Set<String> excludeNames) {
        File dir = getStyleWallpaperDir(context);
        deleteFiles(context, dir, excludeNames);
    }

    public static void deleteOldVideoWallpaper(Context context, Set<String> excludeNames) {
        File dir = getVideoWallpaperDir(context);
        deleteFiles(context, dir, excludeNames);
    }

    public static void deleteOldHDWallpaper(Context context, Set<String> excludeNames) {
        File dir = getHDWallpaperDir(context);
        deleteFiles(context, dir, excludeNames);
    }


    private static void deleteFiles(Context context, File dir, Set<String> excludeNames) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles(fileName ->
                !excludeNames.contains(fileName.getName()));
        for (File file : files) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            NativeFileHelper.INSTANCE.clearNativeFiles(context, file.getAbsolutePath());
        }
    }

    public static boolean ensureChecksumValid(Context context,
                                              String checksum, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        String computedChecksum = ChecksumUtil.getChecksum(file);
        if (TextUtils.equals(checksum, computedChecksum)) {
            return true;
        }
        //noinspection ResultOfMethodCallIgnored
        file.delete();
        return false;
    }

    public static File getLiveWallpaperDir(Context context) {
        return new File(context.getFilesDir(), LIVE_WALLPAPER_FOLDER);
    }

    public static File getStyleWallpaperDir(Context context) {
        return new File(context.getFilesDir(), STYLE_WALLPAPER_FOLDER);
    }

    public static File getVideoWallpaperDir(Context context) {
        return new File(context.getFilesDir(), VIDEO_WALLPAPER_FOLDER);
    }

    public static File getHDWallpaperDir(Context context) {
        return new File(context.getFilesDir(), HD_WALLPAPER_FOLDER);
    }

    public static boolean isNeedDownloadWallpaper(boolean lazy, String storePath) {
        return !new File(storePath).exists();
    }
}
