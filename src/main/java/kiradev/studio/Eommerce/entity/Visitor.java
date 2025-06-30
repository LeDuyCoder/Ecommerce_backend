package kiradev.studio.Eommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "visitors")
public class Visitor {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User user;

    private LocalDate createdAt;
}
