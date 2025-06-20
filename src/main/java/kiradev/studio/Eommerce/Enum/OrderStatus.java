package kiradev.studio.Eommerce.Enum;

public enum OrderStatus {
    PENDING,
    PAYMENT_PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED,
    SUCCESSFUL;

    public static OrderStatus fromString(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }

}
