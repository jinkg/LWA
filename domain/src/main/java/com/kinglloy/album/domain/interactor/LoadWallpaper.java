package com.kinglloy.album.domain.interactor;


import com.kinglloy.album.domain.Wallpaper;
import com.kinglloy.album.domain.WallpaperType;
import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * @author jinyalin
 * @since 2017/7/31.
 */

public class LoadWallpaper extends UseCase<List<Wallpaper>, LoadWallpaper.Params> {
    private WallpaperRepository repository;

    @Inject
    public LoadWallpaper(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<List<Wallpaper>> buildUseCaseObservable(Params params) {
        switch (params.wallpaperType) {
            case LIVE:
                return repository.loadLiveWallpapers();
            case STYLE:
                return repository.loadStyleWallpapers();
            case HD:
                return repository.loadHDWallpapers();
            default:
                return repository.loadVideoWallpapers();
        }

    }

    public static final class Params {
        private final WallpaperType wallpaperType;

        private Params(WallpaperType wallpaperType) {
            this.wallpaperType = wallpaperType;
        }

        public static LoadWallpaper.Params withType(WallpaperType wallpaperType) {
            return new LoadWallpaper.Params(wallpaperType);
        }
    }
}
