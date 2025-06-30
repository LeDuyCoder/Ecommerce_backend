package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.dto.VisitorDTO;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Visitor;

import java.time.LocalDate;
import java.util.List;

public interface IVisitorService {
    void AddVistor(User user, LocalDate dateTime);
    List<VisitorDTO> getAllVistorsByDate(LocalDate date);
    long countVisitorsInMonth(int year, int month);
    List<VisitorDTO> getVisitorsInMonth(int year, int month);
}
