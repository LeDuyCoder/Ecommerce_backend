package kiradev.studio.Eommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Getter
@Setter
public class BankDTO {
    private String owner;
    private String numberCard;
    private String bankName;
    private String address;
    private String cvv;
    private String zipcode;
}
