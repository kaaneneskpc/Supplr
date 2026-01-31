# 🛒 Supplr - Kotlin Multiplatform E-Commerce App

![Android](https://img.shields.io/badge/Android-✅-green)
![iOS](https://img.shields.io/badge/iOS-✅-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-orange)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-%F0%9F%9A%80-orange)
![Koin](https://img.shields.io/badge/Koin-DI-yellow)
![Open Source](https://img.shields.io/badge/Open%20Source-%E2%9C%94%EF%B8%8F-lightgrey)

---

## 🚀 About the Project

**Supplr** is a modern, modular, and scalable e-commerce application targeting both Android and iOS platforms. Built with Kotlin Multiplatform (KMP) and Jetpack Compose Multiplatform, it allows users to browse products, add to cart, place orders, manage their profiles, and manage their favorite products. Features include **location management**, **Stripe payment integration**, and a comprehensive **admin analytics dashboard** for business insights and product management.

---

## 📦 Features

### 🛒 Customer Features
- 🔥 **Kotlin Multiplatform**: Single codebase for Android & iOS
- 🎨 **Modern UI**: Responsive and animated interface with Jetpack Compose Multiplatform
- 🗂️ **Contact Us**: Customer support and communication
- 🛒 **Cart & Order Management**: Full shopping cart with order tracking
- 👤 **User Profile & Authentication**: Secure user management
- 🏷️ **Category & Product Listing**: Browse products by categories
- 💳 **Checkout & Order Completion**: Seamless payment experience
- ❤️ **Favorites**: Save and manage favorite products
- 🗺️ **Location Management**: Address management with categories (Home, Work, Other)
- 💳 **Stripe Payment Integration**: Real PaymentSheet (Android) / Simulated (iOS)
- 🔔 **Push Notification Support**: Personalized notifications
- 📦 **Order History**: View and track all past orders with detailed status
- ⭐ **Product Reviews**: Rate products, write reviews with photos, helpful/unhelpful voting
- 🎁 **Coupon & Discounts**: Apply coupon codes at checkout (percentage, fixed, free shipping)
- 👤 **Profile Settings**: Communication preferences, security settings, account management
- 🔐 **Security Features**: Two-factor authentication, password change, account deletion
- 🎮 **Gamification**: Leaderboard rankings and Spin Wheel for daily prizes

### 📊 Admin Features
- 🛠️ **Admin Panel**: Comprehensive product and analytics management
- 📈 **Analytics Dashboard**: Real-time business insights with interactive charts
- 📊 **Revenue Analytics**: Daily revenue tracking with enhanced line charts
- 🏆 **Top Products**: Best-selling products analysis
- 👥 **User Statistics**: User growth and engagement metrics
- 📅 **Date Range Filtering**: Flexible time period analysis
- 🔄 **Real-time Data**: Auto-refreshing dashboard with pull-to-refresh
- 🎨 **Interactive Charts**: Custom Canvas-based charts with animations
- 📱 **Responsive Design**: Optimized for all screen sizes
- 🔐 **Secure Access**: Role-based authentication and Firestore security rules
- 🛠️ **Order Management**: View all orders, update status, cancel orders (except delivered)

### 🏗️ Technical Features
- 🔐 **Firebase Authentication & Firestore**
- 🏗️ **Dependency Injection with Koin**
- 🌐 **Networking with Ktor**
- 🖼️ **Image Loading with Coil** - Platform-specific caching (25% memory, 100MB disk)
- 🧪 **Comprehensive Unit Testing** - 50+ tests with Turbine, Coroutines Test
- 📄 **Cursor-based Pagination** - Efficient Firestore data fetching with `startAfter()`
- ♾️ **Infinite Scroll** - Automatic page loading with LazyColumn scroll detection
- ⚡ **Code Splitting** - Module-based lazy loading for faster startup performance

---

## 🏗️ Project & Module Structure

```
- composeApp/      : Main multiplatform entry point and general UI.
- iosApp/          : iOS app entry (integrated with SwiftUI).
- feature/
    - auth/              : Authentication (login, register).
    - cart/              : Cart management.
    - home/              : Home screen and navigation.
    - profile/           : User profile management, settings, security features.
    - admin_panel/       : 📊 Admin analytics dashboard with charts and insights.
    - manage_product/    : Admin add, edit, delete products.
    - product_details/   : Product details.
    - products_overview/ : Home screen new & discounted product listing.
    - payment_completed/ : Order completed screen and post-order.
    - categories/        : Category management.
    - contact_us/        : Contact with Us.
    - favorites/         : Favorites screen and logic.
    - locations/         : User address management with categories.
    - checkout/          : Payment processing with Stripe integration.
    - order_history/     : 📦 Customer order history and tracking.
    - gamification/      : 🎮 Gamification with Leaderboard and Spin Wheel.
- data/            : Data layer, repositories, and services.
- shared/          : Shared domain models, utils, constants.
- di/              : Dependency injection modules (Koin).
- navigation/      : Navigation graph and routing.
```

---

## 🧩 Architecture

<img width="722" height="323" alt="Ekran Resmi 2026-01-31 14 26 24" src="https://github.com/user-attachments/assets/8d97e959-f630-47b6-a174-ecd25c2c6a07" />


- **Clean Architecture**: Data → Domain → Presentation layers
- **Feature-based modules**: Independent development and testing
- **Centralized DI with Koin**: All ViewModels and repositories managed centrally
- **Testability**: Mockable repository interfaces and ViewModels
- **Analytics Architecture**: Dedicated analytics layer with real-time data processing

---

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin Multiplatform (KMP)**
- **Jetpack Compose Multiplatform**
- **Koin** (Dependency Injection)
- **Firebase** (Auth, Firestore, Storage, Push Notification)
- **Ktor** (HTTP Client & Stripe API Integration)
- **Stripe SDK** (Android: PaymentSheet, iOS: Simulated Payment Flow)

### UI & Analytics
- **Material3** (Modern UI components)
- **Custom Canvas Charts** (Analytics visualizations)
- **Coil** (Image Loading)
- **Navigation Compose**
- **MessageBar KMP**
- **Animated Components** (Enhanced user experience)

### Data & Storage
- **Coroutines & Flow**
- **Multiplatform Settings**
- **Firebase Firestore** (with comprehensive security rules)
- **Real-time Data Synchronization**

---

## 📱 Screenshots

### Android

 <img src="https://github.com/user-attachments/assets/bc5907bb-c8f6-4cd4-b6b1-6d59c4e90c61" height = "600px"/> | <img src="https://github.com/user-attachments/assets/12f4c573-fae3-44d6-a0ad-942c38fd4d53" height = "600px"/> | <img src="https://github.com/user-attachments/assets/59242749-0f9b-4583-b650-9ffe7430363b" height = "600px"/> | <img src="https://github.com/user-attachments/assets/6c2a6225-6d65-4068-bf1c-efaf5a9fd255" height = "600px"/> |  <img src="https://github.com/user-attachments/assets/e1d09288-1683-42fb-b063-4ca9bd4c0bda" height = "600px"/>  | <img src="https://github.com/user-attachments/assets/d63efe96-662c-4ced-b264-b2d66c2b4e14" height = "600px"/> |  <img src="https://github.com/user-attachments/assets/6c12e60d-2cb3-4b64-a346-8f823b9e44b3" height = "600px"/> |  <img src="https://github.com/user-attachments/assets/2e4fc889-adfb-4bc9-9f04-da994f8a18cf" height = "600px"/> | <img height="600px" src="https://github.com/user-attachments/assets/ca4b806f-403d-4b8d-aef1-ef8460c7d897" />
| <img height="600px" alt="Screenshot_20250809_183018" src="https://github.com/user-attachments/assets/ddf3c57f-e77a-4351-8f44-8e08cad2ea86" />
| <img height="600px" alt="Screenshot_20250809_183028" src="https://github.com/user-attachments/assets/a15c6d0e-ee37-4789-86af-98c79e88cac7" />
| <img height="600px" alt="Screenshot_20250809_183035" src="https://github.com/user-attachments/assets/927e3818-6777-45ee-9e15-fc71b0376210" />
| <img height="600px" alt="Screenshot_20250809_182947" src="https://github.com/user-attachments/assets/b91f26e1-2762-4952-b6fa-bf04cb5c0981" />
| <img height="600px" alt="Screenshot_20250809_182955" src="https://github.com/user-attachments/assets/fd77f96b-f085-4186-af54-d86721369259" />
| <img height="600px" alt="Screenshot_20250809_183000" src="https://github.com/user-attachments/assets/5f15bce1-c808-4aa5-8eaf-eb07824b49fb" />
| <img height="600px"  alt="Screenshot_20260104_030838" src="https://github.com/user-attachments/assets/1a37a03d-c769-49e9-9958-68171bf71fa2" />
| <img height="600px"  alt="Screenshot_20260103_201954" src="https://github.com/user-attachments/assets/ff039755-10d0-4832-b121-786ead319700" />
| <img height="600px"  alt="Screenshot_20260103_201944" src="https://github.com/user-attachments/assets/adfad7f5-0d67-4147-af53-09d9db1fb26c" />
| <img height="600px" alt="Screenshot_20260118_172443" src="https://github.com/user-attachments/assets/a9157909-0974-4138-b386-bdeaa5c88aed" />
| <img height="600px" alt="Screenshot_20260117_183037" src="https://github.com/user-attachments/assets/6b7a6f79-df63-4dde-9b4c-f1d860b9cec9" />
| <img height="600px" alt="Screenshot_20260107_195052" src="https://github.com/user-attachments/assets/315251fc-aee9-4a64-b631-1cfa197cdcae" />
| <img height="600px" alt="Screenshot_20260107_194446" src="https://github.com/user-attachments/assets/08e6a71e-8ec7-4882-b483-66e83d4cd181" />
| <img height="600px" alt="Screenshot_20260131_195059" src="https://github.com/user-attachments/assets/9fadebe4-e8c3-4540-8b1f-6edfbc27434a" />
| <img height="600px" alt="Screenshot_20260131_174534" src="https://github.com/user-attachments/assets/00e265f8-f3ea-46da-9e70-a5faec6ee7a2" />
| <img height="600px" alt="Screenshot_20260131_174413" src="https://github.com/user-attachments/assets/2623f2e9-2f4f-4b5b-964b-2c81a58f1cea" />
| <img height="600px" alt="Screenshot_20260131_174405" src="https://github.com/user-attachments/assets/82f6ff8f-5820-49f3-8cff-0fa43e5445b7" />


### IOS

<img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 32" src="https://github.com/user-attachments/assets/6d7aba7e-77f9-4e42-8f4b-854d4067119b" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 57" src="https://github.com/user-attachments/assets/9e439136-1356-4b46-b639-64cf27e7607a" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 33" src="https://github.com/user-attachments/assets/dfd10f80-c70e-408f-bdce-cfceafc2cb3a" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 24" src="https://github.com/user-attachments/assets/12ac722a-092c-4b9f-b788-c8e17bbab1b9" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 20" src="https://github.com/user-attachments/assets/baef8140-920f-4eec-bd85-0f19d31f72cc" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 16" src="https://github.com/user-attachments/assets/31916a60-d66b-490f-b5e4-fe5f941e2263" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 10" src="https://github.com/user-attachments/assets/c1da59eb-8073-4a7d-8162-e867c58c0fa5" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 34 05" src="https://github.com/user-attachments/assets/3500a168-ca52-4820-bade-e40dfea11503" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 56" src="https://github.com/user-attachments/assets/b5829429-6788-4687-95f5-0a7e11727adc" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 50" src="https://github.com/user-attachments/assets/c3ce53a6-104a-4fe6-be05-e4e1215f7af5" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 48" src="https://github.com/user-attachments/assets/e760c957-3152-439a-a90e-65084a24d9a4" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 45" src="https://github.com/user-attachments/assets/ab6b9129-29e5-49a8-a031-d3a98de4052b" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 35" src="https://github.com/user-attachments/assets/7d1d90a2-98b6-4cf6-8e39-3c6355d78a80" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2025-08-09 at 18 33 21" src="https://github.com/user-attachments/assets/7806dc19-2105-424b-b925-bd7a94c9ebac" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-04 at 03 13 15" src="https://github.com/user-attachments/assets/807a4c28-260a-4ed2-b294-ec31d3c82bb1" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-03 at 20 35 24" src="https://github.com/user-attachments/assets/35809347-b6c6-4aeb-adc0-9e569061ced8" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-03 at 20 35 16" src="https://github.com/user-attachments/assets/ca247923-6c5b-49a2-bf25-9a88f617bab5" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-18 at 17 27 58" src="https://github.com/user-attachments/assets/ef40afd6-696e-4f29-beec-fef8794c6231" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-07 at 19 54 29" src="https://github.com/user-attachments/assets/6fa0c634-f93e-4bdb-8b89-406d4c10c7c2" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-07 at 19 53 51" src="https://github.com/user-attachments/assets/c134f54a-591d-449a-8b7b-b8f7e77a2678" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-07 at 19 53 46" src="https://github.com/user-attachments/assets/3a4c0760-f4ad-4eea-8804-c8350da1c0d1" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-31 at 19 57 07" src="https://github.com/user-attachments/assets/53f09c6c-a551-4142-8609-863e294142a2" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-31 at 19 57 00" src="https://github.com/user-attachments/assets/0d34d82e-12a2-40d8-aebe-9f51d4ebbcc7" />
| <img height="600px" alt="Simulator Screenshot - iPhone 16 Pro - 2026-01-31 at 19 56 54" src="https://github.com/user-attachments/assets/19ff3cdb-b89f-4386-bc22-1d03fb8616e3" />


## 🎉 Recent Updates

### 🧪 Unit Testing Infrastructure (Latest)
- **Comprehensive Test Suite** - 50+ unit tests across domain, repository, and ViewModel layers
- **Testing Libraries** - kotlin-test, kotlinx-coroutines-test, Turbine
- **Domain Tests** - RequestState, Product, Customer, CartItem, PaginatedResult
- **Fake Repositories** - FakeFavoritesRepository, FakeProductRepository for isolated testing
- **ViewModel Tests** - FavoritesViewModel with StateFlow and callback verification
- **Run Tests** - `./gradlew :shared:testDebugUnitTest :data:testDebugUnitTest`

### 🎮 Gamification System
- **Leaderboard** - View top shoppers and compete with other customers
- **User Rankings** - Track your position among all users
- **Spin Wheel** - Daily spin for amazing prizes and discounts
- **Prize Rewards** - Earn coupons, discounts, and special offers
- **Animated UI** - Smooth animations and engaging user experience
- **Real-time Updates** - Live leaderboard data from Firestore

### ⚡ Code Splitting & Performance (Latest)
- **Module-based Lazy Loading** - Feature modules load on-demand for faster startup
- **ModuleLoader Utility** - Centralized module loading with `ModuleType` enum
- **Lazy Module Types** - Admin, Secondary, and Gamification modules
- **Navigation Integration** - Automatic module loading before screen navigation
- **Startup Optimization** - Only core and auth modules loaded at app start

### 🚀 Lazy Loading & Image Caching
- **Cursor-based Pagination** - Firestore `startAfter()` and `limit()` for efficient data fetching
- **PaginatedResult<T>** - Generic data class with `items`, `lastDocumentId`, `hasNextPage`
- **PaginationState** - UI state management (Idle, Loading, LoadingMore, EndReached, Error)
- **Infinite Scroll** - LazyColumn scroll detection with automatic page fetching
- **CachedAsyncImage** - Reusable composable with shimmer placeholder and error handling
- **Platform-specific ImageLoader** - Android/iOS image loader with expect/actual pattern
- **Memory Cache** - 25% of available memory for optimal performance
- **Disk Cache** - 100MB in app cache directory for offline access
- **Applied to** - CategorySearchScreen, ProductCard, HomeProductCard

### 🎁 Coupon and Discount Code System
- **Coupon Code Input** - Apply coupons at checkout
- **Multiple Coupon Types** - Percentage, Fixed Amount, Free Shipping
- **Real-time Validation** - Expiration, usage limit, minimum order checks
- **Discount Display** - Strikethrough original price, show savings
- **Order Integration** - Coupon tracked in order with discount details
- **Usage Tracking** - Automatic usage count increment on order
- **Firestore Collection** - `coupons` collection for coupon management

### 👤 Profile Enhancements (Latest)
- **Settings Screen** - Centralized settings with communication preferences
- **Communication Preferences** - Email, push notification, SMS notification toggles
- **Password Change** - Secure password update with current password verification
- **Two-Factor Authentication** - Enable/disable 2FA for enhanced security
- **Account Deletion** - Permanent account deletion with password confirmation
- **Birth Date** - User birth date for personalized discounts
- **Profile Photo** - Profile photo URL management
- **Drawer Navigation** - Settings accessible from navigation drawer

### ⭐ Product Review System
- **Star Ratings** - Users can rate products with 1-5 stars
- **Written Reviews** - Detailed review comments with 500 character limit
- **📷 Photo Upload** - Attach photos to reviews (Firebase Storage)
- **👍👎 Helpful Voting** - Vote on review helpfulness with atomic counters
- **Real-time Updates** - Average rating and count update instantly
- **Separate Review Page** - Dedicated `AddReviewScreen` for writing reviews
- **Photo Gallery** - `ReviewPhotosGallery` component for displaying review images
- **Vote Tracking** - `review_votes` Firestore collection for vote management
- **User Restrictions** - Cannot vote on own reviews, one vote per review

### 🛠️ Admin Order Management & Order History
- **Admin Order Management** - View and manage all customer orders
- **Order Status Updates** - Progress orders through PENDING → CONFIRMED → PREPARING → SHIPPED → DELIVERED
- **Order Cancellation** - Cancel orders (except delivered/cancelled)
- **Order Detail View** - Comprehensive order details with status timeline
- **Customer Order History** - Users can view their order history and track status
- **Real-time Updates** - Live order status synchronization
- **Status Timeline** - Visual order progress tracking
- **Modern UI** - Consistent design with existing admin panel

### 📊 Analytics Dashboard
- **Interactive Revenue Charts** with custom Canvas drawing
- **Real-time Business Metrics** (revenue, orders, average order value)
- **Enhanced Line Charts** with grid lines, axes, and point connections
- **Top-selling Products Analysis** with visual indicators
- **User Statistics Tracking** with growth metrics
- **Date Range Filtering** (Today, Last 7 Days, Last 30 Days)
- **Animated Components** with staggered loading effects
- **Loading States** with shimmer effects
- **Error Handling** with retry mechanisms
- **Responsive Design** for all screen sizes

### 🗺️ Location Management
- **User-friendly address management** with add/edit functionality
- **Categorized locations:** Home 🏠, Work 🏢, Other 📍
- **Integrated into custom drawer** for easy access
- **Firebase Firestore storage** with user-specific security rules

### 💳 Stripe Payment Integration
- **Multi-platform payment processing** 
- **Android:** Real Stripe PaymentSheet for native payment experience
- **iOS:** Realistic simulated payment flow with same UX
- **Dynamic API key management** from shared constants
- **Complete order management** with payment tracking
- **"Pay with Card" 💳 and "Pay on Delivery" 🚚** options

### ❤️ Enhanced User Experience
- **Advanced review system** - Star ratings, photos, and helpful voting
- **Favorites management** with instant updates
- **Responsive design** for all screen sizes
- **Bottom sheet experiences** consistent across platforms

---

## 🚀 Installation & Running

### Requirements

- **JDK 17+**
- **Android Studio Giraffe or newer**
- **Xcode 14+ (for iOS)**

### Clone the Project

```sh
git clone https://github.com/kaaneneskpc/Supplr.git
cd Supplr
```

### Run for Android

```sh
./gradlew :composeApp:installDebug
```

### Run for iOS

1. Open `iosApp/iosApp.xcodeproj` in Xcode.
2. Configure as needed and run.

---

## 🔐 Security & Firebase Rules

### Admin Panel Security
- **Role-based Authentication**: Admin access controlled via email verification
- **Firestore Security Rules**: Comprehensive rules for analytics, dashboard data, and admin logs
- **Data Isolation**: User-specific data access with proper authorization
- **Audit Trail**: Admin actions logged with timestamps and details

### Supported Collections
- `analytics/` - Analytics data (admin-only read/write)
- `admin_dashboard/` - Dashboard configuration (admin-only)
- `user_stats/` - User statistics (admin-only read)
- `sales_analytics/` - Sales data (admin-only)
- `product_analytics/` - Product performance (admin-only)
- `admin_logs/` - Audit trail (admin-only read, create-only)

---

## 🧑‍💻 Contributing

Contributions are welcome! 🎉 Fork, create a new branch, and submit your changes as a PR.

### Contribution Steps

1. Fork 🍴
2. Create a new branch 🚀 (`git checkout -b feature-name`)
3. Commit your changes 🎯 (`git commit -m 'Description'`)
4. Push the branch 📤 (`git push origin feature-name`)
5. Open a Pull Request 🔥

### Areas for Contribution
- **Analytics Enhancements**: New chart types, advanced metrics
- **UI/UX Improvements**: Better animations, responsive design
- **Performance Optimizations**: Chart rendering, data loading
- **Testing**: Unit tests, UI tests for admin features
- **Documentation**: Code documentation, user guides

---

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

---

## 📚 Resources & Inspiration

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin DI](https://insert-koin.io/)
- [Firebase](https://firebase.google.com/)
- [Ktor](https://ktor.io/)
- [Coil](https://coil-kt.github.io/coil/)
- [Canvas Drawing in Compose](https://developer.android.com/jetpack/compose/graphics/draw/overview)

---

> **Note:** For more details and architectural explanations, see the [PRD document](./prd.md).

---

## Contact

**Kaan Enes Kapıcı**
- LinkedIn: [Kaan Enes Kapıcı](https://www.linkedin.com/in/kaaneneskpc/)
- GitHub: [@kaaneneskpc](https://github.com/kaaneneskpc)
- Email: kaaneneskpc1@gmail.com

💡 **Open to feedback and collaboration!** If you're interested in modern mobile architecture, analytics dashboards, or multiplatform development, feel free to connect. 🚀
