package orange.business.jhub;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import io.grpc.ManagedChannel;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import io.micronaut.grpc.server.GrpcServerChannel;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MicronautTest
public class TestUtil {

    @BeforeEach
    public void initEach() throws InterruptedException {
        log.info("give it some time to collect testdata");
        TimeUnit.SECONDS.sleep(30);
    }

    @Inject
    ClickAggServiceGrpc.ClickAggServiceStub asyncStub;

}


@Factory
class ClientPagesCountAsync {
    @Bean
    ClickAggServiceGrpc.ClickAggServiceStub asynStub(
            @GrpcChannel(GrpcServerChannel.NAME) final ManagedChannel channel) {
        return ClickAggServiceGrpc.newStub(channel);
    }
}
