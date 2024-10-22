import javafx.application.Application;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {
    private List<Expense> expenses = new ArrayList<>();
    private ListView<String> expenseListView;
    private ObservableList<String> expenseListItems;
    private boolean isDarkMode = false; // Flag for dark mode

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Expense Tracker");

        // Create UI components
        TextField amountField = new TextField();
        amountField.setPromptText("Amount");
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category");
        Button addButton = new Button("Add Expense");
        Button clearButton = new Button("Clear All");
        Button showChartButton = new Button("Show Bar Chart");
        ToggleButton toggleButton = new ToggleButton("Switch to Dark Mode");

        expenseListItems = FXCollections.observableArrayList();
        expenseListView = new ListView<>(expenseListItems);

        HBox inputBox = new HBox(10, amountField, categoryField, addButton, clearButton, showChartButton, toggleButton);
        inputBox.setPadding(new Insets(10));

        VBox vbox = new VBox(10, inputBox, expenseListView);
        vbox.setPadding(new Insets(10));

        Scene listScene = new Scene(vbox, 400, 400);

        addButton.setOnAction(e -> addExpense(amountField, categoryField));
        clearButton.setOnAction(e -> clearExpenses());
        showChartButton.setOnAction(e -> showBarChart(primaryStage));

        // Toggle Button Action
        toggleButton.setOnAction(e -> {
            isDarkMode = toggleButton.isSelected();
            applyStyles(primaryStage);
        });

        primaryStage.setScene(listScene);
        applyStyles(primaryStage); // Apply initial styles
        primaryStage.show();
    }

    private void applyStyles(Stage primaryStage) {
        String style = isDarkMode ? "dark.css" : "light.css";
        primaryStage.getScene().getStylesheets().clear();
        primaryStage.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
    }

    private void addExpense(TextField amountField, TextField categoryField) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            if (!category.isEmpty()) {
                Expense expense = new Expense(amount, category);
                expenses.add(expense);
                expenseListItems.add(String.format("$%.2f - %s", amount, category));
                amountField.clear();
                categoryField.clear();
            }
        } catch (NumberFormatException ex) {
            // Handle invalid input for amount
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Invalid amount");
            alert.setContentText("Please enter a valid number for the amount.");
            alert.showAndWait();
        }
    }

    private void clearExpenses() {
        expenses.clear();
        expenseListItems.clear();
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

        // Create a back button to return to the list view
        Button backButton = new Button("Back to List");
        backButton.setOnAction(e -> {
            Scene listScene = new Scene(new VBox(expenseListView), 400, 400);
            primaryStage.setScene(listScene);
        });

        // Create a layout for the bar chart
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
