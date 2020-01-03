package com.github.examples.grpc.client;

import com.proto.greet.GreetEveryoneRequest;
import com.proto.greet.GreetEveryoneResponse;
import com.proto.greet.GreetManyTimesRequest;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client");

        GreetingClient client = new GreetingClient();
        // Case 1: Unary request/response
        client.makeUnaryCall();
//
//
//        // Case 2: Server streaming request/response
//        client.makeServerStreamingCall();

//        // Case 3: Client streaming call
//        client.makeClientStreamingCall();

//        // Case 4: Bidirectional Streaming call
//        client.makeBidirectionalStreamingCall();

//        // Case 5: Unary call with deadline
//        client.makeUnaryCallWithDeadline();

    }

    private void makeUnaryCall() {
        GreetRequest request = GreetRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        ManagedChannel channel = getManagedChannel();

        // Enable this for ssl encryption
//        ManagedChannel channel = null;
//        try {
//            channel = NettyChannelBuilder.forAddress("localhost", 50001)
//                .sslContext(GrpcSslContexts.forClient().trustManager(new File("ssl/ca.crt")).build())
//                .build();
//        } catch (SSLException e) {
//            e.printStackTrace();
//        }

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
        channel.shutdown();
    }

    private void makeBidirectionalStreamingCall() {
        ManagedChannel channel = getManagedChannel();
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Got response from server = " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Got completed response from server");
                latch.countDown();
            }
        });

        Arrays.asList("Rohit", "Rucha").forEach(name ->
        {
            requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName(name).build())
                .build());
        });
        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.shutdown();
    }

    private void makeUnaryCallWithDeadline() {
        GreetRequest request = GreetRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        ManagedChannel channel = getManagedChannel();
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        GreetResponse response = greetClient.withDeadline(Deadline.after(100L, TimeUnit.MILLISECONDS))
            .greet(request);

        System.out.println("Response: " + response.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();
    }
}
