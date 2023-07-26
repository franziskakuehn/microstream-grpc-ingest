package orange.business.jhub.repo;

import jakarta.inject.Singleton;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.types.StorageManager;
import orange.business.jhub.PageView;
import orange.business.jhub.model.MicroStreamData;

@Slf4j
@Singleton
public class ClickRepositoryImpl implements ClickRepository {

  private final StorageManager storageManager;

  public ClickRepositoryImpl(final StorageManager storageManager) {
    this.storageManager = storageManager;
  }

  public MicroStreamData root() {
    return (MicroStreamData) storageManager.root();
  }

  /**
   * save new pageview object to page view list
   * 
   * @param pageview
   */
  @Override
  public void savePageView(final PageView pageview) {

    val pageViews = root().getPageViews();

    log.debug("add new pageview");
    pageViews.add(pageview);
    XThreads.executeSynchronized(
        () -> storageManager.store(pageViews));
    log.debug("microstream list size: {}", readAllPages().count());
  }

  /**
   * reads from MicroStream database (from underlying java collection)
   * 
   * @return
   */
  @Override
  public Stream<PageView> readAllPages() {
    val pageViews = root().getPageViews();
    return pageViews != null ? pageViews.stream() : Stream.empty();
  }
}
