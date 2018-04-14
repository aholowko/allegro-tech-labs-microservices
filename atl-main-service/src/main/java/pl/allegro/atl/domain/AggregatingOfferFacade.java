package pl.allegro.atl.domain;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.allegro.atl.adapters.description.DescriptionApi;
import pl.allegro.atl.adapters.gallery.GalleryApi;
import pl.allegro.atl.adapters.mongodb.CoreOffer;
import pl.allegro.atl.adapters.mongodb.CoreOfferRepository;

@Component
class AggregatingOfferFacade implements OfferFacade {

    private final CoreOfferRepository offerRepository;
    private final GalleryApi galleryApi;
    private final DescriptionApi descriptionApi;

    AggregatingOfferFacade(CoreOfferRepository offerRepository, GalleryApi galleryApi, DescriptionApi descriptionApi) {
        this.offerRepository = offerRepository;
        this.galleryApi = galleryApi;
        this.descriptionApi = descriptionApi;
    }

    @Override
    @Async("mainPool")
    public CompletableFuture<Offer> findById(String id) {
        final CoreOffer coreOffer = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException(id));
        CompletableFuture.allOf(
                descriptionApi.findDescriptionForOffer(id),
                galleryApi.findGalleryForOffer(id))
                .join();
        return CompletableFuture.completedFuture(new BackingOffer.Builder(coreOffer)
                .withDescription(descriptionApi.findDescriptionForOffer(id).join())
                .withGallery(galleryApi.findGalleryForOffer(id).join())
                .build());
    }
}
