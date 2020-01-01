package com.github.examples.grpc.client;

import com.proto.average.AverageRequest;
import com.proto.average.AverageResponse;
import com.proto.average.AverageServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AverageClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client for calculating average service");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();

        AverageServiceGrpc.AverageServiceStub asyncClient = AverageServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> responseStreamObserver = asyncClient.average(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("Average = " + value.getOutput());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        responseStreamObserver.onNext(AverageRequest.newBuilder().setInput(400).build());
        responseStreamObserver.onNext(AverageRequest.newBuilder().setInput(5).build());
        responseStreamObserver.onNext(AverageRequest.newBuilder().setInput(6).build());
        responseStreamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        channel.shutdown();
    }
}
