package orange.business.jhub.service;

import java.util.Iterator;
import com.google.common.collect.Iterators;
import io.grpc.stub.StreamObserver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import orange.business.jhub.ClickAggServiceGrpc;
import orange.business.jhub.ClickServiceGrpc;
import orange.business.jhub.PageIdCountResponse;
import orange.business.jhub.PageIdCountResponseAgg;
import orange.business.jhub.PageIdRequest;
import orange.business.jhub.PageIdRequestAgg;
import orange.business.jhub.PageView;
import orange.business.jhub.PageViewAgg;
import orange.business.jhub.UserOnPageRequest;
import orange.business.jhub.UserOnPageRequestAgg;
import orange.business.jhub.UserOnPageResponse;
import orange.business.jhub.mapper.ClicksMapper;
import orange.business.jhub.repository.DataPageViewRepositoryImpl;
import orange.business.jhub.repository.DataViewtimeRepositoryImpl;

@Slf4j
@Singleton
public class ClickServiceClient extends ClickAggServiceGrpc.ClickAggServiceImplBase {

  @Inject
  DataPageViewRepositoryImpl repo;

  @Inject
  DataViewtimeRepositoryImpl repoAvg;

  @Inject
  CallBackUserPageClicks callBackUserPageClicks;

  @Inject
  ClicksMapper pageViewMapper;


  @Inject
  ClickServiceGrpc.ClickServiceStub reactiveStub;

  @Inject
  ClickServiceGrpc.ClickServiceBlockingStub blockingStub;


  /**
   * overridden grpc server base method - A server-side streaming RPC (one of 4 service methods of
   * RPC) where the client sends a request to the server and gets a stream to read a sequence of
   * messages back.
   */
  @Override
  public void userClicksByIdOnPage(final UserOnPageRequestAgg request,
      final StreamObserver<UserOnPageResponse> responseObserver) {

    userOnPage(UserOnPageRequest.newBuilder().setPageid(request.getPageid())
        .setUserid(request.getUserid()).build(), true);

    repoAvg.readAllViewTimes()
        .entrySet()
        .stream()
        .filter(input -> request.getPageid().equals(input.getValue().pageid()))
        .filter(input -> request.getUserid().equals(input.getValue().userid()))
        .forEach(input -> {

          val response = UserOnPageResponse.newBuilder()
              .setHour(input.getValue().hour()).setPageid(input.getValue().pageid())
              .setUserid(input.getValue().userid())
              .setViewtimeAvg(input.getValue().viewTimeAvg()).build();

          log.info("user {} spent {} seconds avg on on this page {} during this hour {}",
              response.getUserid(), Math.round(response.getViewtimeAvg() / 1000),
              response.getPageid(), response.getHour());

          responseObserver.onNext(response);
        });
  }

  /**
   * overridden grpc server base method - A server-side streaming RPC (one of 4 service methods of
   * RPC) where the client sends a request to the server and gets a stream to read a sequence of
   * messages back. The client reads from the returned stream until there are no more messages. As
   * you can see in our example, you specify a server-side streaming method by placing the stream
   * keyword before the response type.
   */
  @Override
  public void pagesById(final PageIdRequestAgg request,
      final StreamObserver<PageViewAgg> responseObserver) {

    pagesByIdImpl(PageIdRequest.newBuilder().setPageid(request.getPageid()).build(), true,
        responseObserver);

  }

  /**
   * overridden grpc server base method simple RPC (one of 4 service methods of RPC) A simple RPC
   * where the client sends a request to the server using the stub and waits for a response to come
   * back, just like a normal function call.
   */
  @Override
  public void pagesCountById(final PageIdRequestAgg request,
      final StreamObserver<PageIdCountResponseAgg> responseObserver) {
    pagesCounts(PageIdRequest.newBuilder().setPageid(request.getPageid()).build(),
        responseObserver);
  }

  /**
   * client function to request microstream-grpc-server asynchronous
   * 
   * @param userOnPageRequest
   * @param channel
   * @param reactive
   */
  private void userOnPage(final UserOnPageRequest userOnPageRequest,
      final Boolean reactive) {

    if (Boolean.TRUE.equals(reactive)) {
      reactiveStub.userClicksByIdOnPage(userOnPageRequest,
          callBackUserPageClicks);
    } else {
      blockingStub.userClicksByIdOnPage(userOnPageRequest);
    }
  }

  /**
   * client function to request microstream-grpc-server synchronous/asynchronous
   * 
   * @param pageIdRequest
   * @param channel
   * @param reactive
   * @param responseObserver
   */
  private void pagesByIdImpl(final PageIdRequest pageIdRequest,
      final Boolean reactive, final StreamObserver<PageViewAgg> responseObserver) {

    if (Boolean.TRUE.equals(reactive)) {
      reactiveStub.pagesById(pageIdRequest, new StreamObserver<PageView>() {
        @Override
        public void onNext(final PageView value) {
          val response =
              PageViewAgg.newBuilder().setPageid(value.getPageid()).setUserid(value.getUserid())
                  .setTimestamp(value.getTimestamp()).setViewtime(value.getViewtime()).build();
          responseObserver.onNext(response);
          log.info("Received pageview: {}", value);
        }

        @Override
        public void onError(final Throwable cause) {
          log.error("Error occurred, cause {}", cause.getMessage());
        }

        @Override
        public void onCompleted() {
          responseObserver.onCompleted();
          log.info("Finished calling async PageView Stream-API service..");
        }
      });
    } else {
      final Iterator<PageView> pageViewIterator = blockingStub.pagesById(pageIdRequest);
      log.info("counts of pageviews: {}", Iterators.size(pageViewIterator));
    }

  }

  /**
   * client function to request microstream-grpcs-server synchronous/asynchronous
   * 
   * @param pageIdRequest
   * @param channel
   * @param responseObserver
   */
  private void pagesCounts(final PageIdRequest pageIdRequest,
      final StreamObserver<PageIdCountResponseAgg> responseObserver) {
    reactiveStub.pagesCountById(pageIdRequest, new StreamObserver<PageIdCountResponse>() {

      @Override
      public void onNext(final PageIdCountResponse value) {
        responseObserver
            .onNext(
                PageIdCountResponseAgg.newBuilder().setCountPages(value.getCountPages()).build());
        log.info("Received page counts: {}", value.getCountPages());
      }

      @Override
      public void onError(final Throwable cause) {
        log.error("Error occurred, cause {}", cause.getMessage());
      }

      @Override
      public void onCompleted() {
        responseObserver.onCompleted();
        log.info("Finished calling async PageView Unary-API service..");
      }
    });
  }


}


