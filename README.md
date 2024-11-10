# team-project-team-40

# Library Management System

The Library Management System is a Java-based software application that allows users to manage their personal book collections. It provides features for adding, and removing books in personal database and exploring books from both general and personal database. Users can also rate and review books in their personal collection.

## Features

- **Language Internalization**: Users can select their preferred language for the application interface
- **Login Process**:Users can log in using their username and password.For other username-password combinations, the existence of the username is checked in the database. If the username doesn't exist, login fails.
- **Registration Process**: the username doesn't exist in the database, users can register by providing necessary details.
- **Explore General Database**: Users can explore a general database of books, view titles, authors, ratings, and reviews.
- **Access Personal Database**: Users can access their personal database, which includes the books they have added, along with their personal ratings and reviews.
- **Add and Remove Books**: Admins can add or remove books from the general database.
- **User Authentication**: Users can log in or sign up with a username and password. Admins have special privileges.
- **Rating and Review**: Users can rate books and add reviews to books in their personal database.
- **Data Management**: Allows users to manage their personal data, including reviews and ratings.
- **Intuitive Interface**: User-friendly interface with easy navigation and data entry.
- **Navigable Tables**:Users can smoothly scroll through the tables to view entries without any hassle.
- **Search Bar**:A search bar is provided for quickly finding specific books or authors within the tables.
- **Sortable Columns**:Each column in the tables is sortable, allowing users to organize the data based on their preferences.
- **Sorting Functionality**:Clicking on the "Title" heading in the table sorts the entries by ascending book title order. Clicking again sorts them in descending order, and clicking for the third time restores the original order.

## Installation and Usage
To run the application, follow these steps:

1. Clone this repository to your local machine.
2. Open the project in IntelliJ IDEA or your preferred Java IDE.
3. Select your prefered language to begin.
4. Start by running 'LanguageSelectionWindow' and select your language.
5. Then by running the `SignupWindow` class create a new user account.
6. After signing up, you can log in using the `LoginWindow` class.
7. If you are an admin, use the following credentials to access admin privileges:
    - **Username**: admin
    - **Password**: admin
8. Explore the features and functionalities of the application, including adding reviews, ratings, and managing user accounts.

## Frameworks and Libraries

- **Java Swing:** Used for building the graphical user interface (GUI) of the application.
- **JUnit:** Used for unit testing the application components.


## Development Process
## Development Process

The software is developed in Java using Swing for the graphical user interface (GUI). <br>
It consists of several classes organized into packages:

- `src`: Contains the main Java classes for the application.
- `data`: Contains data files for books and user data.
- `media`: Contains image files for GUI elements.

The development process involved:

1. Designing the class structure to represent books, databases, and user data.
2. Implementing GUI components using Swing for a user-friendly interface.
3. Writing methods to handle database operations such as adding, removing, and loading books.
4. Implementing user authentication and authorization features.
5. Testing and debugging the application to ensure functionality and reliability.
6. Daily group discussions were held to review progress, discuss challenges, and plan tasks.
7. Group members collaborated on coding tasks, shared updates, and made commits to GitHub regularly.

## Application Documentation
 1.  The books list stores instances of the Book class, representing the books in the general database.
 The getBooksAsArray method converts the book data into a 2D array, making it suitable for display in a table format. This method also calculates the average rating for each book and retrieves the reviewers' usernames for each book
 The loadFromCSV method reads book data from a CSV file and populates the books list with Book objects. It handles cases where titles or authors may be missing, as well as cases where multiple titles are listed for a single author
2.  We handle the storage and retrieval of personal book data in CSV format (personal_database.csv).
 The saveData method saves the personal book data to the CSV file, ensuring no duplicate entries for the same user and book.
3.  The isUsernameTaken method checks whether a username is already taken during the registration process.
 It reads each line of the user data file and compares the provided username with existing usernames, returning true if a match is found.
 The addUser method adds a new user to the system by appending their username, password (as a character array), and a unique user ID to the CSV file (user_data.csv).
4.  The constructor initializes the RatingCellEditor with a reference to a PersonalDatabase instance, which is used to save ratings to the database.
"TableCellEditor" implementations commonly include validation logic to ensure that only valid data is entered into cells. This may involve checking data types, enforcing constraints, and providing feedback to the user if input is invalid.
5.  "handleTableClick" Method : t iterates through the array of reviewers associated with the clicked cell, calculating the width of each reviewer's name to determine if the click falls within the bounds of that reviewer's name.
 "openUserDetailsWindow" Method : It passes the reviewer's name, book title, and author as parameters to the UserDetailsWindow constructor.

## Presentation

- Watch the presentation of this application on YouTube:

## Contributors
- Javid Jabiyev
- Latafat Guliyeva
- Zeyneb Gasimova
- Leyla Mahalova