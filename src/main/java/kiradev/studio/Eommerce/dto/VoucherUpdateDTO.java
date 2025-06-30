package kiradev.studio.Eommerce.dto;

import kiradev.studio.Eommerce.Enum.MethodApply;
import kiradev.studio.Eommerce.Enum.MethodReduce;
import lombok.Getter;


@Getter
public class VoucherUpdateDTO {
    private MethodApply methodApply;
    private MethodReduce methodReduce;
    private double discountAmount;
}
