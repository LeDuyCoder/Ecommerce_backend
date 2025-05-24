package kiradev.studio.Eommerce.entity;

import jakarta.persistence.*;
import kiradev.studio.Eommerce.Enum.ShopStatus;
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
@Table(name = "Shops")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private UUID userID;
    private String name;
    private String description;
    private ShopStatus status;
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] logo;
    private String createdAt;
    private String bank;
    private String numberCard;
    private String owner;

}
