package kiradev.studio.Eommerce.dto;

import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.Enum.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private PaymentMethod paymentMethod;
    private OrderStatus orderStatus;
    private float totalPrice;
}
