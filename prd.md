# Supplr PRD (Product Requirements Document)

## 1. Project Purpose and Overview

Supplr is a modern, modular, and scalable e-commerce application targeting Android and iOS platforms, developed with Kotlin Multiplatform (KMP) and Jetpack Compose Multiplatform. Users can browse products, add to cart, place orders, add to favorites, and manage profiles. **Advanced admin analytics dashboard** provides business analytics, interactive charts, and real-time data tracking.

---

## 2. Technologies Used

### Core Technologies
- **Kotlin Multiplatform (KMP):** Enables shared code for both Android and iOS.
- **Jetpack Compose Multiplatform:** Modern, declarative UIs with Compose in the UI layer.
- **Koin:** Used for Dependency Injection (DI).
- **Firebase:** Authentication, Firestore (database), Storage (file management), Google Sign-In, and **Push Notification**.
- **Ktor:** Multiplatform HTTP client for network operations and **Stripe API integration**.

### UI & Analytics Technologies
- **Custom Canvas Charts:** Custom drawing components for analytics visualizations.
- **Material3:** Modern UI components and responsive design.
- **Animated Components:** Animations for enhanced user experience.
- **Coil3:** Image loading with **enhanced caching** (memory cache 25%, disk cache 100MB).
- **Loading Shimmer Effects:** Modern loading states with shimmer placeholders.
- **Lazy Loading & Pagination:** Cursor-based pagination for product lists.

### Payment & Integration
- **Stripe SDK:** Payment processing for **Android** and **iOS** (**Android:** Real PaymentSheet, **iOS:** Simulated flow).
- **Coroutines & Flow:** Asynchronous operations and reactive data flow.
- **Navigation Compose:** Screen-to-screen transitions.
- **MessageBar KMP:** Displaying messages to users.
- **Multiplatform Settings:** Cross-platform settings and local storage management.

---

## 3. Project and Module Structure

```
- composeApp/      : Multiplatform main entry point and general UI.
- iosApp/          : iOS app entry point (integrated with SwiftUI).
- feature/
    - auth/        : Login, registration, authentication operations.
    - cart/        : Cart management.
    - home/        : Home screen and navigation.
    - profile/     : User profile management, **settings**, **security features (2FA, password change)**, **account deletion**.
    - admin_panel/ : 📊 Admin analytics dashboard, interactive charts, business metrics, **order management**.
    - manage_product/: Product add, edit, and delete operations for admin.
    - product_details/: Product details and reviews.
    - products_overview/: Product listing featuring new and discounted products on home screen.
    - payment_completed/: Order completed screen and post-order operations.
    - checkout/       : Payment operations and **Stripe integration**.
    - categories/: Category management.
    - favorites/      : Screen and business logic for managing user's favorite products.
    - locations/      : User address management, add/edit, categorization (Home, Work, Other).
    - contact_us/     : Contact page.
    - order_history/  : 📦 User order history, order details, and status tracking.
- data/            : Data layer, repositories, and services.
- shared/          : Shared domain models, utils, constants.
- di/              : Dependency injection modules (Koin).
- navigation/      : Navigation graph and screen routing.
```

---

## 4. Architecture and Clean Architecture

### Layers and Technologies Used

- **Presentation Layer (feature/):**
  - Each screen and ViewModel in separate modules.
  - UI is separated from business logic.
  - Declarative UI with Compose.
  - **Admin Analytics Dashboard:** Real-time data visualization, custom Canvas charts.
  - **Technologies Used:**
    - Jetpack Compose Multiplatform
    - Material3
    - Navigation Compose
    - Koin (koin-compose, koin-compose-viewmodel)
    - MessageBar KMP
    - Coil (coil3, coil3-compose)
    - Kotlin Coroutines & Flow
    - AndroidX Lifecycle ViewModel
    - Custom Canvas Drawing (Analytics charts)

- **Domain Layer (shared/domain/):**
  - Core business models (Product, Customer, CartItem, **Favorite**, **Location**, **PaymentIntent**, **Order**, **DashboardAnalytics**, **DailySummary**, **TopSellingProduct**, **Review**, **ReviewVote**, etc.).
  - Repository interfaces (ProductRepository, **FavoritesRepository**, **LocationRepository**, **PaymentRepository**, **AdminRepository**, **OrderRepository**, **ReviewRepository**, etc.)
  - **Analytics Models:** Dashboard metrics and analytics data structures.
  - **Technologies Used:**
    - Kotlin Multiplatform
    - Kotlinx Serialization
    - Custom interfaces (Repository interfaces)

- **Data Layer (data/):**
  - Repository implementations (e.g., CustomerRepositoryImpl, **FavoritesRepositoryImpl**, **LocationRepositoryImpl**, **PaymentRepositoryImpl**, **AdminRepositoryImpl**).
  - User-specific favorites and **locations** collections in Firebase Firestore.
  - **Analytics Data Processing:** Processing and calculating analytics data from Firestore.
  - **Stripe API integration** for PaymentIntent creation and order management.
  - DTO and data transformations.
  - **Technologies Used:**
    - Firebase (Firestore, Storage, Auth)
    - Ktor
    - Kotlin Coroutines & Flow
    - Kotlinx Serialization
    - Multiplatform Settings
    - Coil3 (coil3-network-ktor) with custom **ImageLoaderFactory** (expect/actual pattern)

- **DI Layer (di/):**
  - Dependency management with Koin.
  - All ViewModels and repositories are injected here.
  - **Technologies Used:**
    - Koin (koin-core, koin-compose)
    - Kotlin Multiplatform

### Clean Architecture Implementation

- **Dependency Direction:** Data → Domain → Presentation (UI, ViewModel).
- **Testability:** Easy mocking with repository interfaces.
- **Modularity:** Each feature in its own module, independently developable.
- **Navigation:** Screen transitions managed with a central NavGraph.
- **Analytics Architecture:** Real-time data processing with separate analytics layer.

### New Feature Modules
  - **admin_panel:** 📊 **Advanced Analytics Dashboard** - Real-time business metrics, interactive charts, revenue analysis, top-selling products, user statistics.
  - **manage_product:** Screen and business logic for admin product add, edit, and delete operations.
  - **products_overview:** Product listing module featuring new and discounted products with quick access on home screen.
  - **payment_completed:** Screen showing order summary and success message after order completion.
  - **favorites:** Users can add products to favorites, view and manage their favorite products on a separate screen.

---

## 5. Admin Analytics Dashboard Features

### 📊 Dashboard Components

#### 5.1. Revenue Analytics
- **Enhanced Line Charts:** Advanced chart visualization with custom Canvas drawing
- **Grid Lines and Axes:** Professional chart appearance
- **Data Points:** Clear visibility with black dots and white borders
- **Fill Area:** Area fill with gradient effect
- **Interactive Elements:** Clickable points and hover effects

#### 5.2. Metrics Cards
- **Animated Entry:** Staggered loading effects
- **Real-time Updates:** Automatic data refresh
- **Key Performance Indicators:**
  - Total Revenue
  - Total Orders
  - Average Order Value

#### 5.3. Top Selling Products
- **Visual Indicators:** Product performance indicators
- **Sales Metrics:** Sales counts and trend analysis
- **Product Cards:** Product information with modern UI

#### 5.4. User Statistics
- **User Growth Tracking:** User growth metrics
- **Engagement Metrics:** User engagement data
- **Demographic Analysis:** Demographic data analysis

### 📅 Date Range Filtering
- **Flexible Time Periods:**
  - Today
  - Last 7 Days
  - Last 30 Days
- **Dynamic Data Loading:** Automatic data loading based on selected date

### 🔄 Real-time Features
- **Auto-refresh:** Automatic data refresh
- **Pull-to-refresh:** Manual refresh support
- **Loading States:** Modern loading with shimmer effects
- **Error Handling:** Retry mechanism for error states

---

## 6. Security and Firebase Rules

### 6.1. Admin Panel Security
- **Role-based Authentication:** Email-based admin control (`isAdmin()` function)
- **Firestore Security Rules:** Comprehensive security rules
- **Data Isolation:** User-specific data access control
- **Audit Trail:** Logging of admin operations

### 6.2. Firestore Collections and Security Rules

#### Analytics Collections
- **`analytics/`** - Analytics data (admin-only access)
- **`admin_dashboard/`** - Dashboard configuration (admin-only)
- **`user_stats/`** - User statistics (admin read-only)
- **`sales_analytics/`** - Sales data (admin-only)
- **`product_analytics/`** - Product performance (admin-only)
- **`admin_logs/`** - Admin operation logs (admin read-only, create-only)

#### Security Rules Features
- **Data Validation:** Required field checks
- **Timestamp Control:** Operation time verification
- **Audit Trail Protection:** Immutable log records
- **Admin ID Verification:** Identity verification of the performing admin

### 6.3. General Security
- **Authentication:** User authentication with Firebase Auth and Google Sign-In.
- **Authorization:** Role-based access control for admin operations (isAdmin).
- **Data Security:** Firestore and Storage access only for authenticated users.
- **Secure Storage:** Sensitive data stored securely locally with Multiplatform Settings.
- **Network Security:** HTTPS usage with Ktor, secure data transfer with Firebase.
- **Payment Security:** **Stripe API keys** secure management (`shared/Consts.kt`), PCI-DSS compliant payment processing.
- **Location Data Security:** User-specific location data, access rights only to own locations.

---

## 7. DevOps and Build

- **Multi-module management with Gradle.**
- **Separate build configurations for Android and iOS.**
- **Platform-specific Firebase integration with GoogleService-Info.plist and google-services.json.**
- **Compilation to two platforms from a single codebase with KMP.**
- **Analytics Build Optimization:** Chart rendering optimizations and performance tuning.

---

## 8. UI/UX and Modern Design

### 8.1. Material3 and Responsive Design
- **Modern, responsive interface with Material3 and Compose.**
- **Animations and transition effects.**
- **Dark and light theme support.**
- **Instant messages and error display to users (MessageBar).**

### 8.2. Admin Dashboard UX
- **Interactive Charts:** User-friendly chart interactions
- **Loading Experience:** Shimmer effects and smooth transitions
- **Responsive Layout:** Design optimized for all screen sizes
- **Error States:** User-friendly error messages and retry options
- **Data Visualization:** Professional business analytics visualization

### 8.3. User Experience Features
- **Easy navigation with BottomBar and TopBar.**
- **User-specific notifications (push notification) support.**
- **Favorites:**
  - Add to favorites with heart icon on top-right in product detail screen.
  - Favorite products listed in favorites screen, remove button on top-right of each card.
  - Favorite products update instantly, user informed with message bar on add/remove operations.
- **Locations:**
  - Easy address add/edit with user address management.
  - Organized address structure with location categories (Home 🏠, Work 🏢, Other 📍).
  - Easily accessible location management from custom drawer.
- **Payment Experience:**
  - **Android:** Native payment experience with real Stripe PaymentSheet.
  - **iOS:** Simulated but realistic payment flow, UX compatible with Android.
  - "Pay with Card" 💳 and "Pay on Delivery" 🚚 options.

---

## 9. Testability

- **Repository interfaces and ViewModels easily testable.**
- **UI tests with mock and fake data.**
- **Common tests with Kotlin Multiplatform.**
- **Analytics Testing:** Chart rendering and data processing tests.
- **Admin Panel Testing:** Role-based access and security rule tests.

---

## 10. Extensibility and Maintenance

### 10.1. Modular Structure
- **Simply open a new module to add a new feature.**
- **Each feature has its own ViewModel and repository.**
- **Easy dependency management with DI.**
- **High code readability and maintainability.**

### 10.2. Recently Added Features
- **📊 Admin Analytics Dashboard (admin_panel):** 
  - Real-time business metrics
  - Interactive revenue charts
  - Enhanced line charts with custom Canvas
  - Top-selling products analysis
  - User statistics tracking
  - Date range filtering
  - Animated components
  - Loading states and error handling

- **🛠️ Admin Order Management (admin_panel - Order Management):**
  - View and search all orders
  - Order status update (PENDING → CONFIRMED → PREPARING → SHIPPED → DELIVERED)
  - Order cancellation (except delivered and cancelled)
  - Order detail view
  - Order tracking with status timeline
  - Role-based authorization

- **📦 Order History (order_history):**
  - User-specific order listing
  - Order detail view
  - Order status tracking
  - Order status timeline
  - Order cards with modern UI

- **⭐ Product Review System (product_details):**
  - User reviews and star ratings
  - Average rating calculation with real-time updates
  - 📷 Review photo upload (Firebase Storage)
  - 👍👎 Helpful/Unhelpful voting system
  - Separate review writing page
  - ReviewPhotosGallery and HelpfulVotingSection components
  - Vote tracking with Firestore `review_votes` collection

- **🎁 Coupon and Discount Code System (checkout):**
  - Coupon code input at checkout
  - Coupon types: Percentage, Fixed Amount, Free Shipping
  - Validation: expiration date, usage limit, minimum order amount
  - Real-time discount calculation
  - Coupon tracking in orders (`couponCode`, `couponDiscount`, `originalAmount`)
  - Usage count increment on order completion
  - Firestore `coupons` collection for coupon management

- **Product Management (manage_product):** Product add, update, and delete operations.
- **Products Overview (products_overview):** Listing of new and discounted products on home screen.
- **Payment Completed (payment_completed):** User information and summary screen after order.
- **Favorites (favorites):** Users can add products to favorites, view and manage favorite products on a separate screen.
- **🗺️ Locations (locations):** User address management system.
- **💳 Stripe Payment Integration:** Multi-platform payment system.

- **👤 Profile Enhancements (profile):**
  - Settings screen with communication preferences (email/push/SMS notifications)
  - Password change functionality with Firebase Auth re-authentication
  - Two-Factor Authentication (2FA) enable/disable
  - Account deletion with password confirmation
  - Birth date for personalized discounts
  - Profile photo URL management
  - Navigation drawer integration

- **🚀 Lazy Loading & Image Caching (shared, data, categories):**
  - **Pagination:** Cursor-based pagination using Firestore `startAfter()` and `limit()`
  - **PaginatedResult:** Generic pagination data class with `items`, `lastDocumentId`, `hasNextPage`
  - **PaginationState:** UI state management (Idle, Loading, LoadingMore, EndReached, Error)
  - **Infinite Scroll:** LazyColumn scroll detection with automatic page fetching
  - **ImageLoaderFactory:** Platform-specific (Android/iOS) image loader with caching
  - **CachedAsyncImage:** Reusable composable with shimmer placeholder and error handling
  - **Memory Cache:** 25% of available memory
  - **Disk Cache:** 100MB in app cache directory
  - **Applied to:** CategorySearchScreen, ProductCard, HomeProductCard

---

## 11. Potential Additions / Improvement Suggestions

### 11.1. Analytics and Admin Panel Improvements
- **Advanced Chart Types:** Bar charts, pie charts, area charts
- **Real-time Notifications:** Critical metric notifications for admin
- **Export Functionality:** PDF/Excel export for reports
- **Advanced Filtering:** Multi-dimensional data filtering
- **Predictive Analytics:** ML-based trend prediction
- **Performance Metrics:** App performance monitoring

### 11.2. General Improvements
- **Increase unit and UI test coverage.**
- **CI/CD pipeline (GitHub Actions, Bitrise, etc.) for automated build and test.**
- **Error and user behavior tracking with Crashlytics and Analytics.**
- **More advanced role and permission management.**
- **Offline-first support (local cache, sync).**
- **More detailed logging and monitoring.**
- **Compose Multiplatform expansion for more platforms (web, desktop).**
- **Extend pagination to more screens:** ProductsOverviewScreen, FavoritesScreen, OrderHistoryScreen.**

---

## 12. Resources

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin DI](https://insert-koin.io/)
- [Firebase](https://firebase.google.com/)
- [Ktor](https://ktor.io/)
- [Coil](https://coil-kt.github.io/coil/)
- [Canvas Drawing in Compose](https://developer.android.com/jetpack/compose/graphics/draw/overview)
- [Firebase Security Rules](https://firebase.google.com/docs/firestore/security/get-started)

---

## 13. Admin Panel Feature Development Guide

### 13.1. Analytics Component Development
```kotlin
// Custom Chart Component Template
@Composable
fun CustomChart(
    data: List<DataPoint>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        // Custom drawing logic
        drawEnhancedChart(data, size)
    }
}
```

### 13.2. Analytics Repository Pattern
```kotlin
interface AdminRepository {
    suspend fun getDashboardAnalytics(dateRange: DateRange): RequestState<DashboardAnalytics>
    suspend fun getUserStatistics(): RequestState<UserStats>
    suspend fun getTopSellingProducts(): RequestState<List<TopSellingProduct>>
}
```

### 13.3. Security Rules Template
```javascript
// Admin Collections Security Template
match /analytics/{document} {
  allow read, write: if request.auth != null && isAdmin();
}
```

### 13.4. Chart Animation Pattern
```kotlin
@Composable
fun AnimatedChart() {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300) // Stagger effect
        isVisible = true
    }
    
    val animatedValue by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800)
    )
    
    // Use animatedValue in drawing
}
```

---

This document includes admin analytics dashboard features and modern chart development practices and can be used as a base reference when developing the project and adding new analytics features.
