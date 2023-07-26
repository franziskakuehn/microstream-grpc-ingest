package orange.business.jhub;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterators;
import io.grpc.stub.StreamObserver;
import io.micronaut.runtime.EmbeddedApplication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;


@Slf4j
class ClickEndpointServerTest extends TestUtil {

  @Inject
  ClickCallback clickCallback;

  @Inject
  EmbeddedApplication<?> application;


  @Test
  void testItWorks() {
    Assertions.assertTrue(application.isRunning());
  }

  @Test
  @SneakyThrows
  @Disabled("step through server call")
  void getPagesCount() {

    val pageId = "Page_79";

    val pageIdRequest = PageIdRequest.newBuilder().setPageid(pageId).build();

    asyncStub.pagesCountById(pageIdRequest, clickCallback);

    Thread.sleep(3000);

    log.info("Finished Call");
  }

  @DisplayName("clicks per user on page")
  @Test
  @Disabled("step through server call")
  void getClicksPerUser() {

    final String userId = "User_2";
    final String pageId = "Page_81";

    final UserOnPageRequest request =
        UserOnPageRequest.newBuilder().setUserid(userId).setPageid(pageId).build();

    final Iterator<PageView> pageViewIterator = blockingStub.userClicksByIdOnPage(request);

    log.info(
        "clicks per user {} : {} on page {}", userId, Iterators.size(pageViewIterator), pageId);
    // pageViewIterator.forEachRemaining(input -> LOG.info(input.toString()));

  }


  @Test
  @Disabled("step through server call")
  void getPagesById() {
    log.info("test");
    final String pageId = "Page_81";

    final PageIdRequest pageIdRequest = PageIdRequest.newBuilder().setPageid(pageId).build();
    final Stopwatch stopwatch = Stopwatch.createStarted();
    final Iterator<PageView> pageViewIterator = blockingStub.pagesById(pageIdRequest);
    stopwatch.stop(); // optional

    final Iterable<PageView> iterable = () -> pageViewIterator;
    final Stream<PageView> targetStream = StreamSupport.stream(iterable.spliterator(), false);

    log.info("clicks per page {} : {}", pageId, targetStream.count());
    log.info("access time in milliseconds: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
  }

}


@Slf4j
@Singleton
class ClickCallback implements StreamObserver<PageIdCountResponse> {

  @Override
  public void onNext(final PageIdCountResponse value) {
    log.info("Received page counts, {}", value.getCountPages());
  }

  @Override
  public void onError(final Throwable cause) {
    log.error("Error occurred, cause {}", cause.getMessage());
  }

  @Override
  public void onCompleted() {
    log.info("Stream completed");
  }
}

