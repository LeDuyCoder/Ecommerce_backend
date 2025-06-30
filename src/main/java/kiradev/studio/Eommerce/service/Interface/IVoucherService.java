package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.Enum.MethodApply;
import kiradev.studio.Eommerce.Enum.MethodReduce;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Voucher;

import java.time.LocalDate;
import java.util.List;

public interface IVoucherService {
    void CreateVoucher(User user, String code, String description, double discountAmount, LocalDate expirationDate, double minimumOrderValue, MethodReduce methodReduce, MethodApply methodApply);
    void UpdateDescriptionVoucher(String code, String description);
    void UpdateDiscountAmountVoucher(String code, double discountAmount);
    void UpdateMinimumOrderValueVoucher(String code, double minimumOrderValue);
    void UpdateExpirationDateVoucher(String code, LocalDate expirationDate);
    void UpdateMethodReduceVoucher(String code, MethodReduce methodReduce);
    void UpdateMethodApplyVoucher(String code, MethodApply methodApply);
    Voucher getVoucherByCode(String code);
    List<Voucher> getAllVouchers();
    void deleteVoucher(String code);
    boolean isVoucherValid(String code);
    List<Voucher> getVouchersExpiredDate();
    List<Voucher> getVouchersNotExpiredDate();
}
