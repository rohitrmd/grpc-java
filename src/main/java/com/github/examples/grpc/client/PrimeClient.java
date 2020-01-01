package com.github.examples.grpc.client;

import com.proto.prime.PrimeRequest;
import com.proto.prime.PrimeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class PrimeClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client for prime factor service");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();

        PrimeServiceGrpc.PrimeServiceBlockingStub primeClient = PrimeServiceGrpc.newBlockingStub(channel);

        PrimeRequest request = PrimeRequest.newBuilder().setInput(54).build();

        primeClient.prime(request)
            .forEachRemaining(primeResponse -> {
                System.out.println(primeResponse.getResult());
            });

        System.out.println("Shutting down the client");
        channel.shutdown();

    }
}
