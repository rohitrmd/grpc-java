package com.github.examples.grpc.server;

import com.proto.calculator.CalMaxRequest;
import com.proto.calculator.CalMaxResponse;
import com.proto.calculator.CalRequest;
import com.proto.calculator.CalResponse;
import com.proto.calculator.CalServiceGrpc;
import com.proto.calculator.SqRootRequest;
import com.proto.calculator.SqRootResponse;
import io.grpc.Status;
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

    @Override
    public StreamObserver<CalMaxRequest> maximum(StreamObserver<CalMaxResponse> responseObserver) {
        StreamObserver<CalMaxRequest> request =  new StreamObserver<CalMaxRequest>() {
            int max = Integer.MIN_VALUE;

            @Override
            public void onNext(CalMaxRequest value) {
                max = Math.max(value.getInput(), max);
                responseObserver.onNext(CalMaxResponse.newBuilder().setOutput(max).build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return request;
    }

    @Override
    public void sqRoot(SqRootRequest request, StreamObserver<SqRootResponse> responseObserver) {
        if(request.getNumber()<0) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                .withDescription("Number provided is negative.")
                .asException()
            );
        } else {
            responseObserver.onNext(
                SqRootResponse.newBuilder().setResult(Math.sqrt(request.getNumber())).build()
            );
            responseObserver.onCompleted();
        }
    }
}
