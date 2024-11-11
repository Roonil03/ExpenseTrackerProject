# Expense Tracker Application
## Overview
The Expense Tracker Application is a JavaFX-based personal finance manager that allows users to track their daily expenses, categorize them, and visualize their spending with charts. It features different user roles (Normal and Premium), authentication, expense tracking, and the ability to toggle between dark and light themes. The app is built using JavaFX and covers a range of Java concepts such as Object-Oriented Programming, multi-threading, exception handling, and GUI programming.

## Features
1. User Authentication
    - Users can sign up with a username and password.
    - Sign in allows authenticated users to access their personalized expense tracking dashboard.
    - Two types of users:
        1. Normal User: Limited features and categories.
        2. Premium User: Advanced features with the ability to add custom categories.

2. Expense Tracking
    - Users can add expenses with an amount and a category.
    - Categories like "Food," "Travel," "Entertainment," and "Others" are supported for normal users. Premium users can add custom categories.
    - Expenses are displayed in a list view.
    - Users can clear all expenses at any time.

3. Data Visualization
    - A bar chart is displayed to visualize the expenses by category.
    - The chart updates dynamically as new expenses are added.

4. Dark Mode and Light Mode
    - Users can toggle between dark and light themes, providing a customizable user experience.

5. Sign Out Functionality
    - Users can sign out at any time and return to the sign-in screen.


## Usage
### Steps to Use the Application
- <b>Launch the Application:</b> Upon running the application, the user will see a sign-up screen.
- <b>Sign Up:</b> If you are a new user, fill in your username and password and select either "Normal User" or "Premium User." Click the "Sign Up" button.
- <b>Sign In:</b> If you already have an account, click "Sign In" and enter your credentials.
- <b>Add Expenses:</b> Once signed in, you can add an expense by entering the amount and selecting or typing the category.
- <b>Clear Expenses:</b> You can clear all expenses by clicking "Clear All."
- <b>View Bar Chart:</b> To view a visual representation of your expenses, click "Show Bar Chart."
- <b>Toggle Theme:</b> Use the "Toggle Theme" button to switch between dark and light modes.
- <b>Sign Out:</b> If you want to sign out, click the "Sign Out" button.

## How to Download and Run the Application
- <b>Download the Source Code:</b><br>
Clone the repository or download the .zip file containing the project files.
- <b>Dependencies:</b><br>
This application uses JavaFX for GUI development. Ensure that JavaFX is set up in your project.
- <b>Compile and Run:</b><br>
Open the project in your favorite IDE (Eclipse, IntelliJ, etc.) or compile from the terminal.<br>
If using the terminal, run the following commands:
    ```
    javac -cp path_to_javafx_libs *.java
    java -cp .:path_to_javafx_libs javafxpackaged.Main
    ```
- <b>Optional:</b><br> To change themes, ensure that ``darkmode.css`` and ``lightmode.css`` are available in your project folder, as they are used to apply themes.

## Conclusion
The Expense Tracker application is an excellent example of a well-structured Java application that integrates various Java programming concepts. It demonstrates key object-oriented principles such as inheritance, interfaces, exception handling, multi-threading, and GUI development using JavaFX. This project is a useful tool for anyone looking to manage personal finances and provides a comprehensive learning experience in Java development.