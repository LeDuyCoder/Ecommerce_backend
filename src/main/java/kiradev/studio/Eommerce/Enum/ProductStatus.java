package kiradev.studio.Eommerce.Enum;

public enum ProductStatus {
    AVAILABLE,
    OUT_OF_STOCK,
    DISCONTINUED,
    WARNING,
    HIDDEN,
    BANNED;

    public static ProductStatus fromString(String status) {
        try {
            return ProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}
