# 01Blog: A Fullstack Social Blogging Platform for Students

## 🚀 Overview

**01Blog** is a social blogging platform built for students to document, share, and engage with each other's learning journeys, discoveries, and progress. This fullstack application provides a secure environment for content sharing, following fellow students, and meaningful discussion, complete with administrative tools for content moderation.

This project utilizes **Java Spring Boot** for a robust backend and **Angular** for an interactive and responsive frontend interface.

---

## 💻 Technologies Used

### Backend (Java Spring Boot)
* **Framework:** Java Spring Boot
* **Security:** Spring Security (or JWT) for authentication and role-based access control (RBAC).
* **Database:** Relational SQL Database (e.g., **PostgreSQL** or **MySQL**)
* **Persistence:** JPA (Java Persistence API) / Hibernate
* **Media Storage:** Secure file system or cloud storage (e.g., AWS S3) for media uploads.
* **APIs:** RESTful API design.

### Frontend (Angular)
* **Framework:** Angular
* **Styling/UI:** Angular Material or Bootstrap (for a responsive design)
* **Interaction:** HTML5, CSS3, TypeScript

---

## 🛠️ Project Structure and Features

The application is split into a robust backend that handles data, security, and business logic, and a dynamic frontend that manages the user experience.

### ⚙️ Backend (Spring Boot REST API)

#### 🔑 Authentication & Security
* **User Management:** Secure **registration** and **login** with secure password hashing.
* **Access Control:** Implementation of **Role-Based Access Control (RBAC)** to distinguish between standard `user` and `admin` roles.
* **Protected Routes:** All endpoints are secured according to user roles.

#### 👤 User Block & Subscription
* **User Profile ("Block"):** Public profile page listing all of a user's published posts.
* **Subscription:** Users can **subscribe** to other profiles.
* **Notifications:** System for notifying subscribed users when new posts are published.

#### 📝 Posts Management
* **CRUD Operations:** Users can **create**, **edit**, and **delete** their own posts.
* **Media Support:** Posts support **media uploads** (images or videos) alongside text descriptions.
* **Interactions:** Support for **likes** and **comments** on posts from other users.

#### 🚩 Reporting System
* **Reporting:** Users can **report** profiles or posts for inappropriate content.
* **Data:** Reports include a reason and a timestamp, and are stored securely.
* **Visibility:** Reports are only visible and manageable by administrators.

#### 🛡️ Admin Panel (Protected Routes)
* **User Management:** View and manage all users (e.g., view details, **ban/unban**).
* **Content Moderation:** View and manage all posts (**remove/hide** inappropriate content).
* **Report Handling:** Review and act upon user reports (leading to user bans or post removal).

### 🎨 Frontend (Angular Application)

#### 🏠 User Experience (UI/UX)
* **Homepage Feed:** Displays a feed of posts published by the profiles the user is subscribed to.
* **Personal Block:** Dedicated page for a user to view and fully manage (CRUD) their own posts.
* **Other Blocks:** Ability to view another user’s block and perform **subscribe/unsubscribe** actions.
* **Responsive Design:** Utilizing **Angular Material** or **Bootstrap** for an intuitive and clean interface across different devices.

#### 💬 Post Interaction
* **Engagement:** UI components for **liking** and **commenting** on posts.
* **Real-Time Updates:** Comments should ideally update in real-time or via a quick refresh mechanism.
* **Post Creation:** Interface for writing posts and uploading media with pre-submission **previews**.
* **Display:** Clear display of post timestamps, like counts, and comments.

#### 🔔 Notifications
* **Icon:** A dedicated notification icon to show updates from subscribed profiles.
* **Management:** Ability to mark notifications as **read/unread**.

#### 🚨 Reporting UI
* **Component:** An easy-to-use UI component or modal for submitting a user report with a required reason.
* **Confirmation:** A final confirmation step before submitting the report.

#### 👑 Admin Dashboard
* **Overview:** Clean and organized view of all users, posts, and pending reports.
* **Moderation Tools:** UI for performing moderation actions: **deleting/banning users** and **removing/hiding posts**.

---

## 🏁 How to Run the Project

### 1. Prerequisites
* **Java Development Kit (JDK):** Version 17+ recommended.
* **Node.js & npm:** Latest LTS version.
* **Angular CLI:** Install globally using `npm install -g @angular/cli`.
* **SQL Database:** A running instance of PostgreSQL or MySQL.

### 2. Backend Setup (Spring Boot)

1.  **Clone the repository:**
    ```bash
    git clone [https://learn.zone01oujda.ma/git/abouziani/01blog.git]
    cd 01Blog/backend
    ```
2.  **Configure Database:**
    * Update the database connection details (URL, username, password) in the `src/main/resources/application.properties` (or `application.yml`) file.
    * Example configuration for PostgreSQL:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/01blog_db
        spring.datasource.username=dbuser
        spring.datasource.password=dbpassword
        spring.jpa.hibernate.ddl-auto=update # Use 'create' for first run
        ```
3.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The backend should start on `http://localhost:8080`.

### 3. Frontend Setup (Angular)

1.  **Navigate to the frontend directory:**
    ```bash
    cd ../frontend
    ```
2.  **Install dependencies:**
    ```bash
    npm install
    ```
3.  **Start the development server:**
    ```bash
    ng serve --open
    ```
    The frontend application will compile and open in your browser at `http://localhost:4200`.

### 4. PostgreSQL Setup (Database)
1. **Start the database docker :**
    ```bash
    docker compose up -d
    ```

---

## ⭐ Bonus Features (Recommended)

The following optional features enhance the platform's user experience and administrative capabilities:

* **Real-time updates:** Implement **WebSockets** (e.g., using Spring Boot's WebSocket support) for instant comment updates or live notifications.
* **Infinite Scroll:** Implement infinite loading on the homepage feed to improve content discovery.
* **Dark Mode Toggle:** Offer a customizable dark theme for better viewing in low-light conditions.
* **Basic Analytics:** Provide an admin dashboard view with metrics like total number of posts, most active users, and most reported users.
* **Markdown Support:** Allow users to use Markdown syntax for richer post formatting.

## Author

Abdelhamid BOUZIANI