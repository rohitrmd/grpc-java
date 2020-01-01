package com.github.examples.grpc.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        Greeting greeting = request.getGreeting();

        // Extract fields from request
        String result = "Hello " + greeting.getFirstName();

        // Create response object
        GreetResponse response = GreetResponse.newBuilder().setResult(result).build();

        // Return the response
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
}
