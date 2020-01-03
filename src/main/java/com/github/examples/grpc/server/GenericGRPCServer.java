package com.github.examples.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GenericGRPCServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting gRPC server!");

        Server server = ServerBuilder.forPort(50001)
            .addService(new GreetServiceImpl())
            .addService(new CalServiceImpl())
            .addService(new PrimeServiceImpl())
            .addService(new AverageServiceImpl())
//            .useTransportSecurity(          // Enable this for ssl encryption
//                new File("ssl/server.crt"),
//                new File("ssl/server.pem")
//            )
            .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("Receiving shutdown request");
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
