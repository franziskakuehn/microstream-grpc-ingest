package orange.business.jhub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import io.grpc.stub.StreamObserver;
import io.micronaut.runtime.EmbeddedApplication;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ClickEndpointClientTest extends TestUtil {

  @Inject
  ClickCallbackUserClicksByIdOnPage clickCallbackUserClicksByIdOnPage;

  @Inject
  EmbeddedApplication<?> application;

  @Test
  void testItWorks() {
    Assertions.assertTrue(application.isRunning());
  }


  @Inject
  ClickCallbackPagesCountById clickCallbackPagesCountById;


  @Test
  @SneakyThrows
  @Disabled("requires microstream-grpc-server to be started")
  void getUserClicksByIdOnPage() {

    val pageId = "Page_81";
    val userId = "User_8";

    val pageIdRequest =
        UserOnPageRequestAgg.newBuilder().setPageid(pageId).setUserid(userId).build();

    asyncStub.userClicksByIdOnPage(pageIdRequest, clickCallbackUserClicksByIdOnPage);

    Thread.sleep(6000);

    log.info("Finished Call");
  }


  @Test
  @SneakyThrows
  @Disabled("requires microstream-grpc-server to be started")
  void getPagesCount() {

    val pageId = "Page_79";

    val pageIdRequest = PageIdRequestAgg.newBuilder().setPageid(pageId).build();

    asyncStub.pagesCountById(pageIdRequest, clickCallbackPagesCountById);

    Thread.sleep(3000);

    log.info("Finished Call");
  }
}


@Slf4j
@Singleton
class ClickCallbackUserClicksByIdOnPage implements StreamObserver<UserOnPageResponse> {

  @Override
  public void onNext(final UserOnPageResponse value) {
    log.info("Received page avg time, {}", value.toString());
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


@Slf4j
@Singleton
class ClickCallbackPagesCountById implements StreamObserver<PageIdCountResponseAgg> {

  @Override
  public void onNext(final PageIdCountResponseAgg value) {
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
