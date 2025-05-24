package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.dto.BankDTO;
import kiradev.studio.Eommerce.entity.Bank;
import kiradev.studio.Eommerce.repository.BankRepository;
import kiradev.studio.Eommerce.service.Interface.IBankService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BankService implements IBankService {
    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public boolean isExistBank(String owner, String numberCard) {
        List<Bank> banks = bankRepository.findByowner(owner);
        for (Bank bank : banks) {
            if (bank.getNumberCard().equals(numberCard)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Bank createBankAccount(BankDTO bankDTO, UUID uuid) {
        Bank bank = new Bank();
        bank.setOwner(bankDTO.getOwner());
        bank.setNumberCard(bankDTO.getNumberCard());
        bank.setBank(bankDTO.getBankName());
        bank.setAddress(bankDTO.getAddress());
        bank.setUserID(uuid);
        bank.setCVV(bankDTO.getCvv());
        bank.setZipCode(bankDTO.getZipcode());

        return bankRepository.save(bank);
    }

    @Override
    public void deleteBankAccount(String owner, String numberCard) {
        List<Bank> banks = bankRepository.findByowner(owner);
        bankRepository.deleteAll(banks);
    }

    @Override
    public void updateBankAccount(String owner, String numberCard, String newOwner, String newNumberCard, String newBankName) {
        List<Bank> banks = bankRepository.findByowner(owner);
        for (Bank bank : banks) {
            if (bank.getNumberCard().equals(numberCard)) {
                if(newOwner!=null) bank.setOwner(newOwner);
                if(newBankName!=null) bank.setNumberCard(newNumberCard);
                if(newBankName!=null) bank.setBank(newBankName);
                bankRepository.save(bank);
            }
        }

    }

    @Override
    public List<Bank> getBankOwner(String owner) {
        return bankRepository.findByowner(owner);
    }

    @Override
    public List<Bank> getBankNumberCard(String numberCard) {
        return bankRepository.findBynumberCard(numberCard);
    }

    @Override
    public List<Bank> getBankUserID(UUID userID) {
        return bankRepository.findByuserID(userID);
    }


}
