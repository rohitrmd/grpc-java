package com.github.examples.grpc.server;

import com.proto.calculator.CalRequest;
import com.proto.calculator.CalResponse;
import com.proto.calculator.CalServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalServiceImpl extends CalServiceGrpc.CalServiceImplBase {
    @Override
    public void calculate(CalRequest request, StreamObserver<CalResponse> responseObserver) {
        // Extract fields from request and calculate result
        int result = request.getCalInput().getInput1() + request.getCalInput().getInput2();

        CalResponse response = CalResponse.newBuilder()
            .setResult(result)
            .build();

        // Set response
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
