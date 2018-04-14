package pl.allegro.atl.domain;

import java.util.concurrent.CompletableFuture;

public interface OfferFacade {
    CompletableFuture<Offer> findById(String id);
}
