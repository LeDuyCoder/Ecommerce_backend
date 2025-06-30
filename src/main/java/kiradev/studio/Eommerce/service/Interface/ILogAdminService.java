package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.dto.LogAdminDTO;
import kiradev.studio.Eommerce.entity.LogAdmin;
import kiradev.studio.Eommerce.entity.User;

import java.util.List;

public interface ILogAdminService {
    void addLog(User userAdmin, String action);
    List<LogAdminDTO> getAllLogs();
    List<LogAdminDTO> getLogsByUserAdmin(User userAdmin);
}
