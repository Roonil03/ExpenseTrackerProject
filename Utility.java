import java.util.*;

public class Utility {
    private static final List<User> userDatabase = new ArrayList<>();

    public static boolean isValidCategory(String category) {
        List<String> validCategories = List.of("Food", "Travel", "Entertainment", "Others");
        return validCategories.contains(category);
    }

    public static void addUser(User user) {
        userDatabase.add(user);
    }

    public static User findUserByUsername(String username) {
        for (User user : userDatabase) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static boolean authenticateUser(String username, String password) {
        User user = findUserByUsername(username);
        return user != null && user.authenticateUser(username, password);
    }

    //Can add more methods if required
}
