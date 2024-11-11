# OOP Lab Project
### Made by Aryan <i>"Roonil03"</i> Vivek, Aaryan Paranjape and Aryan Chandra
Project Date = 14th November<br>

Programming Languages Used:
- Cascading Style Sheets
- Java along with JavaFX

## Java Concepts Covered
The code above demonstrates the application of various Java concepts. Here's how each is implemented:

1. <b>Java data types, type conversions, operators, control statements</b>
    - <i>Data Types:</i> <br>Usage of primitive types like int, double, boolean, and String.
        ```
        private final String username;
        private final String password;
        ```

    - <i>Type Conversions:</i> <br>String to double conversion in the addExpense method.
        ```
        double amount = Double.parseDouble(amountField.getText());
        ```
    - <i>Operators:</i> <br>Arithmetic (+, *) for adding expenses, logical operators for user role checks.
    - <i>Control Statements:</i>
        - if-else for user authentication.
        - try-catch for handling exceptions.
        - Loops (for, while) for iterating over lists and expenses.
    
2. <b>Arrays</b>
    - <i>Usage of Lists:</i><br>Expenses are stored in ArrayList<Expense> and ObservableList<String>.
    - List<T> for user data storage and expense categories.
    - ArrayList is synchronized in Database<T> and UserDatabase for thread-safety.
        ```
        private final List<Expense> expenses;
        ```
        ```
        return new ArrayList<>(items);
        ````

3. <b>Classes & Methods</b>
    - The application uses multiple classes to represent different entities (User, Expense, UserDatabase, etc.).
    - Methods are defined to handle user actions (like adding expenses and toggling themes).
        ```
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
        ```
    - The UserRole class is abstract, with concrete subclasses NormalView and PremiumView.
        ```
        abstract class UserRole {
            private final boolean isPremium;

            public UserRole(boolean isPremium) {
                this.isPremium = isPremium;
            }

            public boolean isPremium() {
                return isPremium;
            }

            public abstract void showView(Stage primaryStage);
        }
        ```

4. <b>Class Inheritance</b>
    - <i>Inheritance:</i><br>UserRole is an abstract class, and both NormalView and PremiumView extend it, showcasing class inheritance in Java.

5. <b>Classes - Access control, Static keywords & Inner Classes</b>
    - <i>Access Control:</i> <br>Various fields and methods are defined with access modifiers (private, public, protected).
        ```
        private final String username;
        private final String password;
        private final List<Expense> expenses;
        ```
    - <i>Static Keyword:</i> <br>UserDatabase is static to maintain a centralized map of users across all instances.<br>The DarkMode toggle is also static to centralize it for the entire program.
        ```
        private static boolean isDarkMode = false;  
        ```
    - <i>Inner Classes:</i> <br>ThemeManager and other UI components are designed as inner classes with proper encapsulation.

6. <b>Interface & Abstract class</b>
    - <i>Interface:</i> <br>UserAuthentication interface defines the contract for user authentication.
    - <i>Abstract Class:</i> <br>UserRole is an abstract class that provides a base for user roles like normal or premium.
        ```
        class User extends UserRole implements UserAuthentication
        ```

7. <b>String Handling</b>
    - <i>String Manipulations:</i> <br>Strings are used for handling user inputs (username, password, category).
    - String comparison is done using equals() and contentEquals() methods.
        ```
        this.username.equals(username) && this.password.equals(password);
        ```

8. <b>Exception Handling</b>
    - Exceptions are caught using try-catch blocks. For example, in the addExpense method, NumberFormatException is caught when the user enters invalid data.
    - Added User Defined Exception to handle if the toggle condition fails:
        ```
        class ThemeApplicationException extends Exception {
            public ThemeApplicationException(String message) {
                super(message);
            }
            public ThemeApplicationException(String message, Throwable cause) {
                super(message, cause);
            }
        }
        ```

9. <b>Multithreaded Programming</b>
    - <i>Multi-threading:</i> <br>A separate thread (Thread updateExpensesThread) is used to update the expense list periodically every second. This is done using Platform.runLater() to safely update the GUI from a background thread.
        ```
        Thread updateExpensesThread = new Thread(() -> {
                while (true) {
                    Platform.runLater(() -> loadExpenses(user));  
                    try {
                        Thread.sleep(1000);  
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        ```
10. <b>Generics Classes Basics</b>
    - <i>Generics:</i> <br>``The Database<T>`` class is a generic class allowing different types of objects to be stored and retrieved. It ensures type safety at compile-time.

11. <b>Java FX – GUI programming</b>
    - <i>JavaFX Components:</i> <br>The application makes extensive use of JavaFX components such as Scene, Stage, TextField, Button, VBox, HBox, Alert, and BarChart.
    - <i>Event Handling:</i> <br>Event listeners are set on buttons like "Sign In", "Add Expense", and "Show Bar Chart".
    - <i>Themes:</i> <br>ThemeManager is used to apply dark and light themes dynamically based on user preference.