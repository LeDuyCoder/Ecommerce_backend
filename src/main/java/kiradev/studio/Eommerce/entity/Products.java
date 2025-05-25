package kiradev.studio.Eommerce.entity;

import jakarta.persistence.*;
import kiradev.studio.Eommerce.Enum.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productID;
    @Column(nullable = false)
    private UUID shopID;
    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private byte[] image;
    private String name;
    private String description;
    private double price;
    private int stock;
    @Enumerated(EnumType.STRING)
    private ProductStatus status;
    private int sold;
    @ManyToMany
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    private String createdAt;
}
