Save Query App
Overview
Save Query App is a JavaFX-based application designed for efficiently managing, organizing, and editing SQL queries. It provides a structured approach to saving queries in different categories and includes a powerful Query Editor that mimics an IDE with line numbers and SQL syntax highlighting.

Features
Query Management
Pending Queries:
Saved as TXT files.
Queries that are under development or awaiting final approval.
Deployed Queries:
Saved as TXT files.
Finalized queries that have been executed or deployed.
Temp Queries:
Stored in a JSON file.
Temporary queries used for quick tests or short-term access.
Home Screen (SaveQueryController)
Save Queries Tab:
Task Name & Database Name: Input fields for defining query metadata.
Is Temp Query: Checkbox to mark a query as temporary.
Query Editor Integration: A dedicated area (using a custom code editor) is provided to write or modify SQL queries.
Save/Clear Buttons: Options to save the query or clear the input fields.
View Queries Tab:
Three Sections:
Pending Queries: Lists all pending queries with options to open, delete, or move queries to deployed.
Temp Queries: Displays temporary queries stored in JSON format.
Deployed Queries: Lists deployed queries with similar management options.
Search and Filter: Each section (Pending and Deployed) includes a search field for filtering queries.
Refresh Button: Easily refresh the query lists to view the most recent changes.
Query Editor (QueryEditorController)
IDE-like Editing Experience:
Displays queries with line numbers for easier navigation.
Provides syntax highlighting for SQL keywords, making it easier to read and edit queries.
Editing Tools:
Save Button: Save any modifications made to the query.
Copy Query Button: Quickly copy the query text.
Close Button: Exit the editor once editing is complete.
Application Workflow
Save Queries Tab:

Input the task name and database name.
(Optional) Check the "Is Temp Query" box to save the query as a temporary query (stored in JSON).
Write or paste your SQL query in the provided editor.
Click Save to store your query in the appropriate category (TXT or JSON).
View Queries Tab:

Use the dedicated sections to view Pending, Temp, and Deployed queries.
Utilize the search fields to filter queries by name.
Click the refresh button to update the list if changes have been made.
Manage queries (open, delete, move) directly from the list.
Query Editor:

When a query is opened from the list, the Query Editor scene loads with the full query.
The editor displays line numbers and highlights SQL keywords for an enhanced coding experience.
Save changes, copy the query, or close the editor using the buttons provided.
Installation & Setup
Prerequisites
Java Development Kit (JDK) 11 or higher
JavaFX SDK
An IDE such as IntelliJ IDEA, Eclipse, or NetBeans configured for JavaFX projects.
Running the Application
Clone the Repository:
sh
Copy
Edit
git clone https://github.com/yourusername/save-query-app.git
Open the Project:
Import the project into your preferred IDE.
Configure JavaFX:
Ensure the JavaFX SDK is properly set up in your project build configuration.
Run the Main Application File:
Launch the application from your IDE.
Future Enhancements
Database Integration: Implement a database system for more robust query storage.
Direct Query Execution: Add functionality to execute SQL queries directly from the application.
Enhanced UI/UX: Incorporate additional customization options, themes, and improved error handling.
