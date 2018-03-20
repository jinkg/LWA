package com.kinglloy.album.data.repository.datasource.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kinglloy.album.data.log.LogUtil;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.HDWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.LiveWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.StyleWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.VideoWallpaper;
import com.kinglloy.album.data.utils.SelectionBuilder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * YaLin 2016/12/30.
 */

public class AlbumProvider extends ContentProvider {

    private static final String TAG = "AlbumProvider";

    private AlbumDatabase mOpenHelper;

    private AlbumProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new AlbumDatabase(getContext());
        mUriMatcher = new AlbumProviderUriMatcher();
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        AlbumDatabase.deleteDatabase(context);
        mOpenHelper = new AlbumDatabase(context);
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        AlbumUriEnum uriEnum = mUriMatcher.matchUri(uri);

        LogUtil.D(TAG, "uri=" + uri + " code=" + uriEnum.code + " proj=" +
                Arrays.toString(projection) + " selection=" + selection + " args="
                + Arrays.toString(selectionArgs) + ")");

        switch (uriEnum) {
            case LIVE_WALLPAPER:
            case LIVE_WALLPAPER_ID:
            case LIVE_WALLPAPER_SELECTED:
            case LIVE_WALLPAPER_PREVIEWING:
            case STYLE_WALLPAPER:
            case STYLE_WALLPAPER_ID:
            case STYLE_WALLPAPER_SELECTED:
            case STYLE_WALLPAPER_PREVIEWING:
            case VIDEO_WALLPAPER:
            case VIDEO_WALLPAPER_ID:
            case VIDEO_WALLPAPER_SELECTED:
            case VIDEO_WALLPAPER_PREVIEWING:
            case HD_WALLPAPER:
            case HD_WALLPAPER_ID:
            case HD_WALLPAPER_SELECTED:
            case HD_WALLPAPER_PREVIEWING:
            case ACTIVE_SERVICE:
            case PREVIEWING_WALLPAPER: {
                final SelectionBuilder builder = buildSimpleSelection(uri);
                return builder.query(db, projection, BaseColumns._ID + " DESC");
            }
            default: {
                final SelectionBuilder builder = buildExpandedSelection(uri, uriEnum.code);

                return builder.query(db, projection, null);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        LogUtil.D(TAG, "insert(uri=" + uri + ", values=" + values.toString()
                + ")");

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        AlbumUriEnum uriEnum = mUriMatcher.matchUri(uri);
        long id = db.insertOrThrow(uriEnum.table, null, values);

        switch (uriEnum) {
            case LIVE_WALLPAPER: {
                return LiveWallpaper.buildWallpaperUri(
                        values.getAsString(LiveWallpaper.COLUMN_NAME_WALLPAPER_ID));
            }
            case STYLE_WALLPAPER: {
                return StyleWallpaper.buildWallpaperUri(
                        values.getAsString(StyleWallpaper.COLUMN_NAME_WALLPAPER_ID));
            }
            case VIDEO_WALLPAPER: {
                return VideoWallpaper.buildWallpaperUri(
                        values.getAsString(VideoWallpaper.COLUMN_NAME_WALLPAPER_ID));
            }
            case HD_WALLPAPER: {
                return HDWallpaper.buildWallpaperUri(
                        values.getAsString(StyleWallpaper.COLUMN_NAME_WALLPAPER_ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        LogUtil.D(TAG, "delete(uri=" + uri + ")");
        if (uri == AlbumContract.BASE_CONTENT_URI) {
            deleteDatabase();
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        AlbumUriEnum uriEnum = mUriMatcher.matchUri(uri);
        final SelectionBuilder builder = buildSimpleSelection(uri);
        switch (uriEnum) {
            case LIVE_WALLPAPER: {
                builder.where(LiveWallpaper.COLUMN_NAME_SELECTED + " = ?", "0");
                break;
            }
            case STYLE_WALLPAPER: {
                builder.where(StyleWallpaper.COLUMN_NAME_SELECTED + " = ?", "0");
                break;
            }
            case VIDEO_WALLPAPER: {
                builder.where(VideoWallpaper.COLUMN_NAME_SELECTED + " = ?", "0");
                break;
            }
            case HD_WALLPAPER: {
                builder.where(HDWallpaper.COLUMN_NAME_SELECTED + " = ?", "0");
                break;
            }
        }
        return builder.where(selection, selectionArgs).delete(db);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        LogUtil.D(TAG, "update(uri=" + uri + ")");
        if (uri == AlbumContract.BASE_CONTENT_URI) {
            deleteDatabase();
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        AlbumUriEnum uriEnum = mUriMatcher.matchUri(uri);

        switch (uriEnum) {
            case LIVE_WALLPAPER:
            case STYLE_WALLPAPER:
            case VIDEO_WALLPAPER:
            case HD_WALLPAPER:
            case ACTIVE_SERVICE:
            case PREVIEWING_WALLPAPER: {
                return builder.table(uriEnum.table);
            }
            case LIVE_WALLPAPER_ID: {
                String wallpaperId = LiveWallpaper.getWallpaperId(uri);
                return builder.table(AlbumDatabase.Tables.LIVE_WALLPAPER)
                        .where(LiveWallpaper.COLUMN_NAME_WALLPAPER_ID + " = ?", wallpaperId);
            }
            case LIVE_WALLPAPER_SELECTED: {
                return builder.table(AlbumDatabase.Tables.LIVE_WALLPAPER)
                        .where(LiveWallpaper.COLUMN_NAME_SELECTED + " = ?", String.valueOf(1));
            }
            case LIVE_WALLPAPER_PREVIEWING: {
                return builder.table(AlbumDatabase.Tables.LIVE_WALLPAPER)
                        .where(LiveWallpaper.COLUMN_NAME_PREVIEWING + " = ?", String.valueOf(1));
            }
            case STYLE_WALLPAPER_ID: {
                String wallpaperId = StyleWallpaper.getWallpaperId(uri);
                return builder.table(AlbumDatabase.Tables.STYLE_WALLPAPER)
                        .where(StyleWallpaper.COLUMN_NAME_WALLPAPER_ID + " = ?", wallpaperId);
            }
            case STYLE_WALLPAPER_SELECTED: {
                return builder.table(AlbumDatabase.Tables.STYLE_WALLPAPER)
                        .where(StyleWallpaper.COLUMN_NAME_SELECTED + " = ?", String.valueOf(1));
            }
            case STYLE_WALLPAPER_PREVIEWING: {
                return builder.table(AlbumDatabase.Tables.STYLE_WALLPAPER)
                        .where(StyleWallpaper.COLUMN_NAME_PREVIEWING + " = ?", String.valueOf(1));
            }
            case VIDEO_WALLPAPER_ID: {
                String wallpaperId = VideoWallpaper.getWallpaperId(uri);
                return builder.table(AlbumDatabase.Tables.VIDEO_WALLPAPER)
                        .where(VideoWallpaper.COLUMN_NAME_WALLPAPER_ID + " = ?", wallpaperId);
            }
            case VIDEO_WALLPAPER_SELECTED: {
                return builder.table(AlbumDatabase.Tables.VIDEO_WALLPAPER)
                        .where(VideoWallpaper.COLUMN_NAME_SELECTED + " = ?", String.valueOf(1));
            }
            case VIDEO_WALLPAPER_PREVIEWING: {
                return builder.table(AlbumDatabase.Tables.VIDEO_WALLPAPER)
                        .where(VideoWallpaper.COLUMN_NAME_PREVIEWING + " = ?", String.valueOf(1));
            }
            case HD_WALLPAPER_ID: {
                String wallpaperId = HDWallpaper.getWallpaperId(uri);
                return builder.table(AlbumDatabase.Tables.HD_WALLPAPER)
                        .where(HDWallpaper.COLUMN_NAME_WALLPAPER_ID + " = ?", wallpaperId);
            }
            case HD_WALLPAPER_SELECTED: {
                return builder.table(AlbumDatabase.Tables.HD_WALLPAPER)
                        .where(HDWallpaper.COLUMN_NAME_SELECTED + " = ?", String.valueOf(1));
            }
            case HD_WALLPAPER_PREVIEWING: {
                return builder.table(AlbumDatabase.Tables.HD_WALLPAPER)
                        .where(HDWallpaper.COLUMN_NAME_PREVIEWING + " = ?", String.valueOf(1));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        AlbumUriEnum uriEnum = mUriMatcher.matchUri(uri);
        if (uriEnum == null) {
            throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        switch (uriEnum) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
