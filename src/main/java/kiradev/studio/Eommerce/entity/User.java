package kiradev.studio.Eommerce.entity;

import jakarta.persistence.*;
import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.Enum.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;
    private String name;

    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String password;

    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
    private String phone;
    private String address;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long moneyConsumed;
    private String CCCD;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    private String createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
