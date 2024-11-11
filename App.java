import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.util.*;
import java.util.function.Predicate;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;

// import ThemeManager.java;
// import Utility.java;

// Interface for user authentication
interface UserAuthentication {
    boolean authenticateUser(String username, String password);
    boolean authenticateUser(StringBuffer usernameBuffer, String password); // Overloaded method
}

// Abstract class for user roles
abstract class UserRole {
    private final boolean isPremium;

    public UserRole(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public boolean isPremium() {
        return isPremium;
    }

    // Abstract method for showing expense views, to be overridden in subclasses
    public abstract void showView(Stage primaryStage);
}

class Database<T> {
    private final List<T> items = Collections.synchronizedList(new ArrayList<>()); // Thread-safe list
    
    // Add an item to the database
    public synchronized void addItem(T item) {
        items.add(item);
    }

    // Find an item by a predicate
    public synchronized T findItem(Predicate<T> predicate) {
        for (T item : items) {
            if (predicate.test(item)) {
                return item;
            }
        }
        return null; // Return null if no matching item is found
    }

    // Get all items in the database
    public List<T> getAllItems() {
        return new ArrayList<>(items);
    }
}

class UserDatabase {
    // Using a thread-safe ConcurrentHashMap to store user data
    private static final Map<String, User> userDatabase = new ConcurrentHashMap<>();

    // Method to add a new user to the in-memory database
    public static synchronized void addUser(User user) {
        userDatabase.put(user.getUsername(), user);
    }

    // Method to find a user by username
    public static synchronized User findUserByUsername(String username) {
        return userDatabase.get(username);  // Return null if no matching user is found
    }

    // Method to get all users (for debugging purposes)
    public static Map<String, User> getAllUsers() {
        return userDatabase;
    }
}

// User class inheriting from UserRole and implementing UserAuthentication
class User extends UserRole implements UserAuthentication {
    private final String username;
    private final String password;
    private final List<Expense> expenses;

    public User(String username, String password, boolean isPremium) {
        super(isPremium);
        this.username = username;
        this.password = password;
        this.expenses = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    @Override
    public boolean authenticateUser(StringBuffer usernameBuffer, String password) {
        return this.username.contentEquals(usernameBuffer) && this.password.equals(password);
    }

    @Override
    public void showView(Stage primaryStage) {
        // Determines which view to show based on user type
        if (isPremium()) {
            new PremiumView().display(primaryStage, this);
        } else {
            new NormalView().display(primaryStage, this);
        }
    }
}

// Expense class
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

// View for premium users
// Modify the PremiumView and NormalView to listen to the user’s expense changes
class PremiumView {
    private final ObservableList<String> expenseItems = FXCollections.observableArrayList();
    
    public void display(Stage primaryStage, User user) {
        setupExpenseTracker(primaryStage, user, true);
    }

    private void setupExpenseTracker(Stage primaryStage, User user, boolean isPremium) {
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        Button addButton = new Button("Add Expense");
        Button clearButton = new Button("Clear All");
        Button showChartButton = new Button("Show Bar Chart");
        Button signOutButton = new Button("Sign Out");
        Button toggleThemeButton = new Button("Toggle Theme");

        ListView<String> expenseListView = new ListView<>(expenseItems); // ListView to show expenses

        toggleThemeButton.setOnAction(e -> ThemeManager.toggleTheme(primaryStage));
        addButton.setOnAction(e -> addExpense(user, amountField, categoryField));
        clearButton.setOnAction(e -> clearExpenses(user));
        showChartButton.setOnAction(e -> showBarChart(primaryStage, user));
        signOutButton.setOnAction(e -> signOut(primaryStage));

        HBox inputBox = new HBox(10, amountField, categoryField);
        HBox buttonBox = new HBox(10, addButton, clearButton, showChartButton, signOutButton, toggleThemeButton);
        inputBox.setPadding(new Insets(10));
        buttonBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, inputBox, buttonBox, expenseListView);

        Scene scene = new Scene(vbox, 600, 200);
        ThemeManager.applyTheme(scene, primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Start a background thread to track and update user’s expenses
        Thread updateExpensesThread = new Thread(() -> {
            while (true) {
                Platform.runLater(() -> loadExpenses(user));  // Update UI with current user expenses
                try {
                    Thread.sleep(1000);  // Update every second (adjust as necessary)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateExpensesThread.setDaemon(true);  // Allow thread to exit when application exits
        updateExpensesThread.start();
    }

    private void addExpense(User user, TextField amountField, TextField categoryField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            if (!category.isEmpty() && (user.isPremium() /*|| Utility.isValidCategory(category)*/)) {
                Expense expense = new Expense(amount, category);
                user.getExpenses().add(expense);
                expenseItems.add("Category: " + category + ", Amount: $" + amount); // Update ListView
                amountField.clear();
                categoryField.clear();
            } else {
                showAlert("Invalid Category", "Please enter a valid category.");
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void clearExpenses(User user) {
        user.getExpenses().clear();
        expenseItems.clear(); // Clear ListView items
    }

    private void loadExpenses(User user) {
        expenseItems.clear();
        for (Expense expense : user.getExpenses()) {
            expenseItems.add("Category: " + expense.getCategory() + ", Amount: $" + expense.getAmount());
        }
    }

    private void showBarChart(Stage primaryStage, User user) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Expenses by Category");

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : user.getExpenses()) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> user.showView(primaryStage));
        Button toggleThemeButton = new Button("Toggle Theme");
        toggleThemeButton.setOnAction(e -> ThemeManager.toggleTheme(primaryStage));

        VBox vbox = new VBox(10, barChart, backButton, toggleThemeButton);
        Scene chartScene = new Scene(vbox, 600, 400);
        ThemeManager.applyTheme(chartScene, primaryStage);
        primaryStage.setScene(chartScene);
    }

    private void signOut(Stage primaryStage) {
        new App().showSignInStage(primaryStage);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


class NormalView {
    private final ObservableList<String> expenseItems = FXCollections.observableArrayList();

    public void display(Stage primaryStage, User user) {
        setupExpenseTracker(primaryStage, user, false);
    }

    private void setupExpenseTracker(Stage primaryStage, User user, boolean isPremium) {
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");

        Button addButton = new Button("Add Expense");
        Button clearButton = new Button("Clear All");
        Button showChartButton = new Button("Show Bar Chart");
        Button signOutButton = new Button("Sign Out");
        Button toggleThemeButton = new Button("Toggle Theme");

        ListView<String> expenseListView = new ListView<>(expenseItems); // ListView to show expenses

        toggleThemeButton.setOnAction(e -> ThemeManager.toggleTheme(primaryStage));
        addButton.setOnAction(e -> addExpense(user, amountField, categoryField));
        clearButton.setOnAction(e -> clearExpenses(user));
        showChartButton.setOnAction(e -> showBarChart(primaryStage, user));
        signOutButton.setOnAction(e -> signOut(primaryStage));

        HBox inputBox = new HBox(10, amountField, categoryField);
        HBox buttonBox = new HBox(10, addButton, clearButton, showChartButton, signOutButton, toggleThemeButton);
        inputBox.setPadding(new Insets(10));
        buttonBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, inputBox, buttonBox, expenseListView);

        Scene scene = new Scene(vbox, 600, 200);
        ThemeManager.applyTheme(scene, primaryStage);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start a background thread to track and update user’s expenses
        Thread updateExpensesThread = new Thread(() -> {
            while (true) {
                Platform.runLater(() -> loadExpenses(user));  // Update UI with current user expenses
                try {
                    Thread.sleep(1000);  // Update every second (adjust as necessary)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateExpensesThread.setDaemon(true);  // Allow thread to exit when application exits
        updateExpensesThread.start();
    }

    private void addExpense(User user, TextField amountField, TextField categoryField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            if (!category.isEmpty() && (user.isPremium() || Utility.isValidCategory(category))) {
                Expense expense = new Expense(amount, category);
                user.getExpenses().add(expense);
                expenseItems.add("Category: " + category + ", Amount: $" + amount); // Update ListView
                amountField.clear();
                categoryField.clear();
            } else {
                showAlert("Invalid Category", "Please enter a valid category. (Food, Travel, Entertainment, Others)");
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid number for the amount.");
        }
    }

    private void clearExpenses(User user) {
        user.getExpenses().clear();
        expenseItems.clear(); // Clear ListView items
    }

    private void loadExpenses(User user) {
        expenseItems.clear();
        for (Expense expense : user.getExpenses()) {
            expenseItems.add("Category: " + expense.getCategory() + ", Amount: $" + expense.getAmount());
        }
    }

    private void showBarChart(Stage primaryStage, User user) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Category");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Expenses by Category");

        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : user.getExpenses()) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses");

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> user.showView(primaryStage));
        Button toggleThemeButton = new Button("Toggle Theme");
        toggleThemeButton.setOnAction(e -> ThemeManager.toggleTheme(primaryStage));

        VBox vbox = new VBox(10, barChart, backButton, toggleThemeButton);
        Scene chartScene = new Scene(vbox, 600, 400);
        ThemeManager.applyTheme(chartScene, primaryStage);
        primaryStage.setScene(chartScene);
    }

    private void signOut(Stage primaryStage) {
        new App().showSignInStage(primaryStage);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

public class App extends Application {
    private User currentUser = null;
    
    @Override
    public void start(Stage primaryStage) {
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
        Button toggleThemeButton = new Button("Toggle Theme");

        signUpButton.setOnAction(e -> signUp(primaryStage, usernameField, passwordField, premiumUserRadio.isSelected()));
        signInButton.setOnAction(e -> showSignInStage(primaryStage));
        toggleThemeButton.setOnAction(e -> ThemeManager.toggleTheme(primaryStage));

        VBox signUpLayout = new VBox(10, usernameField, passwordField, normalUserRadio, premiumUserRadio, signUpButton, signInButton, toggleThemeButton);
        signUpLayout.setPadding(new Insets(10));

        Scene signUpScene = new Scene(signUpLayout, 300, 200);
        ThemeManager.applyTheme(signUpScene, primaryStage);
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

        // Check if user already exists
        User existingUser = UserDatabase.findUserByUsername(username);
        if (existingUser != null) {
            showAlert("Error", "User already exists. Please sign in.");
            return;
        }

        User newUser = new User(username, password, isPremium);
        UserDatabase.addUser(newUser);

        showSignInStage(primaryStage);
    }

    public void showSignInStage(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker - Sign In");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button signInButton = new Button("Sign In");
        Button signUpButton = new Button("No account? Sign Up");
        Button toggleThemeButton = new Button("Toggle Theme");

        signInButton.setOnAction(e -> signIn(usernameField, passwordField, primaryStage));
        signUpButton.setOnAction(e -> start(primaryStage));
        toggleThemeButton.setOnAction(e -> ThemeManager.toggleTheme(primaryStage));

        VBox signInLayout = new VBox(10, usernameField, passwordField, signInButton, signUpButton, toggleThemeButton);
        signInLayout.setPadding(new Insets(10));

        Scene signInScene = new Scene(signInLayout, 300, 200);
        ThemeManager.applyTheme(signInScene, primaryStage);
        primaryStage.setScene(signInScene);
        primaryStage.show();
    }

    private void signIn(TextField usernameField, PasswordField passwordField, Stage primaryStage) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Find user by username in the UserDatabase
        User user = UserDatabase.findUserByUsername(username);
        if (user == null) {
            showAlert("Sign-in Error", "User not found.");
            return;
        }

        // Authenticate user
        if (!user.authenticateUser(username, password)) {
            showAlert("Sign-in Error", "Incorrect password.");
            return;
        }

        currentUser = user;
        user.showView(primaryStage);  // Show user view (either Premium or Normal)
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
