package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.Enum.MethodApply;
import kiradev.studio.Eommerce.Enum.MethodReduce;
import kiradev.studio.Eommerce.dto.VoucherUpdateDTO;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Voucher;
import kiradev.studio.Eommerce.repository.VoucherRepository;
import kiradev.studio.Eommerce.service.Interface.IVoucherService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VoucherService implements IVoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    /**
     * Creates a new voucher for a user.
     *
     * @param user              The user for whom the voucher is created.
     * @param code              The unique code for the voucher.
     * @param description       A description of the voucher.
     * @param discountAmount    The amount of discount provided by the voucher.
     * @param expirationDate    The expiration date of the voucher.
     * @param minimumOrderValue The minimum order value required to use the voucher.
     * @param methodReduce      The method of reduction applied by the voucher.
     * @param methodApply       The method of application for the voucher.
     */
    @Override
    public void CreateVoucher(User user, String code, String description, double discountAmount, LocalDate expirationDate, double minimumOrderValue, MethodReduce methodReduce, MethodApply methodApply) {
        Voucher voucher = new Voucher();
        voucher.setCode(code);
        voucher.setDescription(description);
        voucher.setUser(user);
        voucher.setExpiryDate(expirationDate);
        voucher.setDiscountAmount(discountAmount);
        voucher.setMethodApply(methodApply);
        voucher.setMethodReduce(methodReduce);
        voucher.setMinimumOrderValue(minimumOrderValue);

        voucherRepository.save(voucher);
    }

    /**
     * Updates the description of an existing voucher.
     *
     * @param code        The unique code of the voucher to be updated.
     * @param description The new description for the voucher.
     */
    @Override
    public void UpdateDescriptionVoucher(String code, String description) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucher.setDescription(description);
        voucherRepository.save(voucher);
    }

    /**
     * Updates the discount amount of an existing voucher.
     *
     * @param code            The unique code of the voucher to be updated.
     * @param discountAmount  The new discount amount for the voucher.
     */
    @Override
    public void UpdateDiscountAmountVoucher(String code, double discountAmount) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucher.setDiscountAmount(discountAmount);
        voucherRepository.save(voucher);
    }

    /**
     * Updates the minimum order value required to use an existing voucher.
     *
     * @param code                The unique code of the voucher to be updated.
     * @param minimumOrderValue   The new minimum order value for the voucher.
     */
    @Override
    public void UpdateMinimumOrderValueVoucher(String code, double minimumOrderValue) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucher.setMinimumOrderValue(minimumOrderValue);
        voucherRepository.save(voucher);
    }

    /**
     * Updates the expiration date of an existing voucher.
     *
     * @param code            The unique code of the voucher to be updated.
     * @param expirationDate  The new expiration date for the voucher.
     */
    @Override
    public void UpdateExpirationDateVoucher(String code, LocalDate expirationDate) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucher.setExpiryDate(expirationDate);
        voucherRepository.save(voucher);
    }

    /**
     * Updates the method of reduction applied by an existing voucher.
     *
     * @param code          The unique code of the voucher to be updated.
     * @param methodReduce  The new method of reduction for the voucher.
     */
    @Override
    public void UpdateMethodReduceVoucher(String code, MethodReduce methodReduce) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucher.setMethodReduce(methodReduce);
        voucherRepository.save(voucher);
    }

    /**
     * Updates the method of application for an existing voucher.
     *
     * @param code          The unique code of the voucher to be updated.
     * @param methodApply   The new method of application for the voucher.
     */
    @Override
    public void UpdateMethodApplyVoucher(String code, MethodApply methodApply) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucher.setMethodApply(methodApply);
        voucherRepository.save(voucher);
    }

    /**
     * Retrieves a voucher by its unique code.
     *
     * @param code The unique code of the voucher to be retrieved.
     * @return The Voucher object associated with the given code.
     */
    @Override
    public Voucher getVoucherByCode(String code) {
        return voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
    }

    /**
     * Retrieves all vouchers.
     *
     * @return A list of all Voucher objects.
     */
    @Override
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAll();
    }

    /**
     * Deletes a voucher by its unique code.
     *
     * @param code The unique code of the voucher to be deleted.
     */
    @Override
    public void deleteVoucher(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        voucherRepository.delete(voucher);
    }

    /**
     * Checks if a voucher is valid based on its code.
     *
     * @param code The unique code of the voucher to be checked.
     * @return true if the voucher is valid, false otherwise.
     */
    @Override
    public boolean isVoucherValid(String code) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Voucher with code " + code + " does not exist."));
        return voucher.getExpiryDate().isAfter(LocalDate.now()) && voucher.getMinimumOrderValue() > 0;
    }

    /**
     * Retrieves all vouchers that have expired.
     *
     * @return A list of Voucher objects that have expired.
     */
    @Override
    public List<Voucher> getVouchersExpiredDate() {
        return voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getExpiryDate().isBefore(LocalDate.now()))
                .toList();
    }

    /**
     * Retrieves all vouchers that have not expired.
     *
     * @return A list of Voucher objects that have not expired.
     */
    @Override
    public List<Voucher> getVouchersNotExpiredDate() {
        return voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getExpiryDate().isAfter(LocalDate.now()))
                .toList();
    }

    /**
     * Updates an existing voucher with new details.
     *
     * @param code The unique code of the voucher to be updated.
     * @param dto  The DTO containing the new details for the voucher.
     */
    public void updateVoucher(String code, VoucherUpdateDTO dto) {
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Voucher not found"));


        if (dto.getDiscountAmount() >= 0.0) {
            voucher.setDiscountAmount(dto.getDiscountAmount());
        }

        if (dto.getMethodReduce() != null) {
            voucher.setMethodReduce(dto.getMethodReduce());
        }

        if (dto.getMethodApply() != null) {
            voucher.setMethodApply(dto.getMethodApply());
        }

        voucherRepository.save(voucher);
    }

    /**
     * Validates if a voucher has expired.
     *
     * @param voucher The voucher to be validated.
     * @return true if the voucher has expired, false otherwise.
     */
    public boolean validateVoucherExpiryDate(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher does not exist.");
        }
        return !voucher.getExpiryDate().isAfter(LocalDate.now());
    }
}
