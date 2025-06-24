package kiradev.studio.Eommerce.Enum;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    WAITING("Waiting"),
    SUCCESS("Success"),
    FAILED("Failed");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

}
