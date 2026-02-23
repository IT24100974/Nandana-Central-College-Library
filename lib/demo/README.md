# Nandana Central College Library Management System

A comprehensive Web-based Library Management System built with Spring Boot, MySQL, and Thymeleaf that provides role-based access for multiple user types with advanced features.

## 🚀 Enhanced Features

### 6 Major Functions Implemented:

1. **Book Management** - Complete CRUD operations for books (Librarians & Assistants)
2. **Borrow and Return Tracking** - Automated tracking with fine calculation (Staff)
3. **Book Review & Rating System** - Students can rate and review books
4. **Notification Management** - System-wide announcements and alerts (Admins)
5. **User Account and Role Management** - Complete user lifecycle management (System Admin)
6. **Rule Configuration Module** - Flexible library policy management (Admins)

### Additional Advanced Features:
- **Book Reservation System** - Reserve unavailable books
- **Fine Management** - Automated overdue fine calculation
- **Multi-role Authentication** - 5 distinct user roles
- **Comprehensive Reporting** - System analytics and insights
- **Notification System** - Due date reminders and alerts

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.6
- **Database**: MySQL 8.0
- **Security**: Spring Security 6
- **Frontend**: Thymeleaf + Bootstrap 5
- **Build Tool**: Maven
- **Java Version**: 21

## 📋 Prerequisites

Before running the application, ensure you have:

1. **Java 21** or higher installed
2. **MySQL Server** running on localhost:3306
3. **Maven** installed (or use the included Maven wrapper)

## 🔧 Setup Instructions

### 1. Database Setup
```sql
-- The application will automatically create the database
-- Just ensure MySQL is running on localhost:3306
-- Default configuration uses:
-- Username: root
-- Password: (empty)
-- Database: library_management (auto-created)
```

### 2. Clone and Run
```bash
# Navigate to the project directory
cd c:\Users\isuru\Downloads\lib\demo

# Run the application using Maven wrapper
./mvnw spring-boot:run

# Or if you have Maven installed
mvn spring-boot:run
```

### 3. Access the Application
- **URL**: http://localhost:8080
- **System Admin**: admin / password
- **Librarian**: librarian / password  
- **Library Assistant**: assistant / password
- **Deputy Principal**: deputy / password
- **Student**: student / password

## 👥 User Roles & Permissions

### System Admin Features:
- ✅ Complete user management (create, edit, disable users)
- ✅ System-wide notification management
- ✅ Library rules configuration (borrow limits, fines, etc.)
- ✅ Comprehensive system reports and analytics
- ✅ Role assignment and permission management
- ✅ System monitoring and health checks

### Librarian Features:
- ✅ Dashboard with library statistics
- ✅ Complete book management (add, edit, delete)
- ✅ Monitor all borrowing activities
- ✅ Track overdue books and fines
- ✅ Generate library reports
- ✅ Manage book reservations

### Library Assistant Features:
- ✅ Assist with book operations
- ✅ Help students with borrowing/returning
- ✅ View library statistics
- ✅ Limited book management access
- ✅ Support daily library operations

### Deputy Principal Features:
- ✅ High-level system overview
- ✅ Library performance monitoring
- ✅ Strategic reports and insights
- ✅ System usage analytics
- ✅ Policy oversight capabilities

### Student Features:
- ✅ Personal dashboard with borrowed books
- ✅ Advanced book browsing and search
- ✅ Book borrowing and returning
- ✅ Book reviews and ratings system
- ✅ Reservation system for unavailable books
- ✅ Personal borrowing history and due date tracking

## 📊 Database Schema

The application automatically creates the following tables:

- **users** - User accounts with 5 different roles
- **books** - Book catalog with availability tracking
- **borrow_records** - Borrowing transactions and history
- **book_reviews** - Student reviews and ratings for books
- **notifications** - System-wide announcements and alerts
- **library_rules** - Configurable library policies and settings
- **book_reservations** - Book reservation system
- **fines** - Automated fine calculation and tracking

## 🔒 Security Features

- Role-based access control (RBAC)
- Password encryption using BCrypt
- Session management
- CSRF protection
- Access denied handling

## 📱 User Interface

- **Responsive Design** - Works on desktop and mobile devices
- **Bootstrap 5** - Modern and clean UI components
- **Font Awesome Icons** - Intuitive iconography
- **Flash Messages** - User feedback for actions
- **Search & Filter** - Easy book discovery

## 🚦 Business Rules

- Students can borrow maximum 5 books at a time
- Borrowing period is 14 days
- Books become overdue after the due date
- Students cannot borrow the same book twice simultaneously
- Available copies are automatically managed

## 🧪 Sample Data

The application comes with pre-loaded sample data:

### Sample Books:
- Java: The Complete Reference (Programming)
- Clean Code (Programming)
- Introduction to Algorithms (Computer Science)
- The Great Gatsby (Literature)
- To Kill a Mockingbird (Literature)

### Default Users:
- **Librarian**: librarian/password
- **Student**: student/password

## 🔧 Configuration

### Database Configuration (application.properties):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_management?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### To change database credentials:
1. Update `src/main/resources/application.properties`
2. Modify the datasource username and password
3. Restart the application

## 🐛 Troubleshooting

### Common Issues:

1. **MySQL Connection Error**:
   - Ensure MySQL is running on port 3306
   - Check username/password in application.properties
   - Verify MySQL service is started

2. **Port 8080 Already in Use**:
   - Change server port in application.properties: `server.port=8081`
   - Or stop the service using port 8080

3. **Build Errors**:
   - Ensure Java 25 is installed and JAVA_HOME is set
   - Run `./mvnw clean install` to rebuild

## 📈 Future Enhancements

Potential improvements for the system:
- Email notifications for overdue books
- Book reservation system
- Fine calculation for overdue books
- Advanced reporting and analytics
- Book recommendation system
- Multi-library support

## 🤝 Contributing

This is a demo project for Nandana Central College. For modifications:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is created for educational purposes for Nandana Central College Library Management System.

---

**Developed for Nandana Central College** 📚
*Simple, Efficient, User-Friendly Library Management*
