package com.github.examples.grpc.server;

import com.proto.greet.GreetDeadlineRequest;
import com.proto.greet.GreetDeadlineResponse;
import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetManyTimesResponse;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetResponseOrBuilder;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.Context;
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

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        StreamObserver<LongGreetRequest> requestStreamObserver = new StreamObserver<LongGreetRequest>() {

            StringBuilder sb = new StringBuilder();

            @Override
            public void onNext(LongGreetRequest value) {
                sb.append("Hello " + value.getGreeting().getFirstName() + "!");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(LongGreetResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestStreamObserver = new StreamObserver<GreetEveryoneRequest>() {

            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName();
                responseObserver.onNext(GreetEveryoneResponse.newBuilder().setResult(result).build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }

    @Override
    public void greetWithDeadline(GreetDeadlineRequest request, StreamObserver<GreetDeadlineResponse> responseObserver) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Context context = Context.current();
        if(context.isCancelled()) {
            System.out.println("Request is cancelled");
            return;
        }

        responseObserver.onNext(
                GreetDeadlineResponse.newBuilder()
                    .setResult("Hello "+ request.getGreeting().getFirstName())
                    .build()
            );
        responseObserver.onCompleted();
    }
}
