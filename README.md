
# SitPass

## Project Type:
Web-based gym facility reservation and review system using a microservices architecture.

## Backend:
- **Technology:** Java (Spring Boot) with RESTful services
- **Authentication:** JWT tokens for user authentication and role-based access control
- **Database:** MySQL or PostgreSQL for storing users, facilities, bookings, and reviews
- **Logging:** Log4j for event logging

## Frontend:
- **Technology:** Angular with Tailwind CSS for responsive and modern UI

## Key Features:
- **User Authentication & Authorization:** Secure user authentication with JWT tokens, and role-based access control (Admin, Manager, User)
- **Facility Management:** Create, update, and manage gym facilities, including working hours and available disciplines
- **Booking System:** Users can book facilities, and administrators/managers can manage bookings
- **Review & Rating System:** Users can leave reviews and ratings for facilities, but only if they’ve previously made a booking
- **Manager Deletion Restriction:** Managers can’t be deleted if they have active tasks or bookings

## Tools & Infrastructure:
- **Backend Framework:** Spring Boot with Maven for build and deployment
- **Containerization:** Docker & Docker Compose for containerized deployment (optional)
- **Version Control:** Git (GitHub) for source code management and collaboration
- **API Communication:** RESTful services for communication between frontend and backend
- **Database Management:** MySQL or PostgreSQL for relational data storage

## Setup & Installation:

### Prerequisites:
- **Java** (JDK 11 or later)
- **MySQL** or **PostgreSQL** database setup
- **Docker** (optional for containerized deployment)
- **Node.js** (for frontend Angular setup)
- **Angular CLI** (for frontend setup)

### Installation Steps:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Strahinja15/SitPass.git
   cd sitpass
