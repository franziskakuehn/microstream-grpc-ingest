package orange.business.jhub.factory;

import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.micronaut.context.annotation.Factory;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;
import orange.business.jhub.ClickServiceGrpc;

@Factory
class AddBeanFactory {
    @Singleton
    BindableService reflectionService() {
        return ProtoReflectionService.newInstance();
    }

    @Singleton
    ClickServiceGrpc.ClickServiceStub reactiveStub(
            @GrpcChannel("clickservice") final ManagedChannel channel) {
        return ClickServiceGrpc.newStub(channel);
    }

    @Singleton
    ClickServiceGrpc.ClickServiceBlockingStub blockingStub(
            @GrpcChannel("clickservice") final ManagedChannel channel) {
        return ClickServiceGrpc.newBlockingStub(channel);
    }
}

