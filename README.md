# BankingApp
JavaBank is a demo banking application built with Java and Spring Boot, offering features like user account management, transaction processing, and PDF bank statements. It utilizes technologies such as Hibernate, iTextPDF, and Docker for seamless deployment and operation.

Key Features:
User Account Management: Create, update, and delete user accounts with detailed information such as name, address, and contact details.
Transaction Management: Perform credit, debit, and transfer operations with detailed transaction records.
Bank Statements: Generate and email bank statements in PDF format for specified date ranges.
Email Notifications: Send email alerts for account creation, transactions, and bank statements using Spring Boot's email capabilities.
Exception Handling: Global exception handling for various application-specific exceptions like BankException, TransactionException, and EmailSendingException.
RESTful API: Expose RESTful endpoints for all operations, making it easy to integrate with other systems.

Technologies Used:
Java 17: The application is built using Java 17, leveraging its latest features and improvements.
Spring Boot: Utilizes Spring Boot for rapid application development and dependency management.
Maven: Manages project dependencies and builds the application.
Hibernate: Provides ORM capabilities for database interactions.
iTextPDF: Generates PDF documents for bank statements.
Docker: Containerizes the application for easy deployment and scalability.
