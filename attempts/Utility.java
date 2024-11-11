public class Utility {
    // Utility method to check if a category is within valid categories
    public static boolean isValidCategory(String category) {
        List<String> validCategories = List.of("Food", "Travel", "Entertainment", "Others");
        return validCategories.contains(category);
    }

    // Additional helper methods can be added here as needed
}
