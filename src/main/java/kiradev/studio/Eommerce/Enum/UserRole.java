package kiradev.studio.Eommerce.Enum;

public enum UserRole {
    USER,
    ADMIN,
    SHOPKEEPER;

    public static UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}

