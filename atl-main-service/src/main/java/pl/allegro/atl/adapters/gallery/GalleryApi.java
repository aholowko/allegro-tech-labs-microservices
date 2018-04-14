package pl.allegro.atl.adapters.gallery;

import java.util.concurrent.CompletableFuture;

public interface GalleryApi {

    CompletableFuture<Gallery> findGalleryForOffer(String offerId);
}

