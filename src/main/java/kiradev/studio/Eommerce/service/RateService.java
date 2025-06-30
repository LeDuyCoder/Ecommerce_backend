package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.entity.Rate;
import kiradev.studio.Eommerce.repository.ProductRepository;
import kiradev.studio.Eommerce.repository.RateRepository;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.Interface.IRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RateService implements IRateService {
    private final RateRepository rateRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Autowired
    public RateService(RateRepository rateRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.rateRepository = rateRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }


    /**
     * Adds a rate for a product by a user.
     *
     * @param userId    The ID of the user adding the rate.
     * @param productId The ID of the product being rated.
     * @param rate      The rating value (1 to 5).
     * @param comment   An optional comment for the rating.
     */
    @Override
    public void addRate(UUID userId, UUID productId, int rate, String comment) {
        if(userRepository.existsById(userId) && productRepository.existsById(productId)) {
            if (rate < 1 || rate > 5) {
                throw new IllegalArgumentException("Rate must be between 1 and 5.");
            }
            // Check if the user has already rated the product
            if (rateRepository.findByUserIdAndProductId(userId, productId) != null) {
                throw new IllegalArgumentException("User has already rated this product.");
            }
            // Create and save the new rate
            Rate rateEntity = new Rate();
            rateEntity.setUserId(userId);
            rateEntity.setProductId(productId);
            rateEntity.setRate(rate);
            rateEntity.setComment(comment);
            rateEntity.setCreatedAt(Instant.now().toString());
            rateRepository.save(rateEntity);
        } else {
            throw new IllegalArgumentException("Invalid user or product ID.");

        }
    }

    /**
     * Updates an existing rate for a product by a user.
     *
     * @param userId    The ID of the user updating the rate.
     * @param productId The ID of the product being rated.
     * @param rate      The new rating value (1 to 5).
     * @param comment   An optional new comment for the rating.
     */
    @Override
    public void updateRate(UUID userId, UUID productId, int rate, String comment) {
        if (userRepository.existsById(userId) && productRepository.existsById(productId)) {
            if (rate < 1 || rate > 5) {
                throw new IllegalArgumentException("Rate must be between 1 and 5.");
            }
            // Find the existing rate
            Rate existingRate = rateRepository.findByUserIdAndProductId(userId, productId);
            if (existingRate == null) {
                throw new IllegalArgumentException("User has not rated this product yet.");
            }
            // Update the rate and comment
            existingRate.setRate(rate);
            if(comment != null) existingRate.setComment(comment);
            rateRepository.save(existingRate);
        } else {
            throw new IllegalArgumentException("Invalid user or product ID.");
        }
    }

    /**
     * Deletes a rate for a product by a user.
     *
     * @param userId    The ID of the user whose rate is to be deleted.
     * @param productId The ID of the product whose rate is to be deleted.
     */
    @Override
    public void deleteRate(UUID userId, UUID productId) {
        if (userRepository.existsById(userId) && productRepository.existsById(productId)) {
            // Find the existing rate
            Rate existingRate = rateRepository.findByUserIdAndProductId(userId, productId);
            if (existingRate == null) {
                throw new IllegalArgumentException("User has not rated this product yet.");
            }
            // Delete the rate
            rateRepository.delete(existingRate);
        } else {
            throw new IllegalArgumentException("Invalid user or product ID.");
        }
    }

    /**
     * Retrieves the average rating for a product.
     *
     * @param productId The ID of the product for which to calculate the average rating.
     * @return The average rating, or 0.0 if no ratings are available.
     */
    @Override
    public double getAverageRate(UUID productId) {
        List<Rate> listRates = rateRepository.findByproductId(productId);
        if (listRates.isEmpty()) {
            return 0.0; // No rates available
        }

        double totalRate = 0.0;
        for(Rate rate : listRates) {
            totalRate += rate.getRate();
        }

        return totalRate / listRates.size();
    }

    /**
     * Retrieves the total number of rates for a product.
     *
     * @param productId The ID of the product for which to count the rates.
     * @return The total number of rates for the product.
     */
    @Override
    public int getTotalRates(UUID productId) {
        return rateRepository.findByproductId(productId).size();
    }

    /**
     * Retrieves all rates for a specific product.
     *
     * @param productId The ID of the product for which to retrieve rates.
     * @return A list of Rate objects associated with the product.
     */
    public List<Rate> getAllRatesByProductId(UUID productId) {
        return rateRepository.findByproductId(productId);
    }

    /**
     * Retrieves a specific rate by user ID and product ID.
     *
     * @param userId    The ID of the user who rated the product.
     * @param productId The ID of the product that was rated.
     * @return The Rate object if found, or null if not found.
     */
    public Rate getRateByUserIdAndProductId(UUID userId, UUID productId) {
        return rateRepository.findByUserIdAndProductId(userId, productId);
    }
}
