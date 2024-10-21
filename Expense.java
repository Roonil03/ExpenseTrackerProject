package com.example;
public class Expense {
    private double amount;
    private String category;
    private String date; // Consider using LocalDate for better date handling
    private String description;

    public Expense(double amount, String category, String date, String description) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    // Getters
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f (%s) - %s", date, amount, category, description);
    }
}
