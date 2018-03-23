package com.kinglloy.album.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.kinglloy.album.domain.WallpaperType;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */

public class WallpaperItem implements Parcelable {
    public long id;
    public String wallpaperId;
    public String link;
    public String name;
    public String author;
    public String iconUrl;
    public String downloadUrl;

    public boolean lazyDownload;

    public String providerName;

    public String storePath;

    public boolean isSelected;
    public long size;
    public float price;
    public boolean pro;

    public WallpaperType wallpaperType;

    public WallpaperItem() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof WallpaperItem) {
            WallpaperItem item = (WallpaperItem) obj;
            return TextUtils.equals(item.wallpaperId, wallpaperId) &&
                    TextUtils.equals(item.storePath, storePath) &&
                    TextUtils.equals(item.name, name) &&
                    TextUtils.equals(item.providerName, providerName);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + wallpaperId.hashCode();
        result = 31 * result + storePath.hashCode();
        result = 31 * result + name.hashCode();
        if (!TextUtils.isEmpty(providerName)) {
            result = 31 * result + providerName.hashCode();
        }
        return result;
    }

    protected WallpaperItem(Parcel in) {
        id = in.readLong();
        wallpaperId = in.readString();
        link = in.readString();
        name = in.readString();
        author = in.readString();
        iconUrl = in.readString();
        downloadUrl = in.readString();
        lazyDownload = in.readByte() != 0;
        providerName = in.readString();
        storePath = in.readString();
        isSelected = in.readByte() != 0;
        wallpaperType = WallpaperType.fromTypeInt(in.readByte());
        size = in.readLong();
        price = in.readFloat();
        pro = in.readByte() != 0;
    }

    public static final Creator<WallpaperItem> CREATOR = new Creator<WallpaperItem>() {
        @Override
        public WallpaperItem createFromParcel(Parcel in) {
            return new WallpaperItem(in);
        }

        @Override
        public WallpaperItem[] newArray(int size) {
            return new WallpaperItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(wallpaperId);
        dest.writeString(link);
        dest.writeString(name);
        dest.writeString(author);
        dest.writeString(iconUrl);
        dest.writeString(downloadUrl);
        dest.writeByte((byte) (lazyDownload ? 1 : 0));
        dest.writeString(providerName);
        dest.writeString(storePath);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (wallpaperType.getTypeInt()));
        dest.writeLong(size);
        dest.writeFloat(price);
        dest.writeByte((byte) (pro ? 1 : 0));
    }
}
