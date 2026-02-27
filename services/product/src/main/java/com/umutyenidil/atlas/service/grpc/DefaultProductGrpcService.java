package com.umutyenidil.atlas.service.grpc;

import com.umutyenidil.atlas.entity.ProductVariant;
import com.umutyenidil.atlas.grpc.ProductGrpcServiceGrpc;
import com.umutyenidil.atlas.grpc.VariantValidationRequest;
import com.umutyenidil.atlas.grpc.VariantValidationResponse;
import com.umutyenidil.atlas.repository.ProductVariantRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Optional;
import java.util.UUID;

@GrpcService
public class DefaultProductGrpcService extends ProductGrpcServiceGrpc.ProductGrpcServiceImplBase {

    private final ProductVariantRepository productVariantRepository;

    public DefaultProductGrpcService(ProductVariantRepository productVariantRepository) {
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public void validateVariant(
            VariantValidationRequest request,
            StreamObserver<VariantValidationResponse> responseObserver
    ) {
        VariantValidationResponse.Builder responseBuilder = VariantValidationResponse.newBuilder();

        try {
            UUID productVariantId = UUID.fromString(request.getVariantId());

            Optional<ProductVariant> productVariantOpt = productVariantRepository.findWithProductById(productVariantId);

            if (productVariantOpt.isPresent()) {
                ProductVariant productVariant = productVariantOpt.get();

                responseBuilder.setIsAvailable(productVariant.getStockQuantity() > request.getRequestedQuantity())
                        .setCurrentPrice(productVariant.getPrice().doubleValue())
                        .setProductName(productVariant.getProduct().getName())
                        .setStockQuantity(productVariant.getStockQuantity());

            } else {
                responseBuilder.setIsAvailable(false);
            }
        } catch (IllegalArgumentException e) {
            responseBuilder.setIsAvailable(false);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
