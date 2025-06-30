package kiradev.studio.Eommerce.Enum;

public enum MethodApply {
    SHIPPING,
    DISCOUNT;

    public static MethodApply fromString(String method) {
        try {
            return MethodApply.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}
