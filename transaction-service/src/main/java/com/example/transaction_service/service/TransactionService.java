package com.example.transaction_service.service;

import com.example.transaction_service.entity.Transaction;
import com.example.transaction_service.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    //  Create Transaction
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    // Get all Transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    //  Get Transaction by ID
    public Optional<Transaction> getTransactionById(int id) {
        return transactionRepository.findById(id);
    }

    //  Get Transactions by User
    public List<Transaction> getTransactionsByUserId(int userId) {
        return transactionRepository.findByUserId(userId);
    }

    // 🗂 Get Transactions by Category
    public List<Transaction> getTransactionsByCategoryId(int categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }

    //  Update Transaction
    public Transaction updateTransaction(int id, Transaction updatedTransaction) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.setUserId(updatedTransaction.getUserId());
            transaction.setCategoryId(updatedTransaction.getCategoryId());
            transaction.setAmount(updatedTransaction.getAmount());
            transaction.setTransactionType(updatedTransaction.getTransactionType());
            transaction.setTransactionDate(updatedTransaction.getTransactionDate());
            transaction.setDescription(updatedTransaction.getDescription());
            transaction.setPaymentMethod(updatedTransaction.getPaymentMethod());
            return transactionRepository.save(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction not found with ID " + id));
    }


    //  Delete Transaction
    public void deleteTransaction(int id) {
        transactionRepository.deleteById(id);
    }
}

