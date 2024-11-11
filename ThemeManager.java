import javafx.scene.Scene;
import javafx.stage.Stage;

public class ThemeManager {
    private static boolean isDarkMode = false;  

    //private static final String DARK_MODE_CSS = "/* Dark Mode Styles */\n.root {\n    -fx-background-color: #000000; /* Set background to black */\n}\n\n.region {\n    -fx-background-color: #333333; /* Dark background for regions */\n    -fx-text-fill: #FFFFFF;         /* White text */\n}";
    //private static final String LIGHT_MODE_CSS = "/* Light Mode Styles */\n.root {\n    -fx-background-color: #D3D3D3; /* Set background to light gray */\n}\n\n.region {\n    -fx-background-color: #FFFFFF; /* Light background for regions */\n    -fx-text-fill: #000000;         /* Black text */\n}";

    private static final String DARK_MODE_CSS = "darkmode.css";
    private static final String LIGHT_MODE_CSS = "lightmode.css";

    // private static final String DARK_MODE_CSS = 
    //     ".root { -fx-background-color: #000000; }\n" +
    //     ".region { -fx-background-color: #333333; -fx-text-fill: #FFFFFF; }";

    // private static final String LIGHT_MODE_CSS = 
    //     ".root { -fx-background-color: #D3D3D3; }\n" +
    //     ".region { -fx-background-color: #FFFFFF; -fx-text-fill: #000000; }";

    
    // Method to toggle theme and apply it to the entire stage
    public static void toggleTheme(Stage stage) {
        isDarkMode = !isDarkMode;
        applyTheme(stage.getScene(),stage);  // Apply theme to the Scene
    }

    // Method to apply the current theme to the scene
    public static void applyTheme(Scene scene, Stage primaryStage){
        scene.getStylesheets().clear(); 
        // String css = isDarkMode ? DARK_MODE_CSS : LIGHT_MODE_CSS;
        // scene.getRoot().setStyle(css);
        if (isDarkMode) {
            scene.getStylesheets().add(DARK_MODE_CSS);
        } else {
            scene.getStylesheets().add(LIGHT_MODE_CSS);
        }
    }
}

// import javafx.scene.Scene;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.StackPane;
// import javafx.scene.paint.Color;
// import javafx.stage.Stage;

// public class ThemeManager {
//     private static boolean isDarkMode = false;  // Default is light mode

//     // Method to toggle theme and apply it to the entire stage
//     public static void toggleTheme(Stage stage) {
//         isDarkMode = !isDarkMode;
//         applyTheme(stage.getScene(),stage);  // Apply theme to the Scene
//     }

//     // Method to apply the current theme to the scene
//     public static void applyTheme(Scene scene,Stage stage) {
//         StackPane root = (StackPane) scene.getRoot();  // Get the root container of the scene

//         if (isDarkMode) {
//             scene.setFill(Color.BLACK);  // Set scene background to black
//             root.setStyle("-fx-background-color: black;");  // Set root background to black
//             applyDarkTheme(scene);
//         } else {
//             scene.setFill(Color.WHITE);  // Set scene background to white
//             root.setStyle("-fx-background-color: white;");  // Set root background to white
//             applyLightTheme(scene);
//         }
//     }

//     // Apply dark theme to all nodes within the scene (if necessary)
//     public static void applyDarkTheme(Scene scene) {
//         for (var node : scene.getRoot().getChildrenUnmodifiable()) {
//             if (node instanceof Region region) {
//                 region.setStyle("-fx-background-color: black; -fx-text-fill: white;");
//             }
//         }
//     }

//     // Apply light theme to all nodes within the scene (if necessary)
//     public static void applyLightTheme(Scene scene) {
//         for (var node : scene.getRoot().getChildrenUnmodifiable()) {
//             if (node instanceof Region region) {
//                 region.setStyle("-fx-background-color: white; -fx-text-fill: black;");
//             }
//         }
//     }
// }
