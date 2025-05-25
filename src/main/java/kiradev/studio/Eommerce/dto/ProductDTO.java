package kiradev.studio.Eommerce.dto;

import kiradev.studio.Eommerce.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private String name;
    private String description;
    private double price;
    private int stock;
    private List<String> categories;
}
