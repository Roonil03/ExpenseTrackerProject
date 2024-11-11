public class Expense {
    private double amount;
    private String category;

    public Expense(double amount, String category) {
        this.amount = amount;
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }
}
