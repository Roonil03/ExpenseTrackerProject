package com.example;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ExpenseTrackerApp extends Application {

    private ExpenseTracker tracker = new ExpenseTracker();
    private TextArea expensesArea = new TextArea();
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the primary view from FXML and set it as the scene's root
        scene = new Scene(loadFXML("primary"), 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Expense Tracker");
        primaryStage.show();
    }

    // Method to switch the root of the scene to a different FXML file
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Helper method to load FXML files
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ExpenseTrackerApp.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
