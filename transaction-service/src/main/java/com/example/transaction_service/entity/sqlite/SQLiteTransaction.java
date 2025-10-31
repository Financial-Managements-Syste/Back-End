package com.example.transaction_service.entity.sqlite;

import javax.persistence.*;

@Entity
@Table(name = "Transactions")
public class SQLiteTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private int transactionId; // ✅ renamed from transaction_id

    @Column(name = "user_id")
    private int userId; // ✅ renamed from user_id

    @Column(name = "category_id")
    private int categoryId; // ✅ renamed from category_id

    @Column(name = "amount")
    private double amount;

    @Column(name = "transaction_type")
    private String transactionType; // ✅ renamed from transaction_type

    @Column(name = "transaction_date")
    private String transactionDate; // ✅ renamed from transaction_date

    @Column(name = "description")
    private String description;

    @Column(name = "payment_method")
    private String paymentMethod; // ✅ renamed from payment_method

    public SQLiteTransaction() {
    }

    // --- Getters & Setters ---
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
