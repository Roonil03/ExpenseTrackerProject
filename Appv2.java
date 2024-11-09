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

// User class hierarchy
abstract class User {
    protected String username;
    protected String password;
    protected List<Expense> expenses;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.expenses = Collections.synchronizedList(new ArrayList<>());
    }

    public abstract void addExpense(double amount, String category, String additionalFeature);

    public String getUsername() {
        return username;
    }
}

class NormalUser extends User {
    public NormalUser(String username, String password) {
        super(username, password);
    }

    @Override
    public void addExpense(double amount, String category, String additionalFeature) {
        expenses.add(new Expense(amount, category));
    }
}

class PremiumUser extends User {
    public PremiumUser(String username, String password) {
        super(username, password);
    }

    @Override
    public void addExpense(double amount, String category, String additionalFeature) {
        expenses.add(new Expense(amount, category, additionalFeature));
    }

    public void displayMonthlyGraph(Stage primaryStage) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Monthly Expenses by Category");

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

        BorderPane chartLayout = new BorderPane();
        chartLayout.setCenter(barChart);
        Button backButton = new Button("Back to List");
        backButton.setOnAction(e -> primaryStage.setScene(primaryStage.getScene())); // Back to main screen
        chartLayout.setBottom(backButton);
        BorderPane.setMargin(backButton, new Insets(10));

        Scene chartScene = new Scene(chartLayout, 600, 400);
        primaryStage.setScene(chartScene);
    }
}

// Thread-safe ExpenseInputTask for multiple user inputs
class ExpenseInputTask implements Runnable {
    private final User user;
    private final double amount;
    private final String category;
    private final String additionalFeature;
    private static final Lock lock = new ReentrantLock();

    public ExpenseInputTask(User user, double amount, String category, String additionalFeature) {
        this.user = user;
        this.amount = amount;
        this.category = category;
        this.additionalFeature = additionalFeature;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            user.addExpense(amount, category, additionalFeature);
            System.out.printf("Added expense $%.2f for %s\n", amount, category);
        } finally {
            lock.unlock();
        }
    }
}

// Database class for storing users
class Database {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
    }

    public Optional<User> getUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.password.equals(password))
                .findFirst();
    }
}

// Main application class
public class App extends Application {
    private User currentUser;
    private Database database = new Database();
    private ObservableList<String> expenseListItems;
    private ListView<String> expenseListView;
    private boolean isPremium = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker");

        // Sign Up and Login UI
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button signUpButton = new Button("Sign Up");
        Button signInButton = new Button("Sign In");
        ToggleButton premiumToggle = new ToggleButton("Premium Account");

        premiumToggle.setOnAction(e -> isPremium = premiumToggle.isSelected());

        HBox loginBox = new HBox(10, usernameField, passwordField, premiumToggle, signUpButton, signInButton);
        VBox vbox = new VBox(10, loginBox);
        Scene loginScene = new Scene(vbox, 400, 200);

        signUpButton.setOnAction(e -> signUp(usernameField.getText(), passwordField.getText()));
        signInButton.setOnAction(e -> signIn(usernameField.getText(), passwordField.getText(), primaryStage));

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void signUp(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Invalid Input", "Username and password cannot be empty.");
            return;
        }
        User user = isPremium ? new PremiumUser(username, password) : new NormalUser(username, password);
        database.addUser(user);
        showAlert("Success", "User registered successfully!");
    }

    private void signIn(String username, String password, Stage primaryStage) {
        Optional<User> userOpt = database.getUser(username, password);
        if (userOpt.isPresent()) {
            currentUser = userOpt.get();
            showExpenseTracker(primaryStage);
        } else {
            showAlert("Error", "Invalid username or password.");
        }
    }

    private void showExpenseTracker(Stage primaryStage) {
        expenseListItems = FXCollections.observableArrayList();
        expenseListView = new ListView<>(expenseListItems);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        TextField additionalFeatureField = null;
        if (currentUser instanceof PremiumUser) {
            additionalFeatureField = new TextField();
            additionalFeatureField.setPromptText("Additional Feature (Premium)");
        }

        Button addButton = new Button("Add Expense");
        Button showChartButton = new Button("Show Monthly Chart");
        Button signOutButton = new Button("Sign Out");

        final TextField finalAdditionalFeatureField = additionalFeatureField;
        addButton.setOnAction(e -> addExpense(amountField, categoryField, finalAdditionalFeatureField));
        showChartButton.setOnAction(e -> {
            if (currentUser instanceof PremiumUser) {
                ((PremiumUser) currentUser).displayMonthlyGraph(primaryStage);
            } else {
                showAlert("Access Denied", "Only premium users can view monthly charts.");
            }
        });

        signOutButton.setOnAction(e -> {
            currentUser = null;
            primaryStage.setScene(primaryStage.getScene());
            showAlert("Sign Out", "You have been signed out.");
        });

        HBox inputBox = currentUser instanceof PremiumUser
                ? new HBox(10, amountField, categoryField, finalAdditionalFeatureField, addButton, showChartButton)
                : new HBox(10, amountField, categoryField, addButton, showChartButton);

        VBox vbox = new VBox(10, inputBox, expenseListView, signOutButton);
        vbox.setPadding(new Insets(10));

        Scene trackerScene = new Scene(vbox, 400, 400);
        primaryStage.setScene(trackerScene);
    }

    private void addExpense(TextField amountField, TextField categoryField, TextField additionalFeatureField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String additionalFeature = additionalFeatureField != null ? additionalFeatureField.getText() : null;

            if (!category.isEmpty()) {
                new Thread(new ExpenseInputTask(currentUser, amount, category, additionalFeature)).start();
                expenseListItems.add(String.format("$%.2f - %s%s",
                        amount, category, additionalFeature != null ? " - " + additionalFeature : ""));
                amountField.clear();
                categoryField.clear();
                if (additionalFeatureField != null) additionalFeatureField.clear();
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
