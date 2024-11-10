import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Expense class definition
class Expense {
    private final double amount;
    private final String category;

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

// Custom Exception for non-registered user login attempts
class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}

// Custom Exception for incorrect password
class WrongPasswordException extends Exception {
    public WrongPasswordException(String message) {
        super(message);
    }
}

// User class with account type (normal or premium) and expense tracking
class User implements Runnable {
    private final String username;
    private final String password;
    private final boolean isPremium;
    private final List<Expense> expenses; // List to store user's expenses
    private final Lock expenseLock = new ReentrantLock();

    public User(String username, String password, boolean isPremium) {
        this.username = username;
        this.password = password;
        this.isPremium = isPremium;
        this.expenses = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addExpense(Expense expense) {
        expenseLock.lock();
        try {
            expenses.add(expense);
        } finally {
            expenseLock.unlock();
        }
    }

    public void clearExpenses() {
        expenseLock.lock();
        try {
            expenses.clear();
        } finally {
            expenseLock.unlock();
        }
    }

    @Override
    public void run() {
        // In a real application, you might include logic for handling user-specific tasks.
    }
}

public class App extends Application {
    private ObservableList<String> expenseListItems;
    private User currentUser = null; // Current logged-in user
    private final List<User> userDatabase = new ArrayList<>(); // ArrayList to store users
    private final Lock userDatabaseLock = new ReentrantLock(); // Lock for user database

    @Override
    public void start(Stage primaryStage) {
        // Initial Sign-up Stage
        primaryStage.setTitle("Expense Tracker - Sign Up");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        RadioButton normalUserRadio = new RadioButton("Premium User");
        RadioButton premiumUserRadio = new RadioButton("Normal User");
        ToggleGroup group = new ToggleGroup();
        normalUserRadio.setToggleGroup(group);
        premiumUserRadio.setToggleGroup(group);
        normalUserRadio.setSelected(true);

        Button signUpButton = new Button("Sign Up");
        Button signInButton = new Button("Already have an account? Sign In");

        signUpButton.setOnAction(e -> signUp(primaryStage, usernameField, passwordField, normalUserRadio.isSelected()));
        signInButton.setOnAction(e -> showSignInStage(primaryStage));

        VBox signUpLayout = new VBox(10, usernameField, passwordField, normalUserRadio, premiumUserRadio, signUpButton, signInButton);
        signUpLayout.setPadding(new Insets(10));

        Scene signUpScene = new Scene(signUpLayout, 300, 200);
        primaryStage.setScene(signUpScene);
        primaryStage.show();
    }

    private void signUp(Stage primaryStage, TextField usernameField, PasswordField passwordField, boolean isPremium) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username or Password cannot be empty.");
            return;
        }

        userDatabaseLock.lock();
        try {
            User newUser = new User(username, password, isPremium);
            userDatabase.add(newUser);

            Thread userThread = new Thread(newUser); // Start a new thread for each user
            userThread.start();
        } finally {
            userDatabaseLock.unlock();
        }

        // Switch to Sign-in Stage
        showSignInStage(primaryStage);
    }

    private void showSignInStage(Stage primaryStage) {
        // Sign In Stage
        primaryStage.setTitle("Expense Tracker - Sign In");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button signInButton = new Button("Sign In");
        Button signUpButton = new Button("No account? Sign Up");

        signInButton.setOnAction(e -> {
            try {
                signIn(usernameField, passwordField, primaryStage);
            } catch (UserNotFoundException | WrongPasswordException ex) {
                showAlert("Sign-in Error", ex.getMessage());
            }
        });
        signUpButton.setOnAction(e -> start(primaryStage)); // Show sign-up scene again

        VBox signInLayout = new VBox(10, usernameField, passwordField, signInButton, signUpButton);
        signInLayout.setPadding(new Insets(10));

        Scene signInScene = new Scene(signInLayout, 300, 200);
        primaryStage.setScene(signInScene);
        primaryStage.show();
    }

    private void signIn(TextField usernameField, PasswordField passwordField, Stage primaryStage) throws UserNotFoundException, WrongPasswordException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        userDatabaseLock.lock();
        try {
            User user = findUserByUsername(username);
            if (user == null) {
                throw new UserNotFoundException("User not found.");
            }
            if (!user.getPassword().equals(password)) {
                throw new WrongPasswordException("Incorrect password.");
            }

            currentUser = user;
            if (currentUser.isPremium()) {
                showPremiumView(primaryStage);
            } else {
                showNormalView(primaryStage);
            }
        } finally {
            userDatabaseLock.unlock();
        }
    }

    private User findUserByUsername(String username) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPremiumView(Stage primaryStage) {
        setupExpenseTracker(primaryStage, true);
    }

    private void showNormalView(Stage primaryStage) {
        setupExpenseTracker(primaryStage, false);
    }

    private void setupExpenseTracker(Stage primaryStage, boolean isPremium) {
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        Button addButton = new Button("Add Expense");
        Button clearButton = new Button("Clear All");
        Button showChartButton = new Button(isPremium ? "Show Bar Chart" : "Show Bar Chart (Premium Only)");
        Button signOutButton = new Button("Sign Out");

        expenseListItems = FXCollections.observableArrayList();
        ListView<String> expenseListView = new ListView<>(expenseListItems);

        // Load current user's expenses into the list view
        loadUserExpenses();

        HBox inputBox = new HBox(10, amountField, categoryField, addButton, clearButton, showChartButton, signOutButton);
        inputBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, inputBox, expenseListView);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 400);

        addButton.setOnAction(e -> addExpense(amountField, categoryField));
        clearButton.setOnAction(e -> clearExpenses());
        showChartButton.setOnAction(e -> {
            if (isPremium) {
                showBarChart(primaryStage);
            } else {
                showAlert("Upgrade", "Upgrade to Premium to access the chart feature.");
            }
        });
        signOutButton.setOnAction(e -> signOut(primaryStage));

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadUserExpenses() {
        expenseListItems.clear();
        for (Expense expense : currentUser.getExpenses()) {
            expenseListItems.add(String.format("₹%.2f - %s", expense.getAmount(), expense.getCategory()));
        }
    }

    private void addExpense(TextField amountField, TextField categoryField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            if (!category.isEmpty()) {
                Expense expense = new Expense(amount, category);
                currentUser.addExpense(expense);
                expenseListItems.add(String.format("₹%.2f - %s", amount, category));
                amountField.clear();
                categoryField.clear();
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void clearExpenses() {
        currentUser.clearExpenses();
        expenseListItems.clear();
    }

    private void signOut(Stage primaryStage) {
        currentUser = null; // Clear current user
        showSignInStage(primaryStage); // Return to the sign-in stage
    }

    private void showBarChart(Stage primaryStage) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Expenses by Category");

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : currentUser.getExpenses()) { // Use current user's expenses
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

        Button backButton = new Button("Back"); // Back button to return to expense list
        backButton.setOnAction(e -> setupExpenseTracker(primaryStage, currentUser.isPremium()));

        BorderPane chartLayout = new BorderPane();
        chartLayout.setCenter(barChart);
        chartLayout.setBottom(backButton);
        BorderPane.setMargin(backButton, new Insets(10));

        Scene chartScene = new Scene(chartLayout, 600, 400);
        primaryStage.setScene(chartScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
