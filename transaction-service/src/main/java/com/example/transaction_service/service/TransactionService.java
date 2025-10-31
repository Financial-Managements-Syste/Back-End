package com.example.transaction_service.service;

import com.example.transaction_service.entity.sqlite.SQLiteTransaction;
import com.example.transaction_service.repository.sqlite.SQLiteTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private SQLiteTransactionRepository SQLiteTransactionRepository;

    //  Create Transaction
    public SQLiteTransaction createTransaction(SQLiteTransaction transaction) {
        return SQLiteTransactionRepository.save(transaction);
    }

    // Get all Transactions
    public List<SQLiteTransaction> getAllTransactions() {
        return SQLiteTransactionRepository.findAll();
    }

    //  Get Transaction by ID
    public Optional<SQLiteTransaction> getTransactionById(int id) {
        return SQLiteTransactionRepository.findById(id);
    }

    //  Get Transactions by User
    public List<SQLiteTransaction> getTransactionsByUserId(int userId) {
        return SQLiteTransactionRepository.findByUserId(userId);
    }

    // 🗂 Get Transactions by Category
    public List<SQLiteTransaction> getTransactionsByCategoryId(int categoryId) {
        return SQLiteTransactionRepository.findByCategoryId(categoryId);
    }

    //  Update Transaction
    public SQLiteTransaction updateTransaction(int id, SQLiteTransaction updatedTransaction) {
        return SQLiteTransactionRepository.findById(id).map(transaction -> {
            transaction.setUserId(updatedTransaction.getUserId());
            transaction.setCategoryId(updatedTransaction.getCategoryId());
            transaction.setAmount(updatedTransaction.getAmount());
            transaction.setTransactionType(updatedTransaction.getTransactionType());
            transaction.setTransactionDate(updatedTransaction.getTransactionDate());
            transaction.setDescription(updatedTransaction.getDescription());
            transaction.setPaymentMethod(updatedTransaction.getPaymentMethod());
            return SQLiteTransactionRepository.save(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction not found with ID " + id));
    }


    //  Delete Transaction
    public void deleteTransaction(int id) {
        SQLiteTransactionRepository.deleteById(id);
    }
}

