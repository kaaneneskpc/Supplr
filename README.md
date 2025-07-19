# 🛒 Supplr - Kotlin Multiplatform E-Commerce App

![Android](https://img.shields.io/badge/Android-✅-green)
![iOS](https://img.shields.io/badge/iOS-✅-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-orange)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-%F0%9F%9A%80-orange)
![Koin](https://img.shields.io/badge/Koin-DI-yellow)
![Open Source](https://img.shields.io/badge/Open%20Source-%E2%9C%94%EF%B8%8F-lightgrey)

---

## 🚀 About the Project

**Supplr** is a modern, modular, and scalable e-commerce application targeting both Android and iOS platforms. Built with Kotlin Multiplatform (KMP) and Jetpack Compose Multiplatform, it allows users to browse products, add to cart, place orders, and manage their profiles. An admin panel is available for product management.

---

## 📦 Features

- 🔥 **Kotlin Multiplatform**: Single codebase for Android & iOS
- 🎨 **Modern UI**: Responsive and animated interface with Jetpack Compose Multiplatform
- 🛒 **Cart & Order Management**
- 👤 **User Profile & Authentication**
- 🛠️ **Admin Panel**: Add, edit, delete, and search products
- 🏷️ **Category & Product Listing**
- 💳 **Checkout & Order Completion**
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
    - admin_panel/       : Admin product management and search.
    - manage_product/    : Admin add, edit, delete products.
    - product_details/   : Product details.
    - products_overview/ : Home screen new & discounted product listing.
    - payment_completed/ : Order completed screen and post-order.
    - categories/        : Category management.
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

---

## 🛠️ Tech Stack

- **Kotlin Multiplatform (KMP)**
- **Jetpack Compose Multiplatform**
- **Koin** (Dependency Injection)
- **Firebase** (Auth, Firestore, Storage)
- **Ktor** (HTTP Client)
- **Coil** (Image Loading)
- **Coroutines & Flow**
- **Material3**
- **Navigation Compose**
- **MessageBar KMP**
- **Multiplatform Settings**
- **Gradle**

---

## 📱 Screenshots

<!-- Add screenshots of key app screens here. Example: -->
<!--
### Home
![Home](docs/screenshots/home.png)
### Cart
![Cart](docs/screenshots/cart.png)
### Admin Panel
![Admin Panel](docs/screenshots/admin_panel.png)
-->

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

## 🧑‍💻 Contributing

Contributions are welcome! 🎉 Fork, create a new branch, and submit your changes as a PR.

### Contribution Steps

1. Fork 🍴
2. Create a new branch 🚀 (`git checkout -b feature-name`)
3. Commit your changes 🎯 (`git commit -m 'Description'`)
4. Push the branch 📤 (`git push origin feature-name`)
5. Open a Pull Request 🔥

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

---

> **Note:** For more details and architectural explanations, see the [PRD document](./prd.md).

---

## Contact

**Kaan Enes Kapıcı**
- LinkedIn: [Kaan Enes Kapıcı](https://www.linkedin.com/in/kaaneneskpc/)
- GitHub: [@kaaneneskpc](https://github.com/kaaneneskpc)
- Email: kaaneneskpc1@gmail.com


💡 **Open to feedback and collaboration!** If you're interested in modern mobile architecture, feel free to connect. 🚀
