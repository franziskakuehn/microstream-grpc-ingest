## Micronaut 3.7.1 Documentation

- [User Guide](https://docs.micronaut.io/3.7.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.7.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.7.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)

---

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

## Feature microstream documentation

- [Micronaut MicroStream documentation](https://micronaut-projects.github.io/micronaut-microstream/latest/guide)

- [https://microstream.one/](https://microstream.one/)

## gRPC

- [Introduction](https://grpc.io/docs/what-is-grpc/introduction/)
- [Core concept](https://grpc.io/docs/what-is-grpc/core-concepts/)
- [tutorials](https://grpc.io/docs/languages/java/)
- [basictutorial in bitbucket](https://grpc.io/docs/languages/java/basics/)
- git clone ssh://git@git02.unbelievable-machine.net:7999/jav/simplegrpcbasictutorial.git


- for eventhandling- simplest rpc call is implemented.
  > simple RPC (one of 4 service methods of RPC)
  > A simple RPC where the client sends a request to
  > the server using the stub and waits for a response to come back, just like a normal function call.


  > A server-side streaming RPC (one of 4 service methods of RPC)
  > the client sends a request to the server and gets a stream to read a sequence of messages back.
  > The client reads from the returned stream until there are no more messages.

### Multi-module-setup

This project contains of three submodules as a Maven multi-module-project

- jhub-grpc-stack
    - ./mvnw install
    - contains the gRPC protobuf definition for
        - message type definition
        - service definition for eventhandling
    - Maven definition for class generation out of protobuf-grpc definition
    - generated gRPC classes
        - ServiceBaseDefinitions ( ClickServiceGrpc.ClickServiceImplBase ) (gRPC-server)
            - with generated service implementation out of protobuf definition
            - ClickServiceGrpc.ClickServiceImplBase
            ```        
                public void pagesById(orange.business.jhub.PageIdRequest request, io.grpc.stub.StreamObserver<orange.business.jhub.PageView> responseObserver)

                public void userClicksByIdOnPage(orange.business.jhub.UserOnPageRequest request,
                io.grpc.stub.StreamObserver<orange.business.jhub.PageView> responseObserver)  
            ```
          > On the server side, the server implements the methods declared by the service and
          > runs a gRPC server to handle client calls.
        - generated synchronous and asynchronous Stub implementation (gRPC-client)
            - ClickServiceGrpc.ClickServiceBlockingStub or ClickServiceGrpc.ClickServiceStub
            ```
            public orange.business.jhub.PageIdCountResponse pagesCountById(orange.business.jhub.PageIdRequest request)

            public java.util.Iterator<orange.business.jhub.PageView> pagesById(orange.business.jhub.PageIdRequest request)
            ```
          > On the client side, the client has a local object known as stub that implements the same methods
          > as the service. The methods wrap the parameters for the call in the appropriate protocol buffer
          > message type, send the requests to the server, and return the server’s protocol buffer responses
        - 
- microstream-grpc-server

  ```mn create-app orange.business.jhub.microstream-grpc-server --features=microstream,serialization-jackson,micronaut-validation,grpc,kafka --build=maven --lang=java ```
  
    - Microservice stores ingested raw data from Kafka topic into Microstream
    - Micronaut application along with Microstream definìtion
    - Micronaut application along with gRPC server provisioning
    - asks the server sth:

    ```grpcurl -d  '{"pageid":"Page_81", "userid":"User_2"}' -plaintext 127.0.0.1:5050 ClickService.UserClicksByIdOnPage```

- microstream-grpc-client
  - Client asks grpc-server to gather rawdata over gRPC endpoint, stores some aggregates and provides data over gRPC API definition as well
  - Client is implemented as server as well for unlimited request possibility

  ```mn create-app orange.business.jhub.microstream-grpc-client --features=microstream,serialization-jackson,micronaut-validation,grpc --build=maven --lang=java```



### HowTo start the microstream-service

```
./mvnw install (this starts the docker-compose network for confluent platform and is installing datagen connector during test-compile)

./mvnw mn:run -pl microstream-grpc-server

./mvnw  exec:java -pl microstream-grpc-client

ask the client sth:

grpcurl  -d  '{"pageid":"Page_81"}' -plaintext 127.0.0.1:50051 ClickAggService.PagesCountById

grpcurl  127.0.0.1:50051 describe ClickAggService

````

to debug it as java app in your ide add the folllowing jvm-option:
```
java --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED -jar microstream-grpc-client/target/microstream-grpc-client-0.1-SNAPSHOT.jar
```
