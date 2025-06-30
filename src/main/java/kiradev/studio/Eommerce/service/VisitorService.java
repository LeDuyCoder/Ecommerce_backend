package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.dto.VisitorDTO;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Visitor;
import kiradev.studio.Eommerce.repository.VisitorRepository;
import kiradev.studio.Eommerce.service.Interface.IVisitorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitorService implements IVisitorService {

    private final VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }


    /**
     * Adds a visitor record for a user on a specific date.
     *
     * @param user      The user who visited.
     * @param dateTime  The date and time of the visit.
     */
    @Override
    public void AddVistor(User user, LocalDate dateTime) {

        Visitor visitorCheck = visitorRepository.findByUserAndCreatedAt(user, dateTime);

        if (visitorCheck != null) {
            return;
        }

        Visitor visitor = new Visitor();
        visitor.setUser(user);
        visitor.setCreatedAt(dateTime);

        visitorRepository.save(visitor);
    }

    /**
     * Retrieves all visitors for a specific date.
     *
     * @param date The date for which to retrieve visitors.
     * @return A list of VisitorDTO objects representing the visitors on that date.
     */
    @Override
    public List<VisitorDTO> getAllVistorsByDate(LocalDate date) {
        List<Visitor> visitors = visitorRepository.findAllByCreatedAt(date);
        if (visitors.isEmpty()) {
            return List.of(); // Return an empty list if no visitors found
        }

        List<VisitorDTO> visitorDTOs = visitors.stream()
                .map(visitor -> new VisitorDTO(visitor.getId(), visitor.getUser().getID(), visitor.getCreatedAt()))
                .toList();
        return visitorDTOs;
    }

    /**
     * Counts the number of visitors in a specific month.
     *
     * @param year  The year of the month to count visitors.
     * @param month The month to count visitors.
     * @return The count of visitors in the specified month.
     */
    @Override
    public long countVisitorsInMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return visitorRepository.findAllByCreatedAtBetween(startDate, endDate).size();
    }

    /**
     * Retrieves all visitors in a specific month.
     *
     * @param year  The year of the month to retrieve visitors.
     * @param month The month to retrieve visitors.
     * @return A list of VisitorDTO objects representing the visitors in that month.
     */
    @Override
    public List<VisitorDTO> getVisitorsInMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return visitorRepository.findAllByCreatedAtBetween(startDate, endDate).stream()
                .map(visitor -> new VisitorDTO(visitor.getId(), visitor.getUser().getID(), visitor.getCreatedAt()))
                .toList();
    }

    /**
     * Retrieves all visitors in a specific year.
     *
     * @param year The year to retrieve visitors.
     * @return A list of VisitorDTO objects representing the visitors in that year.
     */
    public List<VisitorDTO> getVisitorsByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        return visitorRepository.findAllByCreatedAtBetween(startDate, endDate).stream()
                .map(visitor -> new VisitorDTO(visitor.getId(), visitor.getUser().getID(), visitor.getCreatedAt()))
                .toList();
    }

    /**
     * Gets the total number of visitors.
     *
     * @return The total count of visitors.
     */
    public long getTotalVisitors() {
        return visitorRepository.count();
    }
}
