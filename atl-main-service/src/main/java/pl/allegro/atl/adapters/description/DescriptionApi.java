package pl.allegro.atl.adapters.description;

import java.util.concurrent.CompletableFuture;

public interface DescriptionApi {

    CompletableFuture<Description> findDescriptionForOffer(String offerId);
}

