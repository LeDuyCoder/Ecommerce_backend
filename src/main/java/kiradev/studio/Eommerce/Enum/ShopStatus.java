package kiradev.studio.Eommerce.Enum;

public enum ShopStatus {
    OPEN,
    CLOSED,
    UNDER_MAINTENANCE,
    BANNED;

    public static ShopStatus fromString(String status) {
        try {
            return ShopStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}
