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
 * @since 2017/7/28.
 */

public class GetWallpapers extends UseCase<List<Wallpaper>, GetWallpapers.Params> {

    private WallpaperRepository repository;

    @Inject
    public GetWallpapers(ThreadExecutor threadExecutor,
                         PostExecutionThread postExecutionThread,
                         WallpaperRepository repository) {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    public Observable<List<Wallpaper>> buildUseCaseObservable(GetWallpapers.Params params) {
        switch (params.wallpaperType) {
            case LIVE:
                return repository.getLiveWallpapers();
            case STYLE:
                return repository.getStyleWallpapers();
            case HD:
                return repository.getHDWallpapers();
            default:
                return repository.getVideoWallpapers();
        }

    }

    public static final class Params {
        private final WallpaperType wallpaperType;

        private Params(WallpaperType wallpaperType) {
            this.wallpaperType = wallpaperType;
        }

        public static GetWallpapers.Params withType(WallpaperType wallpaperType) {
            return new GetWallpapers.Params(wallpaperType);
        }
    }
}
