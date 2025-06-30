package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.dto.LogAdminDTO;
import kiradev.studio.Eommerce.entity.LogAdmin;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.LogAdminRepository;
import kiradev.studio.Eommerce.service.Interface.ILogAdminService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LogAdminService implements ILogAdminService {
    private final LogAdminRepository logAdminRepository;

    public LogAdminService(LogAdminRepository logAdminRepository) {
        this.logAdminRepository = logAdminRepository;
    }

    /**
     * Adds a log entry for an admin action.
     *
     * @param userAdmin The admin user who performed the action.
     * @param action    The action performed by the admin.
     */
    @Override
    public void addLog(User userAdmin, String action) {
        LogAdmin logAdmin = new LogAdmin();
        logAdmin.setUser(userAdmin);
        logAdmin.setLog(action);
        logAdmin.setCreatedAt(Instant.now().toString());
        logAdminRepository.save(logAdmin);
    }

    /**
     * Retrieves all log entries.
     *
     * @return A list of all log entries.
     */
    @Override
    public List<LogAdminDTO> getAllLogs() {
        List<LogAdmin> logs = logAdminRepository.findAll();
        return logs.stream()
                .map(log -> new LogAdminDTO(log.getId(), log.getUser().getID(), log.getLog(), log.getCreatedAt()))
                .toList();
    }

    /**
     * Retrieves log entries for a specific admin user.
     *
     * @param userAdmin The admin user whose logs are to be retrieved.
     * @return A list of log entries for the specified admin user.
     */
    @Override
    public List<LogAdminDTO> getLogsByUserAdmin(User userAdmin) {
        List<LogAdmin> logs = logAdminRepository.findByUser(userAdmin);
        return logs.stream()
                .map(log -> new LogAdminDTO(log.getId(), log.getUser().getID(), log.getLog(), log.getCreatedAt()))
                .toList();
    }
}
