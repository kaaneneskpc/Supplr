# Supplr PRD (Product Requirements Document)

## 1. Proje Amacı ve Genel Tanım

Supplr, Android ve iOS platformlarını hedefleyen, Kotlin Multiplatform (KMP) ve Jetpack Compose Multiplatform ile geliştirilen, modern, modüler ve ölçeklenebilir bir e-ticaret uygulamasıdır. Kullanıcılar ürünleri inceleyebilir, sepete ekleyebilir, sipariş verebilir, favorilere ekleyebilir ve profil yönetimi yapabilir. **Gelişmiş admin analytics dashboard** ile iş analitikleri, interaktif grafikler ve gerçek zamanlı veri takibi sağlanır.

---

## 2. Kullanılan Teknolojiler

### Core Technologies
- **Kotlin Multiplatform (KMP):** Ortak kodun hem Android hem iOS için kullanılmasını sağlar.
- **Jetpack Compose Multiplatform:** UI katmanında Compose kullanımı ile modern, deklaratif arayüzler.
- **Koin:** Dependency Injection (DI) için kullanılır.
- **Firebase:** Authentication, Firestore (veritabanı), Storage (dosya yönetimi), Google Sign-In ve **Push Notification** (bildirim).
- **Ktor:** Network işlemleri için multiplatform HTTP client ve **Stripe API entegrasyonu**.

### UI & Analytics Technologies
- **Custom Canvas Charts:** Analytics görselleştirmeleri için özel çizim bileşenleri.
- **Material3:** Modern UI bileşenleri ve responsive design.
- **Animated Components:** Gelişmiş kullanıcı deneyimi için animasyonlar.
- **Coil:** Görsel yükleme ve cache işlemleri için.
- **Loading Shimmer Effects:** Modern loading state'leri için.

### Payment & Integration
- **Stripe SDK:** **Android** ve **iOS** için payment processing (**Android:** Real PaymentSheet, **iOS:** Simulated flow).
- **Coroutines & Flow:** Asenkron işlemler ve reaktif veri akışı.
- **Navigation Compose:** Ekranlar arası geçişler için.
- **MessageBar KMP:** Kullanıcıya mesaj göstermek için.
- **Multiplatform Settings:** Platformlar arası ayar ve local storage yönetimi.

---

## 3. Proje ve Modül Yapısı

```
- composeApp/      : Uygulamanın multiplatform ana giriş noktası ve genel UI.
- iosApp/          : iOS uygulama giriş noktası (SwiftUI ile entegre).
- feature/
    - auth/        : Giriş, kayıt, authentication işlemleri.
    - cart/        : Sepet yönetimi.
    - home/        : Ana ekran ve navigasyon.
    - profile/     : Kullanıcı profil yönetimi.
    - admin_panel/ : 📊 Admin analytics dashboard, interaktif grafikler, iş metrikleri.
    - manage_product/: Admin için ürün ekleme, düzenleme ve silme işlemleri.
    - product_details/: Ürün detayları.
    - products_overview/: Ana ekranda yeni ve indirimli ürünlerin öne çıkarıldığı ürün listeleme.
    - payment_completed/: Sipariş tamamlandı ekranı ve sipariş sonrası işlemler.
    - checkout/       : Ödeme işlemleri ve **Stripe entegrasyonu**.
    - categories/: Kategori yönetimi.
    - favorites/      : Kullanıcının favori ürünlerini yönettiği ekran ve iş mantığı.
    - locations/      : Kullanıcı adres yönetimi, ekleme/düzenleme, kategorizasyon (Home, Work, Other).
    - contact_us/     : İletişim sayfası.
- data/            : Veri katmanı, repository ve servisler.
- shared/          : Ortak domain modelleri, util, constantlar.
- di/              : Dependency injection modülleri (Koin).
- navigation/      : Navigation graph ve ekran yönlendirme.
```

---

## 4. Mimari ve Clean Architecture

### Katmanlar ve Kullanılan Teknolojiler

- **Presentation Layer (feature/):**
  - Her ekran (Screen) ve ViewModel ayrı modüllerde.
  - UI, business logic'ten ayrılmıştır.
  - Compose ile deklaratif UI.
  - **Admin Analytics Dashboard:** Gerçek zamanlı veri görselleştirme, custom Canvas charts.
  - **Kullanılan Teknolojiler:**
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
  - Temel iş modelleri (Product, Customer, CartItem, **Favorite**, **Location**, **PaymentIntent**, **Order**, **DashboardAnalytics**, **DailySummary**, **TopSellingProduct** vs.).
  - Repository arayüzleri (ProductRepository, **FavoritesRepository**, **LocationRepository**, **PaymentRepository**, **AdminRepository** ...)
  - **Analytics Models:** Dashboard metrikleri ve analytics veri yapıları.
  - **Kullanılan Teknolojiler:**
    - Kotlin Multiplatform
    - Kotlinx Serialization
    - Kendi interface'ler (Repository arayüzleri)

- **Data Layer (data/):**
  - Repository implementasyonları (ör. CustomerRepositoryImpl, **FavoritesRepositoryImpl**, **LocationRepositoryImpl**, **PaymentRepositoryImpl**, **AdminRepositoryImpl**).
  - Firebase Firestore'da her kullanıcıya özel favorites ve **locations** koleksiyonu.
  - **Analytics Data Processing:** Firestore'dan analytics verilerinin işlenmesi ve hesaplanması.
  - **Stripe API entegrasyonu** PaymentIntent oluşturma ve order yönetimi için.
  - DTO ve veri dönüşümleri.
  - **Kullanılan Teknolojiler:**
    - Firebase (Firestore, Storage, Auth)
    - Ktor
    - Kotlin Coroutines & Flow
    - Kotlinx Serialization
    - Multiplatform Settings
    - Coil (coil3-network-ktor)

- **DI Layer (di/):**
  - Koin ile bağımlılıkların yönetimi.
  - Tüm ViewModel ve repository'ler burada inject edilir.
  - **Kullanılan Teknolojiler:**
    - Koin (koin-core, koin-compose)
    - Kotlin Multiplatform

### Clean Architecture Uygulaması

- **Bağımlılık Yönü:** Data → Domain → Presentation (UI, ViewModel).
- **Test Edilebilirlik:** Repository arayüzleri sayesinde kolayca mocklanabilir.
- **Modülerlik:** Her feature kendi modülünde, bağımsız geliştirilebilir.
- **Navigation:** Ekranlar arası geçişler merkezi bir NavGraph ile yönetilir.
- **Analytics Architecture:** Ayrı analytics katmanı ile gerçek zamanlı veri işleme.

### Yeni Feature Modülleri
  - **admin_panel:** 📊 **Gelişmiş Analytics Dashboard** - Gerçek zamanlı iş metrikleri, interaktif grafikler, revenue analizi, top-selling products, user statistics.
  - **manage_product:** Adminlerin ürün ekleme, düzenleme ve silme işlemlerini gerçekleştirdiği ekran ve iş mantığı.
  - **products_overview:** Ana ekranda yeni ve indirimli ürünlerin öne çıkarıldığı, kullanıcıya hızlı erişim sağlayan ürün listeleme modülü.
  - **payment_completed:** Sipariş tamamlandıktan sonra kullanıcıya sipariş özeti ve başarı mesajı gösteren ekran.
  - **favorites:** Kullanıcıların ürünleri favorilere ekleyebilir, favori ürünlerini ayrı bir ekranda görebilir ve yönetebilir.

---

## 5. Admin Analytics Dashboard Özellikleri

### 📊 Dashboard Bileşenleri

#### 5.1. Revenue Analytics
- **Enhanced Line Charts:** Custom Canvas çizimi ile gelişmiş grafik görselleştirme
- **Grid Lines ve Axes:** Profesyonel grafik görünümü
- **Data Points:** Siyah noktalar ve beyaz kenarlar ile net görünürlük
- **Fill Area:** Gradient efekti ile alan dolgusu
- **Interactive Elements:** Tıklanabilir noktalar ve hover efektleri

#### 5.2. Metrics Cards
- **Animated Entry:** Staggered loading efektleri
- **Real-time Updates:** Otomatik veri yenileme
- **Key Performance Indicators:**
  - Total Revenue (Toplam Gelir)
  - Total Orders (Toplam Sipariş)
  - Average Order Value (Ortalama Sipariş Değeri)

#### 5.3. Top Selling Products
- **Visual Indicators:** Ürün performans göstergeleri
- **Sales Metrics:** Satış adetleri ve trend analizi
- **Product Cards:** Modern UI ile ürün bilgileri

#### 5.4. User Statistics
- **User Growth Tracking:** Kullanıcı büyüme metrikleri
- **Engagement Metrics:** Kullanıcı etkileşim verileri
- **Demographic Analysis:** Demografik veri analizi

### 📅 Date Range Filtering
- **Flexible Time Periods:**
  - Today (Bugün)
  - Last 7 Days (Son 7 Gün)
  - Last 30 Days (Son 30 Gün)
- **Dynamic Data Loading:** Seçilen tarihe göre otomatik veri yükleme

### 🔄 Real-time Features
- **Auto-refresh:** Otomatik veri yenileme
- **Pull-to-refresh:** Manuel yenileme desteği
- **Loading States:** Shimmer efektleri ile modern loading
- **Error Handling:** Hata durumları için retry mekanizması

---

## 6. Güvenlik ve Firebase Rules

### 6.1. Admin Panel Güvenliği
- **Role-based Authentication:** Email bazlı admin kontrolü (`isAdmin()` fonksiyonu)
- **Firestore Security Rules:** Kapsamlı güvenlik kuralları
- **Data Isolation:** Kullanıcıya özel veri erişim kontrolü
- **Audit Trail:** Admin işlemlerinin loglanması

### 6.2. Firestore Collections ve Güvenlik Kuralları

#### Analytics Collections
- **`analytics/`** - Analytics verileri (sadece admin erişimi)
- **`admin_dashboard/`** - Dashboard konfigürasyonu (sadece admin)
- **`user_stats/`** - Kullanıcı istatistikleri (sadece admin okuma)
- **`sales_analytics/`** - Satış verileri (sadece admin)
- **`product_analytics/`** - Ürün performansı (sadece admin)
- **`admin_logs/`** - Admin işlem logları (sadece admin okuma, sadece oluşturma)

#### Security Rules Özellikleri
- **Veri Validasyonu:** Gerekli alanların kontrolü
- **Timestamp Kontrolü:** İşlem zamanlarının doğrulanması
- **Audit Trail Koruması:** Log kayıtlarının değiştirilemez olması
- **Admin ID Doğrulama:** İşlemi yapan admin'in kimlik kontrolü

### 6.3. Genel Güvenlik
- **Authentication:** Firebase Auth ve Google Sign-In ile kullanıcı doğrulama.
- **Authorization:** Admin işlemleri için kullanıcıya özel rol kontrolü (isAdmin).
- **Veri Güvenliği:** Firestore ve Storage erişimleri sadece authenticated kullanıcıya açık.
- **Güvenli Depolama:** Multiplatform Settings ile hassas veriler local olarak güvenli şekilde saklanır.
- **Network Güvenliği:** Ktor ile HTTPS kullanımı, Firebase ile güvenli veri transferi.
- **Payment Security:** **Stripe API keys** güvenli yönetimi (`shared/Consts.kt`), PCI-DSS compliant payment processing.
- **Location Data Security:** Kullanıcıya özel location verisi, sadece kendi lokasyonlarına erişim hakkı.

---

## 7. DevOps ve Build

- **Gradle ile çoklu modül yönetimi.**
- **Android ve iOS için ayrı build konfigürasyonları.**
- **GoogleService-Info.plist ve google-services.json ile platforma özel Firebase entegrasyonu.**
- **KMP ile tek kod tabanından iki platforma derleme.**
- **Analytics Build Optimization:** Chart rendering optimizasyonları ve performance tuning.

---

## 8. UI/UX ve Modern Design

### 8.1. Material3 ve Responsive Design
- **Material3 ve Compose ile modern, responsive arayüz.**
- **Animasyonlar ve geçiş efektleri.**
- **Karanlık ve aydınlık tema desteği.**
- **Kullanıcıya anlık mesaj ve hata gösterimi (MessageBar).**

### 8.2. Admin Dashboard UX
- **Interactive Charts:** Kullanıcı dostu grafik etkileşimleri
- **Loading Experience:** Shimmer efektleri ve smooth transitions
- **Responsive Layout:** Tüm ekran boyutlarında optimize edilmiş tasarım
- **Error States:** Kullanıcı dostu hata mesajları ve retry options
- **Data Visualization:** Profesyonel iş analitiği görselleştirme

### 8.3. Kullanıcı Deneyimi Özellikleri
- **BottomBar ve TopBar ile kolay navigasyon.**
- **Kullanıcıya özel bildirimler (push notification) desteği.**
- **Favoriler:**
  - Ürün detay ekranında sağ üstte kalp ikonu ile favoriye ekleme.
  - Favoriler ekranında favori ürünlerin listelenmesi, her kartın sağ üstünde favoriden çıkarma butonu.
  - Favori ürünler anlık olarak güncellenir, ekleme/çıkarma işlemlerinde mesaj bar ile kullanıcı bilgilendirilir.
- **Locations:**
  - Kullanıcı adres yönetimi ile kolay adres ekleme/düzenleme.
  - Location kategorileri (Home 🏠, Work 🏢, Other 📍) ile organize edilmiş adres yapısı.
  - Custom drawer'dan kolayca erişilebilir lokasyon yönetimi.
- **Payment Experience:**
  - **Android:** Real Stripe PaymentSheet ile native payment deneyimi.
  - **iOS:** Simulated ama realistic payment flow, Android ile uyumlu UX.
  - "Pay with Card" 💳 ve "Pay on Delivery" 🚚 seçenekleri.

---

## 9. Test Edilebilirlik

- **Repository arayüzleri ve ViewModel'ler kolayca test edilebilir.**
- **Mock ve fake veri ile UI testleri yapılabilir.**
- **Kotlin Multiplatform ile ortak testler yazılabilir.**
- **Analytics Testing:** Chart rendering ve data processing testleri.
- **Admin Panel Testing:** Role-based access ve security rule testleri.

---

## 10. Genişletilebilirlik ve Bakım

### 10.1. Modüler Yapı
- **Yeni bir feature eklemek için yeni bir modül açmak yeterli.**
- **Her feature kendi ViewModel ve repository'sine sahip.**
- **DI ile bağımlılıklar kolayca yönetilir.**
- **Kod okunabilirliği ve sürdürülebilirliği yüksek.**

### 10.2. Son Eklenen Feature'lar
- **📊 Admin Analytics Dashboard (admin_panel):** 
  - Gerçek zamanlı iş metrikleri
  - İnteraktif revenue charts
  - Enhanced line charts with custom Canvas
  - Top-selling products analysis
  - User statistics tracking
  - Date range filtering
  - Animated components
  - Loading states ve error handling

- **Ürün Yönetimi (manage_product):** Ürün ekleme, güncelleme ve silme işlemleri.
- **Ürünler Genel Bakış (products_overview):** Ana ekranda yeni ve indirimli ürünlerin listelenmesi.
- **Ödeme Tamamlandı (payment_completed):** Sipariş sonrası kullanıcı bilgilendirme ve özet ekranı.
- **Favoriler (favorites):** Kullanıcılar ürünleri favorilere ekleyebilir, favori ürünlerini ayrı bir ekranda görebilir ve yönetebilir.
- **🗺️ Locations (locations):** Kullanıcı adres yönetimi sistemi.
- **💳 Stripe Payment Integration:** Multi-platform payment sistemi.

---

## 11. Eklenebilecekler / İyileştirme Önerileri

### 11.1. Analytics ve Admin Panel İyileştirmeleri
- **Advanced Chart Types:** Bar charts, pie charts, area charts
- **Real-time Notifications:** Admin için kritik metrik bildirimleri
- **Export Functionality:** PDF/Excel export for reports
- **Advanced Filtering:** Multi-dimensional data filtering
- **Predictive Analytics:** ML-based trend prediction
- **Performance Metrics:** App performance monitoring

### 11.2. Genel İyileştirmeler
- **Unit ve UI test coverage'ı artırılabilir.**
- **CI/CD pipeline (GitHub Actions, Bitrise vs.) ile otomatik build ve test.**
- **Crashlytics ve Analytics ile hata ve kullanıcı davranışı takibi.**
- **Daha gelişmiş rol ve izin yönetimi.**
- **Offline-first desteği (local cache, sync).**
- **Daha detaylı logging ve monitoring.**
- **Daha fazla platform (web, desktop) için Compose Multiplatform genişletmesi.**

---

## 12. Kaynaklar

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

### 13.1. Analytics Component Geliştirme
```kotlin
// Custom Chart Component Şablonu
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

Bu doküman, admin analytics dashboard özelliklerini ve modern chart geliştirme pratiklerini içererek projeyi geliştirirken ve yeni analitik özellikler eklerken temel referans olarak kullanılabilir.
