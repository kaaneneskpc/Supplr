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

### 🏗️ Technical Features
- 🔐 **Firebase Authentication & Firestore**
- 🏗️ **Dependency Injection with Koin**
- 🌐 **Networking with Ktor**
- 🖼️ **Image Loading with Coil**
- 🧪 **Testable & Modular Architecture**

---

## 🏗️ Project & Module Structure

```
- composeApp/      : Main multiplatform entry point and general UI.
- iosApp/          : iOS app entry (integrated with SwiftUI).
- feature/
    - auth/              : Authentication (login, register).
    - cart/              : Cart management.
    - home/              : Home screen and navigation.
    - profile/           : User profile management.
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
- data/            : Data layer, repositories, and services.
- shared/          : Shared domain models, utils, constants.
- di/              : Dependency injection modules (Koin).
- navigation/      : Navigation graph and routing.
```

---

## 🧩 Architecture

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

<!-- Add screenshots of key app screens here. Example: -->
<!--
### Analytics Dashboard
![Analytics Dashboard](docs/screenshots/analytics_dashboard.png)
### Revenue Charts
![Revenue Charts](docs/screenshots/revenue_charts.png)
### Home
![Home](docs/screenshots/home.png)
### Cart
![Cart](docs/screenshots/cart.png)
### Admin Panel
![Admin Panel](docs/screenshots/admin_panel.png)
-->

---

## 🎉 Recent Updates

### 📊 Analytics Dashboard (Latest)
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
- **Modern review system** with improved UI/UX
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
