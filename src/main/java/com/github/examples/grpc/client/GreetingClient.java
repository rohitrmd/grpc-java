package com.github.examples.grpc.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
    public static void main(String[] args) {
        System.out.println("Starting gRPC client");

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50001)
            .usePlaintext()
            .build();

        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        Greeting greeting = Greeting.newBuilder()
            .setFirstName("Rohit")
            .setLastName("Deshpande")
            .build();

        GreetRequest request = GreetRequest.newBuilder()
            .setGreeting(greeting)
            .build();

        GreetResponse response = greetClient.greet(request);

        System.out.println("Response: " + response.getResult());

        System.out.println("Shutting down channel");
        channel.shutdown();

    }
}
