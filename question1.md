# Project Title: "Library Management System with Enhanced Features"

## Project Overview:
Develop a Library Management System (LMS) that includes the following features and adheres to the best practices outlined in the CERT Java Coding Standard. The system should utilize object-oriented programming principles, demonstrate effective use of inheritance, interfaces, and generics, incorporate exception handling and multithreading, and include a graphical user interface (GUI) using JavaFX.

## Project Requirements:
1. Class Design and Object-Oriented Principles:
- Library System Structure:
Create a base class LibraryItem with common attributes such as title, author, and ISBN. Derive subclasses Book and Magazine from LibraryItem.
Implement a class Library that manages a collection of LibraryItem objects using an ArrayList.
- Inheritance:
Use inheritance to extend the functionality of LibraryItem into specialized classes like Book and Magazine, implementing features specific to each.
Include an abstract class User with subclasses Librarian and Member to handle different types of users within the system.

2. Interfaces and Packages:
- Interfaces:
Define an interface Borrowable with methods borrowItem() and returnItem(). Implement this interface in classes where items can be borrowed or returned.
- Packages:
Organize your code into appropriate packages, such as library for core classes and gui for JavaFX-related classes.
Use package-level access control and import statements effectively.

3. Exception Handling:
- Implement exception handling to manage errors such as invalid input, item not found, and issues with borrowing/returning items.
- Create custom exception classes (e.g., ItemNotFoundException, InvalidUserException) and use them where appropriate.

4. Multithreading:
- Create a separate thread for handling background tasks such as periodic updates to item availability or processing overdue items.
- Ensure proper synchronization when accessing shared resources between threads.

5. Generics:
- Use generics to create a class GenericCollection<T> that can handle different types of items (e.g., Book, Magazine).
- Demonstrate the use of bounded types, wildcards, and generic methods where applicable.

6. GUI with JavaFX:
- Design a JavaFX-based GUI that includes:
A main application window with options to view, add, or remove items from the library.
Forms for users to borrow or return items.
Controls like buttons, text fields, combo boxes, and list views to interact with the system.
Implement event handling to respond to user actions.

7. CERT Java Coding Standard:
Ensure that your code follows the CERT Java Coding Standard guidelines, focusing on expressions, numeric types, characters and strings, object orientation, and exceptional behavior.


## Deliverables:

- Complete source code with clear documentation and comments.
- A user manual explaining how to use the application.
- A brief report describing the design choices, challenges faced, and how they were addressed.

## Evaluation Criteria:
- Proper implementation of OOP principles and design patterns.
- Effective use of inheritance, interfaces, and generics.
- Robust exception handling and multithreading.
- Well-designed and functional GUI.
- Compliance with CERT Java Coding Standards.
- Code readability, documentation, and overall quality.
