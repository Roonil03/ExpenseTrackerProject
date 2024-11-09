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

    public abstract void addExpense(double amount, String category);

    public String getUsername() {
        return username;
    }
}

class NormalUser extends User {
    public NormalUser(String username, String password) {
        super(username, password);
    }

    @Override
    public void addExpense(double amount, String category) {
        expenses.add(new Expense(amount, category));
    }
}

class PremiumUser extends User {
    public PremiumUser(String username, String password) {
        super(username, password);
    }

    @Override
    public void addExpense(double amount, String category) {
        expenses.add(new Expense(amount, category));
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
    private static final Lock lock = new ReentrantLock();

    public ExpenseInputTask(User user, double amount, String category) {
        this.user = user;
        this.amount = amount;
        this.category = category;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            user.addExpense(amount, category);
            System.out.printf("Added expense $%.2f for %s\n", amount, category);
        } finally {
            lock.unlock();
        }
    }
}

// Generic class for user data storage
class UserData<T extends User> {
    private List<T> users = new ArrayList<>();

    public void addUser(T user) {
        users.add(user);
    }

    public Optional<T> getUser(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.password.equals(password))
                .findFirst();
    }
}

// Main application class
public class Appv2 extends Application {
    private User currentUser;
    private UserData<User> userData = new UserData<>();
    private ObservableList<String> expenseListItems;
    private ListView<String> expenseListView;

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

        HBox loginBox = new HBox(10, usernameField, passwordField, signUpButton, signInButton);
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
        User user = username.endsWith("@premium") ? new PremiumUser(username, password) : new NormalUser(username, password);
        userData.addUser(user);
        showAlert("Success", "User registered successfully!");
    }

    private void signIn(String username, String password, Stage primaryStage) {
        Optional<User> userOpt = userData.getUser(username, password);
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
        Button addButton = new Button("Add Expense");
        Button showChartButton = new Button("Show Monthly Chart");

        addButton.setOnAction(e -> addExpense(amountField, categoryField));
        showChartButton.setOnAction(e -> {
            if (currentUser instanceof PremiumUser) {
                ((PremiumUser) currentUser).displayMonthlyGraph(primaryStage);
            } else {
                showAlert("Access Denied", "Only premium users can view monthly charts.");
            }
        });

        HBox inputBox = new HBox(10, amountField, categoryField, addButton, showChartButton);
        VBox vbox = new VBox(10, inputBox, expenseListView);
        vbox.setPadding(new Insets(10));

        Scene trackerScene = new Scene(vbox, 400, 400);
        primaryStage.setScene(trackerScene);
    }

    private void addExpense(TextField amountField, TextField categoryField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            if (!category.isEmpty()) {
                new Thread(new ExpenseInputTask(currentUser, amount, category)).start();
                expenseListItems.add(String.format("$%.2f - %s", amount, category));
                amountField.clear();
                categoryField.clear();
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
