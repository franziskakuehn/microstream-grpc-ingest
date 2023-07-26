package orange.business.jhub.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.stub.StreamObserver;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.OffsetReset;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import orange.business.jhub.ClickServiceGrpc;
import orange.business.jhub.PageIdCountResponse;
import orange.business.jhub.PageIdRequest;
import orange.business.jhub.PageView;
import orange.business.jhub.UserOnPageRequest;
import orange.business.jhub.mapper.ClickMapper;
import orange.business.jhub.repo.ClickRepositoryImpl;

@Slf4j
@GrpcService
@Singleton
@KafkaListener(offsetReset = OffsetReset.LATEST)
public class ClickService extends ClickServiceGrpc.ClickServiceImplBase {
  @Inject
  ClickMapper mapper;

  @Inject
  ClickRepositoryImpl clickRepository;

  /**
   * A server-side streaming RPC (one of 4 service methods of RPC) where the client sends a request
   * to the server and gets a stream to read a sequence of messages back. The client reads from the
   * returned stream until there are no more messages. As you can see in our example, you specify a
   * server-side streaming method by placing the stream keyword before the response type.
   */
  @Override
  public void pagesById(
      final PageIdRequest request, final StreamObserver<PageView> responseObserver) {
    clickRepository
        .readAllPages()
        .filter(input -> input.getPageid().equals(request.getPageid()))
        .forEach(responseObserver::onNext);

    responseObserver.onCompleted();

    log.info("Finished calling PagesById-Stream-API service..");
  }

  /**
   * overridden server base method defined by proto definition simple RPC (one of 4 service methods
   * of RPC) A simple RPC where the client sends a request to the server using the stub and waits
   * for a response to come back, just like a normal function call.
   */
  @Override
  public void pagesCountById(
      final PageIdRequest request, StreamObserver<PageIdCountResponse> responseObserver) {
    val count =
        clickRepository
            .readAllPages()
            .filter(input -> input.getPageid().equals(request.getPageid()))
            .count();

    val pageIdCountResponse =
        PageIdCountResponse.newBuilder().setCountPages(count).build();
    responseObserver.onNext(pageIdCountResponse);
    responseObserver.onCompleted();

    log.info("Finished calling PagesCountById Unary-API service..");
  }

  /**
   * overridden server base method defined by proto definition A server-side streaming RPC (one of 4
   * service methods of RPC) where the client sends a request to the server and gets a stream to
   * read a sequence of messages back.
   */
  @Override
  public void userClicksByIdOnPage(
      final UserOnPageRequest request, final StreamObserver<PageView> responseObserver) {
    clickRepository
        .readAllPages()
        .filter(input -> input.getPageid().equals(request.getPageid()))
        .filter(input -> input.getUserid().equals(request.getUserid()))
        .forEach(responseObserver::onNext);

    responseObserver.onCompleted();

    log.info("Finished calling UserClicksByIdOnPage-Stream-API service..");
  }

  /**
   * The kafka listener is subscribing to a topic called pageviews
   * 
   * @param key
   * @param value
   */
  @Topic("pageviews")
  public void receive(@KafkaKey final String key, final String value) {
    log.debug("Received record: {} {}", key, value);
    try {
      clickRepository.savePageView(mapper.map(System.currentTimeMillis(), value));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getCause());
    }
  }
}

@Factory
class ReflectionFactory {
    @Singleton
    BindableService reflectionService() {
        return ProtoReflectionService.newInstance();
    }
}
