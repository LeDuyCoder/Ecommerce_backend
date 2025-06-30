package kiradev.studio.Eommerce.dto;


import kiradev.studio.Eommerce.Enum.MethodApply;
import kiradev.studio.Eommerce.Enum.MethodReduce;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VoucherDTO {
    private String code;
    private String description;
    private double discountAmount;
    private LocalDate expirationDate;
    private double minimumOrderValue;
    private MethodReduce methodReduce;
    private MethodApply methodApply;

}


