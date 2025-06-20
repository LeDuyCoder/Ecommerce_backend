package kiradev.studio.Eommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "orderitems")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "orderid", referencedColumnName = "id", nullable = false)
    @JsonIgnore
    private Order order;

    @ManyToOne
    @JoinColumn(name = "productID", referencedColumnName = "productID", nullable = false)
    @JsonIgnore
    private Products products;

    @Column(nullable = false, columnDefinition = "int default 1")
    private int quantity;
    @Column(nullable = false)
    private float price;

}
