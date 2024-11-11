import java.util.List;  // Import the List class from java.util package

public class Utility {
    // Utility method to check if a category is within valid categories
    public static boolean isValidCategory(String category) {
        // Create a List of valid categories
        List<String> validCategories = List.of("Food", "Travel", "Entertainment", "Others");
        return validCategories.contains(category);  // Check if the category is valid
    }

    // Additional helper methods can be added here as needed
}
