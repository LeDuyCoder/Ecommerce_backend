package kiradev.studio.Eommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "Banks")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(nullable = false)
    private UUID userID;
    @Column(nullable = false)
    private String Bank;
    @Column(nullable = false)
    private String numberCard;
    @Column(nullable = false)
    private String owner;
    private String CVV;
    private String zipCode;
    private String address;

}
