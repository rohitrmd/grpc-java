package com.github.examples.grpc.client;

import com.proto.calculator.CalInput;
import com.proto.calculator.CalRequest;
import com.proto.calculator.CalResponse;
import com.proto.calculator.CalServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client for calculator service");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();

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
        System.out.println("Shuttind down the client");
        channel.shutdown();

    }
}
