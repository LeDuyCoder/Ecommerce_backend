package kiradev.studio.Eommerce.Enum;

public enum UserStatus {
    ACTIVE,         // Đang hoạt động bình thường
    INACTIVE,       // Không hoạt động (có thể do chưa xác thực email, chưa đăng nhập lần nào,...)
    BANNED,         // Bị khóa do vi phạm chính sách
    DELETED,        // Đã bị xóa hoặc ẩn khỏi hệ thống
    PENDING,        // Đang chờ xác thực (email, admin duyệt,...)
    SUSPENDED;       // Tạm khóa do lý do cụ thể (ví dụ: thanh toán chưa hoàn tất)

    public static UserStatus fromString(String role) {
        try {
            return UserStatus.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // or throw an exception if you prefer
        }
    }
}
