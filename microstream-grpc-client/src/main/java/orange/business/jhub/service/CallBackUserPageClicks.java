package orange.business.jhub.service;

import java.util.List;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import orange.business.jhub.PageView;
import orange.business.jhub.mapper.ClicksAggregator;
import orange.business.jhub.mapper.ClicksMapper;
import orange.business.jhub.model.PageViewTime;
import orange.business.jhub.repository.DataViewtimeRepositoryImpl;
import orange.business.jhub.repository.DataPageViewRepositoryImpl;


@Slf4j
@Singleton
public class CallBackUserPageClicks implements StreamObserver<PageView> {

  @Inject
  DataPageViewRepositoryImpl repo;

  @Inject
  DataViewtimeRepositoryImpl repoAvg;

  @Inject
  ClicksMapper clickmapper;

  @Inject
  ClicksAggregator clicksAggregator;


  @Override
  public void onNext(final PageView value) {

    repo.updateUserPageClicks(String.valueOf(value.getTimestamp()).concat(value.getUserid()),
        clickmapper.map2PageViewClient(value));

    final List<PageViewTime> calcAvgViewTimes =
        clicksAggregator.avgViewTimes(value.getUserid(), value.getPageid(),
            repo.readAllPageClicks());

    calcAvgViewTimes.forEach(
        pageViewTimeRecord -> repoAvg.updateUserViewTime(value.getUserid(), pageViewTimeRecord));

  }

  /**
   * @param t the error occurred on the stream
   */
  @Override
  public void onError(final Throwable t) {
    log.error("Error occurred, cause: {}",
        t.getCause() != null ? t.getCause().getLocalizedMessage() : t.getLocalizedMessage());
  }

  /** */
  @Override
  public void onCompleted() {
    log.info("Finished calling async PageView Unary-API service..");
  }
}
