package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.dto.BankDTO;
import kiradev.studio.Eommerce.entity.Bank;

import java.util.List;
import java.util.UUID;

public interface IBankService {
    Bank createBankAccount(BankDTO bankDTO, UUID uuid);
    void deleteBankAccount(String owner, String numberCard);
    void updateBankAccount(String owner, String numberCard, String newOwner, String newNumberCard, String newBankName);
    List<Bank> getBankOwner(String owner);
    List<Bank> getBankNumberCard(String numberCard);
    List<Bank> getBankUserID(UUID userID);
}
