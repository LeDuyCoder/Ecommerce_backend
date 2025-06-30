package kiradev.studio.Eommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import kiradev.studio.Eommerce.Enum.MethodApply;
import kiradev.studio.Eommerce.Enum.MethodReduce;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private double discountAmount;
    @Column(nullable = false)
    private double minimumOrderValue;
    @Column(nullable = false)
    private LocalDate expiryDate;
    @Enumerated(EnumType.STRING)
    private MethodReduce methodReduce;
    @Enumerated(EnumType.STRING)
    private MethodApply methodApply;

    @ManyToOne
    @JoinColumn(name = "userID", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private User user;

}
