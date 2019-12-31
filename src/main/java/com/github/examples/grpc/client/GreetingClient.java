package com.github.examples.grpc.client;

import com.proto.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();

        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        System.out.println("Shutting down channel");
        channel.shutdown();

    }
}
