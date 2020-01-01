package com.github.examples.grpc.client;

import com.proto.calculator.CalInput;
import com.proto.calculator.CalMaxRequest;
import com.proto.calculator.CalMaxResponse;
import com.proto.calculator.CalRequest;
import com.proto.calculator.CalResponse;
import com.proto.calculator.CalServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client for calculator service");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();

        // Case 1 : Calculating Sum
        CalServiceGrpc.CalServiceBlockingStub calStub = CalServiceGrpc.newBlockingStub(channel);

        CalInput input = CalInput.newBuilder()
            .setInput1(5)
            .setInput2(3)
            .build();

        CalRequest request = CalRequest.newBuilder()
            .setCalInput(input)
            .build();

        CalResponse response = calStub.calculate(request);

        System.out.println("Sum calculated: " + response.getResult());

        // Case 2: Calculating max in a stream of numbers
        CountDownLatch latch = new CountDownLatch(1);

        CalServiceGrpc.CalServiceStub asyncClient = CalServiceGrpc.newStub(channel);
        StreamObserver<CalMaxRequest> requestStreamObserver = asyncClient.maximum(new StreamObserver<CalMaxResponse>() {
            @Override
            public void onNext(CalMaxResponse value) {
                System.out.println("Max value returned from server = " + value.getOutput());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed the computation.");
                latch.countDown();
            }
        });


        Arrays.asList(130, 400, 100).forEach(number -> {
            requestStreamObserver.onNext(CalMaxRequest.newBuilder().setInput(number).build());
        });
        requestStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Shuttind down the client");
        channel.shutdown();

    }
}
