package com.github.examples.grpc.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client");

        GreetingClient client = new GreetingClient();
//        // Case 1: Unary request/response
//        client.makeUnaryCall();
//
//
//        // Case 2: Server streaming request/response
//        client.makeServerStreamingCall();

        // Case 3: Client streaming call
        client.makeClientStreamingCall();

    }

    private void makeUnaryCall() {
        GreetRequest request = GreetRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        ManagedChannel channel = getManagedChannel();
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetResponse response = greetClient.greet(request);

        System.out.println("Response: " + response.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private static Greeting greeting = Greeting.newBuilder()
        .setFirstName("Rohit")
        .setLastName("Deshpande")
        .build();

    private ManagedChannel getManagedChannel() {
        return ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();
    }

    private void makeServerStreamingCall() {
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        ManagedChannel channel = getManagedChannel();
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        greetClient.greetManyTimes(request)
            .forEachRemaining(greetManyTimesResponse -> {
                System.out.println(greetManyTimesResponse.getResult());
            });

        System.out.println("Shutting down channel");
        channel.shutdown();
    }

    private void makeClientStreamingCall() {
        ManagedChannel channel = getManagedChannel();
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println("Received a response from the server.");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending the response.");
                latch.countDown();
            }
        });



        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(greeting)
            .build());
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(greeting)
            .build());
        requestObserver.onNext(LongGreetRequest.newBuilder()
            .setGreeting(greeting)
            .build());

        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
