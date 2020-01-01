package com.github.examples.grpc.server;

import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetManyTimesResponse;
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

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        Greeting greeting = request.getGreeting();

        try {
            for (int i = 0; i < 10; i++) {
                // Extract fields from request
                String result = "Hello " + greeting.getFirstName() + " :" + i;
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();

                responseObserver.onNext(response);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }
}
