package com.github.examples.grpc.server;

import com.proto.prime.PrimeRequest;
import com.proto.prime.PrimeResponse;
import com.proto.prime.PrimeServiceGrpc;
import io.grpc.stub.StreamObserver;

public class PrimeServiceImpl extends PrimeServiceGrpc.PrimeServiceImplBase {
    @Override
    public void prime(PrimeRequest request, StreamObserver<PrimeResponse> responseObserver) {

        // Extract input from the request
        int n = request.getInput();
        int k = 2;

        while(n > 1 && k <=n) {
            if(n % k == 0) {
                responseObserver.onNext(PrimeResponse.newBuilder().setResult(k).build());
                n = n/k;
            } else {
                k++;
            }
        }
        responseObserver.onCompleted();

    }
}
