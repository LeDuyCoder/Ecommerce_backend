package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.User;

public interface IUserService {
    User registerUser(String password, String email);
    User loginUser(String usernameOrEmail, String password);
    boolean isMailExist(String email);
    boolean hasPermission(User user, UserRole role);
}
