package com.github.examples.grpc.server;

import com.proto.average.AverageRequest;
import com.proto.average.AverageResponse;
import com.proto.average.AverageServiceGrpc;
import io.grpc.stub.StreamObserver;

public class AverageServiceImpl extends AverageServiceGrpc.AverageServiceImplBase {
    @Override
    public StreamObserver<AverageRequest> average(StreamObserver<AverageResponse> responseObserver) {

        StreamObserver<AverageRequest> requestStreamObserver = new StreamObserver<AverageRequest>() {
            double sum = 0;
            int cnt = 0;

            @Override
            public void onNext(AverageRequest value) {
                sum += value.getInput();
                cnt++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AverageResponse.newBuilder()
                    .setOutput(sum / cnt)
                    .build());
                responseObserver.onCompleted();
            }
        };

        return requestStreamObserver;
    }
}
