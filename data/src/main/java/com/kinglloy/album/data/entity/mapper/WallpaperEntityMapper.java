package com.kinglloy.album.data.entity.mapper;

import com.fernandocejas.arrow.checks.Preconditions;
import com.kinglloy.album.data.entity.TempHDWallpaperEntity;
import com.kinglloy.album.data.entity.TempStyleWallpaperEntity;
import com.kinglloy.album.data.entity.TempVideoWallpaperEntity;
import com.kinglloy.album.data.entity.WallpaperEntity;
import com.kinglloy.album.domain.Wallpaper;
import com.kinglloy.album.domain.WallpaperType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author jinyalin
 * @since 2017/7/28.
 */
@Singleton
public class WallpaperEntityMapper {
    @Inject
    public WallpaperEntityMapper() {

    }

    public Wallpaper transform(WallpaperEntity wallpaperEntity) {
        Preconditions.checkNotNull(wallpaperEntity, "Wallpaper can not be null.");
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.id = wallpaperEntity.id;
        wallpaper.wallpaperId = wallpaperEntity.wallpaperId;
        wallpaper.link = wallpaperEntity.link;
        wallpaper.name = wallpaperEntity.name;
        wallpaper.author = wallpaperEntity.author;
        wallpaper.iconUrl = wallpaperEntity.iconUrl;
        wallpaper.downloadUrl = wallpaperEntity.downloadUrl;
        wallpaper.providerName = wallpaperEntity.providerName;
        wallpaper.storePath = wallpaperEntity.storePath;
        wallpaper.isDefault = wallpaperEntity.isDefault;
        wallpaper.isSelected = wallpaperEntity.isSelected;
        wallpaper.lazyDownload = wallpaperEntity.lazyDownload;
        wallpaper.wallpaperType = wallpaperEntity.type;
        wallpaper.size = wallpaperEntity.size;
        wallpaper.price = wallpaperEntity.price;
        wallpaper.pro = wallpaperEntity.pro;

        return wallpaper;
    }

    public WallpaperEntity transformToEntity(Wallpaper wallpaper) {
        Preconditions.checkNotNull(wallpaper, "Wallpaper can not be null.");
        WallpaperEntity wallpaperEntity = new WallpaperEntity();
        wallpaperEntity.id = wallpaper.id;
        wallpaperEntity.wallpaperId = wallpaper.wallpaperId;
        wallpaperEntity.link = wallpaper.link;
        wallpaperEntity.name = wallpaper.name;
        wallpaperEntity.author = wallpaper.author;
        wallpaperEntity.iconUrl = wallpaper.iconUrl;
        wallpaperEntity.downloadUrl = wallpaper.downloadUrl;
        wallpaperEntity.providerName = wallpaper.providerName;
        wallpaperEntity.storePath = wallpaper.storePath;
        wallpaperEntity.isDefault = wallpaper.isDefault;
        wallpaperEntity.isSelected = wallpaper.isSelected;
        wallpaperEntity.lazyDownload = wallpaper.lazyDownload;
        wallpaperEntity.type = wallpaper.wallpaperType;
        wallpaperEntity.size = wallpaper.size;
        wallpaperEntity.price = wallpaper.price;
        wallpaperEntity.pro = wallpaper.pro;

        return wallpaperEntity;
    }

    public List<Wallpaper> transformList(List<WallpaperEntity> wallpaperEntities) {
        Preconditions.checkNotNull(wallpaperEntities, "SourceEntity can not be null.");
        List<Wallpaper> sources = new ArrayList<>();
        for (WallpaperEntity entity : wallpaperEntities) {
            sources.add(transform(entity));
        }
        return sources;
    }

    public List<WallpaperEntity> transformFromTempStyleEntity(ArrayList<TempStyleWallpaperEntity> temp) {
        Preconditions.checkNotNull(temp, "TempEntities can not be null.");
        List<WallpaperEntity> results = new ArrayList<>();
        for (TempStyleWallpaperEntity tempEntity : temp) {
            WallpaperEntity entity = new WallpaperEntity();
            entity.type = WallpaperType.STYLE;
            entity.checkSum = tempEntity.checksum;
            entity.downloadUrl = tempEntity.imageUri;
            entity.iconUrl = tempEntity.imageUri;
            entity.name = tempEntity.title;
            entity.wallpaperId = tempEntity.wallpaperId;
            entity.size = tempEntity.size;
            entity.pro = tempEntity.pro;
            results.add(entity);
        }
        return results;
    }

    public List<WallpaperEntity> transformFromTempVideoEntity(ArrayList<TempVideoWallpaperEntity> temp) {
        Preconditions.checkNotNull(temp, "TempEntities can not be null.");
        List<WallpaperEntity> results = new ArrayList<>();
        for (TempVideoWallpaperEntity tempEntity : temp) {
            WallpaperEntity entity = new WallpaperEntity();
            entity.type = WallpaperType.VIDEO;
            entity.checkSum = tempEntity.checksum;
            entity.downloadUrl = tempEntity.videoUri;
            entity.iconUrl = tempEntity.iconUri;
            entity.name = tempEntity.name;
            entity.wallpaperId = tempEntity.wallpaperId;
            entity.size = tempEntity.size;
            entity.pro = tempEntity.pro;
            results.add(entity);
        }
        return results;
    }

    public List<WallpaperEntity> transformFromTempHDEntity(ArrayList<TempHDWallpaperEntity> temp) {
        Preconditions.checkNotNull(temp, "TempEntities can not be null.");
        List<WallpaperEntity> results = new ArrayList<>();
        for (TempHDWallpaperEntity tempEntity : temp) {
            WallpaperEntity entity = new WallpaperEntity();
            entity.type = WallpaperType.HD;
            entity.checkSum = tempEntity.checksum;
            entity.downloadUrl = tempEntity.imageUri;
            entity.iconUrl = tempEntity.imageUri;
            entity.name = tempEntity.title;
            entity.wallpaperId = tempEntity.wallpaperId;
            entity.size = tempEntity.size;
            entity.pro = tempEntity.pro;
            results.add(entity);
        }
        return results;
    }
}
