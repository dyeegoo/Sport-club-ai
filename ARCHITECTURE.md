# SportClub AI - Software Architecture Document

## 1. Executive Summary
SportClub AI is a scalable, offline-capable Android application designed for martial arts and sports clubs. It supports three primary roles (Owner, Coach, Student) and leverages Firebase for backend infrastructure, real-time sync, and scalable storage, supporting up to 100,000+ active users.

## 2. Suggested Technologies
*   **Platform:** Android (Native)
*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose
*   **Architecture Pattern:** Clean Architecture + MVVM (Model-View-ViewModel)
*   **Dependency Injection:** Hilt (Dagger)
*   **Asynchronous Programming:** Kotlin Coroutines & Flow
*   **Backend & Database:** Firebase (Auth, Firestore, Storage)
*   **Serverless Logic:** Firebase Cloud Functions (Node.js/TypeScript)
*   **Push Notifications:** Firebase Cloud Messaging (FCM)
*   **AI Integration:** Google Cloud Vertex AI / Gemini API

## 3. Overall Project Architecture
The application will follow **Clean Architecture** principles strictly divided into three layers to ensure separation of concerns and testability:
1.  **Presentation Layer:** Contains Jetpack Compose UI, ViewModels, and UI state management.
2.  **Domain Layer:** Contains pure Kotlin business logic, Use Cases (Interactors), and repository interfaces. Agnostic of any Android or Firebase dependencies.
3.  **Data Layer:** Implements repository interfaces. Handles data orchestration between Firestore (Remote Data Source) and local caching.

## 4. Folder Structure (Android Codebase)
```text
app/src/main/java/com/sportclubai/
├── di/                     # Hilt Modules (Network, Database, Repositories)
├── core/                   # Common utilities, extensions, base classes
├── data/
│   ├── remote/             # Firebase data sources, DTOs
│   ├── local/              # Room DAOs (if complex offline needed beyond Firestore)
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Pure domain entities (User, Class, Club)
│   ├── repository/         # Repository interfaces
│   └── usecase/            # Business logic (e.g., BookClassUseCase)
└── presentation/
    ├── theme/              # Jetpack Compose Theme, Typography, Colors
    ├── common/             # Reusable Compose components (Buttons, Cards)
    ├── auth/               # Login, Signup, Onboarding
    ├── owner/              # Admin dashboard and management screens
    ├── coach/              # Coach roster, attendance, evaluation screens
    └── student/            # Student schedule, progress, payments
```

## 5. Database Design & Firestore Collections
Firestore is a NoSQL document database. We will structure data to avoid deep nesting and favor shallow queries with indexing.

*   **`users`**: `{ uid, role (owner|coach|student), name, email, phone, createdAt, fcmToken }`
*   **`clubs`**: `{ clubId, name, ownerId, address, subscriptionPlan, isActive }`
*   **`memberships`**: `{ membershipId, clubId, userId, role, beltRank, status }`
*   **`classes`**: `{ classId, clubId, coachId, name, schedule (array), capacity, currentEnrollment }`
*   **`sessions`** (sub-collection of classes or top-level linked by classId for time-series attendance): `{ sessionId, classId, date, status }`
*   **`attendance`**: `{ attendanceId, sessionId, studentId, status (present|absent|late), notes }`
*   **`payments`**: `{ paymentId, studentId, clubId, amount, dueDate, status (paid|pending|overdue) }`

## 6. Data Relationships
*   **Users to Clubs:** Many-to-Many handled via the `memberships` collection. A user can be a student in one club and a coach in another.
*   **Classes to Coaches:** Many-to-One.
*   **Attendance to Sessions & Students:** Many-to-One relationship mapped relationally via IDs to prevent document bloat.

## 7. User Roles & Permissions
*   **Club Owner (Admin):** Full CRUD access to their specific `clubId`. Can invite coaches and students, view financials, edit schedules, and access AI analytics.
*   **Coach:** Read access to assigned `classes` and `memberships` (students). Write access to `attendance` and student evaluations (e.g., belt test grading).
*   **Student:** Read access to their own `memberships`, available `classes`, and `payments`. Write access to enroll in classes.

## 8. Security Rules (Firestore)
Security will utilize **Firebase Custom Claims** set via a secure Cloud Function when an Owner invites a user.
```javascript
// Example Rules Structure
match /clubs/{clubId} {
  allow read: if request.auth != null && isMemberOf(clubId);
  allow write: if request.auth != null && isOwnerOf(clubId);
}
match /classes/{classId} {
  allow read: if request.auth != null; // Members can view schedule
  allow write: if request.auth != null && (isOwnerOf(resource.data.clubId) || isCoachFor(classId));
}
```

## 9. Navigation Flow & Main Screens
*   **Onboarding:** Splash -> Login/Register -> Role Routing.
*   **Owner Flow:** Dashboard (KPIs) -> Manage Members -> Manage Classes -> Financial Reports.
*   **Coach Flow:** Dashboard (Today's Classes) -> Class Details -> Take Attendance -> Evaluate Student.
*   **Student Flow:** Dashboard (Next Class, Current Belt) -> Schedule/Booking -> Progress/Videos -> Payments.

## 10. AI Features
1.  **AI Attendance & Churn Prediction:** A Firebase Cloud Function runs a scheduled Vertex AI job analyzing attendance patterns to flag students at high risk of dropping out (Churn) to the Owner.
2.  **Smart Schedule Optimization:** AI analyzes peak attendance times and waitlists to suggest optimized class schedules to the Owner.
3.  **Video Form Analysis (Future):** Students upload short clips of martial arts forms (Katas/Poomsae). Gemini 1.5 Pro (via Cloud Functions) analyzes the video and provides text-based feedback on stance and technique.

## 11. Offline Synchronization Strategy
*   **Firestore Persistence:** Enabled by default. Cache size will be configured to 100MB to prevent device bloat.
*   **Optimistic UI:** When a coach takes offline attendance, Jetpack Compose UI updates immediately. Firestore queues the write operation and syncs automatically when the network returns.
*   **Critical Data:** We will use `Source.CACHE` for offline reads of static data (Club info, Schedule) to ensure immediate app launch, syncing in the background.

## 12. Backup Strategy
*   **GCP Automated Backups:** Enable Point-in-Time Recovery (PITR) for Firestore.
*   **Scheduled Exports:** A Cloud Scheduler job triggers a Cloud Function daily to export the Firestore database to a coldline Google Cloud Storage bucket for disaster recovery.

## 13. Image Storage Strategy
*   **Service:** Firebase Cloud Storage.
*   **Structure:** `/clubs/{clubId}/profiles/{userId}.jpg`
*   **Optimization:** Utilize the **Firebase "Resize Images" Extension**. The app uploads the raw image, the extension automatically generates thumbnails (e.g., 200x200), and the app retrieves the optimized URL to save bandwidth and improve list rendering performance.

## 14. Notification Architecture
*   **Triggers:** Handled entirely by Firebase Cloud Functions.
*   **Examples:**
    *   *System-triggered:* "Your payment is due tomorrow." (Cron Job).
    *   *Event-triggered:* Coach changes class time -> triggers FCM to all students enrolled in `classes/{classId}`.
*   **Client:** Handled via standard Android Notification Channels (e.g., "Alerts", "Reminders", "Updates").

## 15. Future Scalability (100,000+ Users)
*   **Pagination:** UI will use Jetpack Paging 3 combined with Firestore `startAfter()` cursors for all lists (e.g., Student Roster).
*   **Data Aggregation:** Cloud Functions will aggregate counts (e.g., total active students) into the `clubs` document to avoid reading thousands of membership documents just to show a KPI.
*   **Sharding:** If payment logs or attendance records exceed 1 write/second, we will implement distributed counters.

## 16. Complete Development Roadmap
*   **Phase 1: Foundation (Weeks 1-3)**
    *   Project setup, CI/CD pipeline (GitHub Actions -> Firebase App Distribution).
    *   Firebase Auth setup and Cloud Functions for Custom Claims.
    *   Base architecture (Hilt, Compose Theme, Navigation graph).
*   **Phase 2: Core Workflows (Weeks 4-7)**
    *   Firestore data modeling implementation.
    *   Owner Dashboard & Member Management.
    *   Class creation and scheduling modules.
*   **Phase 3: Coach & Student Portals (Weeks 8-11)**
    *   Coach class rosters and offline-capable attendance taking.
    *   Student booking engine and progress tracking.
    *   Image uploads and profile management.
*   **Phase 4: Notifications & Payments (Weeks 12-14)**
    *   Stripe SDK integration for payments.
    *   Cloud Functions for FCM push notifications.
*   **Phase 5: AI Integration & Polish (Weeks 15-18)**
    *   Vertex AI integration for churn prediction.
    *   Performance profiling (Macrobenchmark).
    *   Play Store Beta release and staged rollout.
