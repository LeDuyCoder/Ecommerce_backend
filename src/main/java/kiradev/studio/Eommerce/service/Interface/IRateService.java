package kiradev.studio.Eommerce.service.Interface;

import java.util.UUID;

public interface IRateService {
    void addRate(UUID userId, UUID productId, int rate, String comment);
    void updateRate(UUID userId, UUID productId, int rate, String comment);
    void deleteRate(UUID userId, UUID productId);
    double getAverageRate(UUID productId);
    int getTotalRates(UUID productId);
}
