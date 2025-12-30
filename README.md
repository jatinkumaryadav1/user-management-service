User Management Service
A CRUD-based Spring Boot application to manage users with JWT-based authentication and protected routes.

Features :
User registration & login
JWT-based authentication
Protected APIs
Role-based access
H2 in-memory database
Unit-tested service layer

Tech Stack :
Java 8+ (Used Java 17)
Spring Boot
Spring Security (JWT)
Spring Data JPA
H2 Database
Maven
JUnit 5 & Mockito

1. Clone the Repository
git clone https://github.com/jatinkumaryadav1/user-management-service.git
cd user-management-service

2. Configuration
Update application.properties:

spring.application.name=user-management-service
server.port=8336

# Database Configuration
spring.datasource.url=jdbc:h2:mem:user_management_db
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true

# JWT Configuration
# (Can be improved using encoded secrets or environment variables)
jwt.secret=${JWT_SECRET:this-is-my$secret$key$for$jwt!jatin110504}

# JWT Expiration
# 1 hour = 60 * 60 * 1000 = 3600000
jwt.expiration=3600000


3. Build the Project
mvn clean install
4. Run the Application
mvn spring-boot:run

Application runs on:
http://localhost:8336

5. Run Unit Tests
mvn test

API Endpoints

Register User (Public)

POST : http://localhost:8336/api/auth/register
Request Body
{
  "username": "akshat",
  "password": "ayadav12",
  "fullName": "John Doe",
  "email": "ayadav@example.com",
  "roles": ["USER"]
}

Login User (Public)

POST : http://localhost:8336/api/auth/login
Request Body
{
  "username": "akshat",
  "password": "ayadav12"
}

Response contains JWT Token

Protected Endpoints (JWT Required)

Include token in header:

Authorization: Bearer <JWT_TOKEN>
Method	Endpoint
GET	/api/users/me
GET	/api/users/{id}
DELETE	/api/users/{id}

Example:
http://localhost:8336/api/users/3

Role-Based Access Control (RBAC) and JWT Authentication
Issue: Unauthorized Access to Admin Routes

There was an issue where admin-only routes (/api/admin/**) were being accessed with a user token. This happened because the role was hardcoded as USER during registration and login, regardless of the user's actual role.

Fix:

The role is now dynamically assigned based on the user's actual role. This ensures:

Users with ADMIN role can access admin routes (e.g., /api/admin/**).

Users with USER role can access user-specific routes (e.g., /api/users/**).

Code Changes:

Previous:

Map<String, Object> claims = Map.of("role", "USER");
String token = jwtUtil.generateToken(userDto.getUsername(), claims);


Updated:

Map<String, Object> claims = Map.of("role", userDto.getRoles());
String token = jwtUtil.generateToken(userDto.getUsername(), claims);

Example Roles:

ADMIN: Access to /api/admin/** routes.
USER: Access to /api/users/** routes.

This update ensures that only users with the appropriate role can access protected resources.

Future Improvements

Thread safety & concurrency handling
Caching (Redis)
Improved exception handling
Memory optimization
API documentation (Swagger)
Docker support

Created By:
Jatin Kumar Yadav
