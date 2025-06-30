package kiradev.studio.Eommerce.Enum;

public enum MethodReduce {
    PERSENT,
    MONEY;

    public static MethodReduce fromString(String method) {
        try {
            return MethodReduce.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}
