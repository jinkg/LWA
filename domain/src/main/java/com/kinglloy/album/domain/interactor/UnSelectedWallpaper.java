package com.kinglloy.album.domain.interactor;

import com.kinglloy.album.domain.executor.PostExecutionThread;
import com.kinglloy.album.domain.executor.ThreadExecutor;
import com.kinglloy.album.domain.repository.WallpaperRepository;
import io.reactivex.Observable;
import javax.inject.Inject;

/**
 * YaLin
 * On 2018/9/25.
 */
public class UnSelectedWallpaper extends UseCase<Boolean, Void> {
  private WallpaperRepository repository;

  @Inject
  public UnSelectedWallpaper(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread,
      WallpaperRepository repository) {
    super(threadExecutor, postExecutionThread);
    this.repository = repository;
  }

  @Override
  public Observable<Boolean> buildUseCaseObservable(Void aVoid) {
    return repository.unSelectPreviewingWallpaper();
  }
}