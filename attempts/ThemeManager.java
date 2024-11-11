import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class ThemeManager {
    private static boolean isDarkMode = false;

    // Method to toggle theme and apply it to the entire stage
    public static void toggleTheme(Stage stage) {
        isDarkMode = !isDarkMode;
        applyTheme(stage.getScene(), stage);  // Apply theme both to Scene and Stage
    }

    // Method to apply the current theme to the entire scene and stage
    public static void applyTheme(Scene scene, Stage stage) {
        if (isDarkMode) {
            stage.setOpacity(0.9);  // Dim the stage slightly for dark mode effect
            scene.setFill(Color.DARKSLATEGRAY);  // Apply dark background to the scene
            applyDarkTheme(scene);
        } else {
            stage.setOpacity(1.0);  // Reset opacity for light mode
            scene.setFill(Color.LIGHTGRAY);  // Apply light background to the scene
            applyLightTheme(scene);
        }
    }

    // Apply dark theme to all nodes within the scene
    public static void applyDarkTheme(Scene scene) {
        for (var node : scene.getRoot().getChildrenUnmodifiable()) {
            if (node instanceof Region region) {
                region.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFFFFF;");
            }
        }
    }

    // Apply light theme to all nodes within the scene
    public static void applyLightTheme(Scene scene) {
        for (var node : scene.getRoot().getChildrenUnmodifiable()) {
            if (node instanceof Region region) {
                region.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000;");
            }
        }
    }
}
