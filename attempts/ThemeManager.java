import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ThemeManager {
    private static boolean isDarkMode = false;

    // Method to toggle theme and apply it to the scene
    public static void toggleTheme(Stage stage) {
        isDarkMode = !isDarkMode;
        applyTheme(stage.getScene());
    }

    // Method to apply the current theme to the entire scene
    public static void applyTheme(Scene scene) {
        if (isDarkMode) {
            scene.setFill(Color.DARKSLATEGRAY);
            applyDarkTheme(scene);
        } else {
            scene.setFill(Color.LIGHTGRAY);
            applyLightTheme(scene);
        }
    }

    // Apply dark theme to all nodes within the scene
    private static void applyDarkTheme(Scene scene) {
        for (var node : scene.getRoot().getChildrenUnmodifiable()) {
            if (node instanceof Region region) {
                region.setStyle("-fx-background-color: #333333; -fx-text-fill: #FFFFFF;");
            }
        }
    }

    // Apply light theme to all nodes within the scene
    private static void applyLightTheme(Scene scene) {
        for (var node : scene.getRoot().getChildrenUnmodifiable()) {
            if (node instanceof Region region) {
                region.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: #000000;");
            }
        }
    }
}
