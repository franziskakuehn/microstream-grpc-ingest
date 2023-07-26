package orange.business.jhub;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Inject;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import io.micronaut.grpc.server.GrpcServerChannel;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;


@Slf4j
@MicronautTest
public class TestUtil {

@Inject ClickServiceGrpc.ClickServiceStub asyncStub;

@Inject ClickServiceGrpc.ClickServiceBlockingStub blockingStub;


  @BeforeEach
  public void initEach() throws InterruptedException {
    val timeout = 30;
    log.info("give it {} seconds time to collect testdata", timeout);
    TimeUnit.SECONDS.sleep(30);
  }
    
}

@Factory
class ClientAsync {
  @Bean
  ClickServiceGrpc.ClickServiceStub asyncStub(
      @GrpcChannel(GrpcServerChannel.NAME) final ManagedChannel channel) {
    return ClickServiceGrpc.newStub(channel);
  }
}

@Factory
class Clients {
  @Bean
  ClickServiceGrpc.ClickServiceBlockingStub blockingStub(
      @GrpcChannel(GrpcServerChannel.NAME) final ManagedChannel channel) {
    return ClickServiceGrpc.newBlockingStub(channel);
  }
}

