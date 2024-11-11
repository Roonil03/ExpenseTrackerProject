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

// Custom Exception for non-registered user login attempts
class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }
}

// User class with account type (normal or premium)
class User {
    private String username;
    private String password;
    private boolean isPremium;

    public User(String username, String password, boolean isPremium) {
        this.username = username;
        this.password = password;
        this.isPremium = isPremium;
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
}

public class App extends Application {
    private List<Expense> expenses = new ArrayList<>();
    private ObservableList<String> expenseListItems;
    private boolean isDarkMode = false;
    private User currentUser = null; // Current logged-in user
    private Map<String, User> userDatabase = new HashMap<>(); // For user sign-up and sign-in
    private Lock expenseLock = new ReentrantLock(); // To ensure thread safety for expense list

    @Override
    public void start(Stage primaryStage) {
        // Initial Sign-up Stage
        primaryStage.setTitle("Expense Tracker - Sign Up");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        RadioButton normalUserRadio = new RadioButton("Normal User");
        RadioButton premiumUserRadio = new RadioButton("Premium User");
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

        User newUser = new User(username, password, isPremium);
        userDatabase.put(username, newUser);

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

        signInButton.setOnAction(e -> signIn(usernameField, passwordField, primaryStage));
        signUpButton.setOnAction(e -> showSignUpStage(primaryStage));

        VBox signInLayout = new VBox(10, usernameField, passwordField, signInButton, signUpButton);
        signInLayout.setPadding(new Insets(10));

        Scene signInScene = new Scene(signInLayout, 300, 200);
        primaryStage.setScene(signInScene);
        primaryStage.show();
    }

    private void signIn(TextField usernameField, PasswordField passwordField, Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!userDatabase.containsKey(username)) {
            showAlert("Sign-in Error", "User not found.");
            return;
        }

        User user = userDatabase.get(username);
        if (!user.getPassword().equals(password)) {
            showAlert("Sign-in Error", "Incorrect password.");
            return;
        }

        currentUser = user;

        // Proceed to expense tracker UI based on user type
        if (currentUser.isPremium()) {
            showPremiumView(primaryStage);
        } else {
            showNormalView(primaryStage);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showPremiumView(Stage primaryStage) {
        // Create UI components for the expense tracker for Premium Users
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        Button addButton = new Button("Add Expense");
        Button clearButton = new Button("Clear All");
        Button showChartButton = new Button("Show Bar Chart");
        Button signOutButton = new Button("Sign Out");

        expenseListItems = FXCollections.observableArrayList();
        ListView<String> expenseListView = new ListView<>(expenseListItems);

        HBox inputBox = new HBox(10, amountField, categoryField, addButton, clearButton, showChartButton, signOutButton);
        inputBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, inputBox, expenseListView);
        vbox.setPadding(new Insets(10));

        Scene premiumScene = new Scene(vbox, 400, 400);

        addButton.setOnAction(e -> addExpense(amountField, categoryField));
        clearButton.setOnAction(e -> clearExpenses());
        showChartButton.setOnAction(e -> showBarChart(primaryStage));
        signOutButton.setOnAction(e -> showSignInStage(primaryStage));

        primaryStage.setScene(premiumScene);
        primaryStage.show();
    }

    private void showNormalView(Stage primaryStage) {
        // Create UI components for the expense tracker for Normal Users
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        Button addButton = new Button("Add Expense");
        Button clearButton = new Button("Clear All");
        Button showChartButton = new Button("Show Bar Chart (Premium Only)");
        Button signOutButton = new Button("Sign Out");

        expenseListItems = FXCollections.observableArrayList();
        ListView<String> expenseListView = new ListView<>(expenseListItems);

        HBox inputBox = new HBox(10, amountField, categoryField, addButton, clearButton, showChartButton, signOutButton);
        inputBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, inputBox, expenseListView);
        vbox.setPadding(new Insets(10));

        Scene normalScene = new Scene(vbox, 400, 400);

        addButton.setOnAction(e -> addExpense(amountField, categoryField));
        clearButton.setOnAction(e -> clearExpenses());
        showChartButton.setOnAction(e -> showAlert("Upgrade", "Upgrade to Premium to access the chart feature."));
        signOutButton.setOnAction(e -> showSignInStage(primaryStage));

        // Show random ads for normal users
        showRandomAds(primaryStage);

        primaryStage.setScene(normalScene);
        primaryStage.show();
    }

    private void addExpense(TextField amountField, TextField categoryField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            if (!category.isEmpty()) {
                expenseLock.lock(); // Ensure thread safety when modifying expenses
                try {
                    Expense expense = new Expense(amount, category);
                    expenses.add(expense);
                    expenseListItems.add(String.format("$%.2f - %s", amount, category));
                } finally {
                    expenseLock.unlock();
                }
                amountField.clear();
                categoryField.clear();
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void clearExpenses() {
        expenseLock.lock(); // Ensure thread safety when clearing expenses
        try {
            expenses.clear();
            expenseListItems.clear();
        } finally {
            expenseLock.unlock();
        }
    }

    private void showBarChart(Stage primaryStage) {
        // Create the axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        // Create the bar chart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Expenses by Category");

        // Create a map to aggregate expenses by category
        Map<String, Double> categoryTotals = new HashMap<>();

        // Aggregate expenses by category
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        // Populate the bar chart data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);

        // Create a layout for the bar chart
        BorderPane chartLayout = new BorderPane();
        chartLayout.setCenter(barChart);

        Scene chartScene = new Scene(chartLayout, 600, 400);
        primaryStage.setScene(chartScene);
    }

    private void showRandomAds(Stage primaryStage) {
        // Simulate random ads for normal users
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> showAlert("Ad", "Upgrade to Premium for more features!"));
            }
        }, 5000, 10000); // Show ads at random intervals
    }

    public static void main(String[] args) {
        launch(args);
    }
}
