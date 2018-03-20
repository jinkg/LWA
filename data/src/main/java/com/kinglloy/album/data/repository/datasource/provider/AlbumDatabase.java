package com.kinglloy.album.data.repository.datasource.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.kinglloy.album.data.R;
import com.kinglloy.album.data.log.LogUtil;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.ActiveService;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.HDWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.LiveWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.PreviewingWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.StyleWallpaper;
import com.kinglloy.album.data.repository.datasource.provider.AlbumContract.VideoWallpaper;
import com.kinglloy.album.data.repository.datasource.sync.account.Account;
import com.kinglloy.album.domain.WallpaperType;


/**
 * YaLin 2016/12/30.
 */

public class AlbumDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AlbumDatabase";
    private static final String DATABASE_NAME = "album.db";

    private static final int VERSION_2017_9_26 = 1;
    private static final int VERSION_2017_9_27 = 2;
    private static final int VERSION_2017_11_3 = 3;
    private static final int VERSION_2018_3_20 = 4;
    private static final int CUR_DATABASE_VERSION = VERSION_2018_3_20;

    private final Context mContext;

    interface Tables {
        String LIVE_WALLPAPER = LiveWallpaper.TABLE_NAME;
        String STYLE_WALLPAPER = StyleWallpaper.TABLE_NAME;
        String VIDEO_WALLPAPER = VideoWallpaper.TABLE_NAME;
        String HD_WALLPAPER = HDWallpaper.TABLE_NAME;
        String ACTIVE_SERVICE = ActiveService.TABLE_NAME;
        String PREVIEWING_WALLPAPER = PreviewingWallpaper.TABLE_NAME;
    }

    public AlbumDatabase(Context context) {
        super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.LIVE_WALLPAPER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LiveWallpaper.COLUMN_NAME_WALLPAPER_ID + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_ICON_URL + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_DOWNLOAD_URL + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_NAME + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_AUTHOR + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_STORE_PATH + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_LINK + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_PROVIDER_NAME + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_CHECKSUM + " TEXT,"
                + LiveWallpaper.COLUMN_NAME_SELECTED + " INTEGER DEFAULT 0,"
                + LiveWallpaper.COLUMN_NAME_LAZY_DOWNLOAD + " INTEGER DEFAULT 1,"
                + LiveWallpaper.COLUMN_NAME_PREVIEWING + " INTEGER NOT NULL DEFAULT 0);");

        upgradeFrom20170926to20170927(db);
        upgradeFrom20170927to20171103(db);
        upgradeFrom20171103to20180320(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.D(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        // Cancel any sync currently in progress
        android.accounts.Account account = Account.getAccount();
        if (account != null) {
            LogUtil.D(TAG, "Cancelling any pending syncs for account");
            ContentResolver.cancelSync(account, mContext.getString(R.string.authority));
        }
        int version = oldVersion;
        if (version == VERSION_2017_9_26) {
            upgradeFrom20170926to20170927(db);
            version = VERSION_2017_9_27;
        }

        if (version == VERSION_2017_9_27) {
            upgradeFrom20170927to20171103(db);
            version = VERSION_2017_11_3;
        }

        if (version == VERSION_2017_11_3) {
            upgradeFrom20171103to20180320(db);
            version = VERSION_2018_3_20;
        }

        if (version != CUR_DATABASE_VERSION) {
            LogUtil.E(TAG, "Upgrade unsuccessful -- destroying old data during upgrade");

            db.execSQL("DROP TABLE IF EXISTS " + Tables.LIVE_WALLPAPER);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.ACTIVE_SERVICE);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.STYLE_WALLPAPER);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.VIDEO_WALLPAPER);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.HD_WALLPAPER);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.PREVIEWING_WALLPAPER);
            onCreate(db);
            version = CUR_DATABASE_VERSION;
        }
    }

    private void upgradeFrom20170926to20170927(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ACTIVE_SERVICE + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ActiveService.COLUMN_NAME_SERVICE_ID + "  INTEGER NOT NULL DEFAULT 0);");
        ContentValues contentValues = new ContentValues();
        contentValues.put(ActiveService.COLUMN_NAME_SERVICE_ID, 0);
        db.insert(Tables.ACTIVE_SERVICE, null, contentValues);
    }

    private void upgradeFrom20170927to20171103(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.STYLE_WALLPAPER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StyleWallpaper.COLUMN_NAME_WALLPAPER_ID + " TEXT,"
                + StyleWallpaper.COLUMN_NAME_ICON_URL + " TEXT,"
                + StyleWallpaper.COLUMN_NAME_DOWNLOAD_URL + " TEXT,"
                + StyleWallpaper.COLUMN_NAME_NAME + " TEXT,"
                + StyleWallpaper.COLUMN_NAME_STORE_PATH + " TEXT,"
                + StyleWallpaper.COLUMN_NAME_CHECKSUM + " TEXT,"
                + StyleWallpaper.COLUMN_NAME_SELECTED + " INTEGER DEFAULT 0,"
                + StyleWallpaper.COLUMN_NAME_PREVIEWING + " INTEGER NOT NULL DEFAULT 0,"
                + StyleWallpaper.COLUMN_NAME_SIZE + " INTEGER DEFAULT 0,"
                + StyleWallpaper.COLUMN_NAME_PRO + " INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE " + Tables.VIDEO_WALLPAPER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + VideoWallpaper.COLUMN_NAME_WALLPAPER_ID + " TEXT,"
                + VideoWallpaper.COLUMN_NAME_ICON_URL + " TEXT,"
                + VideoWallpaper.COLUMN_NAME_DOWNLOAD_URL + " TEXT,"
                + VideoWallpaper.COLUMN_NAME_NAME + " TEXT,"
                + VideoWallpaper.COLUMN_NAME_STORE_PATH + " TEXT,"
                + VideoWallpaper.COLUMN_NAME_CHECKSUM + " TEXT,"
                + VideoWallpaper.COLUMN_NAME_SELECTED + " INTEGER DEFAULT 0,"
                + VideoWallpaper.COLUMN_NAME_PREVIEWING + " INTEGER NOT NULL DEFAULT 0,"
                + VideoWallpaper.COLUMN_NAME_PRICE + " REAL DEFAULT 0,"
                + VideoWallpaper.COLUMN_NAME_SIZE + " INTEGER DEFAULT 0,"
                + VideoWallpaper.COLUMN_NAME_PRO + " INTEGER DEFAULT 0);");

        db.execSQL("ALTER TABLE " + Tables.LIVE_WALLPAPER
                + " ADD COLUMN " + LiveWallpaper.COLUMN_NAME_SIZE + " INTEGER DEFAULT 0");
        db.execSQL("ALTER TABLE " + Tables.LIVE_WALLPAPER
                + " ADD COLUMN " + LiveWallpaper.COLUMN_NAME_PRICE + " REAL DEFAULT 0");
        db.execSQL("ALTER TABLE " + Tables.LIVE_WALLPAPER
                + " ADD COLUMN " + LiveWallpaper.COLUMN_NAME_PRO + " INTEGER DEFAULT 0");

        db.execSQL("CREATE TABLE " + Tables.PREVIEWING_WALLPAPER + " ("
                + BaseColumns._ID + " INTEGER DEFAULT 0,"
                + PreviewingWallpaper.COLUMN_NAME_WALLPAPER_TYPE + " INTEGER NOT NULL,"
                + PreviewingWallpaper.COLUMN_NAME_WALLPAPER_ID + " TEXT NOT NULL);");
        ContentValues contentValues = new ContentValues();
        contentValues.put(PreviewingWallpaper.COLUMN_NAME_WALLPAPER_TYPE,
                WallpaperType.LIVE.getTypeInt());
        contentValues.put(PreviewingWallpaper.COLUMN_NAME_WALLPAPER_ID, "0");
        db.insert(Tables.PREVIEWING_WALLPAPER, null, contentValues);
    }

    private void upgradeFrom20171103to20180320(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.HD_WALLPAPER + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HDWallpaper.COLUMN_NAME_WALLPAPER_ID + " TEXT,"
                + HDWallpaper.COLUMN_NAME_ICON_URL + " TEXT,"
                + HDWallpaper.COLUMN_NAME_DOWNLOAD_URL + " TEXT,"
                + HDWallpaper.COLUMN_NAME_NAME + " TEXT,"
                + HDWallpaper.COLUMN_NAME_STORE_PATH + " TEXT,"
                + HDWallpaper.COLUMN_NAME_CHECKSUM + " TEXT,"
                + HDWallpaper.COLUMN_NAME_SELECTED + " INTEGER DEFAULT 0,"
                + HDWallpaper.COLUMN_NAME_PREVIEWING + " INTEGER NOT NULL DEFAULT 0,"
                + HDWallpaper.COLUMN_NAME_SIZE + " INTEGER DEFAULT 0,"
                + HDWallpaper.COLUMN_NAME_PRO + " INTEGER DEFAULT 0);");
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
