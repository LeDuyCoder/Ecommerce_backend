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

    @Override
    public int getTotalRates(UUID productId) {
        return rateRepository.findByproductId(productId).size();
    }

    public List<Rate> getAllRatesByProductId(UUID productId) {
        return rateRepository.findByproductId(productId);
    }

    public Rate getRateByUserIdAndProductId(UUID userId, UUID productId) {
        return rateRepository.findByUserIdAndProductId(userId, productId);
    }
}
