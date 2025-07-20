# Supplr PRD (Product Requirements Document)

## 1. Proje Amacı ve Genel Tanım

Supplr, Android ve iOS platformlarını hedefleyen, Kotlin Multiplatform (KMP) ve Jetpack Compose Multiplatform ile geliştirilen, modern, modüler ve ölçeklenebilir bir e-ticaret uygulamasıdır. Kullanıcılar ürünleri inceleyebilir, sepete ekleyebilir, sipariş verebilir ve profil yönetimi yapabilir. Admin paneli ile ürün yönetimi mümkündür.

---

## 2. Kullanılan Teknolojiler

- **Kotlin Multiplatform (KMP):** Ortak kodun hem Android hem iOS için kullanılmasını sağlar.
- **Jetpack Compose Multiplatform:** UI katmanında Compose kullanımı ile modern, deklaratif arayüzler.
- **Koin:** Dependency Injection (DI) için kullanılır.
- **Firebase:** Authentication, Firestore (veritabanı), Storage (dosya yönetimi), Google Sign-In ve **Push Notification** (bildirim).
- **Ktor:** Network işlemleri için multiplatform HTTP client.
- **Coil:** Görsel yükleme ve cache işlemleri için.
- **Coroutines & Flow:** Asenkron işlemler ve reaktif veri akışı.
- **Material3:** Modern UI bileşenleri.
- **Navigation Compose:** Ekranlar arası geçişler için.
- **MessageBar KMP:** Kullanıcıya mesaj göstermek için.
- **Multiplatform Settings:** Platformlar arası ayar ve local storage yönetimi.
- **Gradle:** Çok modüllü yapı ve multiplatform derleme için.

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
    - admin_panel/ : Admin için ürün yönetimi ve ürün arama/listeme (yeni).
    - manage_product/: Admin için ürün ekleme, düzenleme ve silme işlemleri (yeni).
    - product_details/: Ürün detayları.
    - products_overview/: Ana ekranda yeni ve indirimli ürünlerin öne çıkarıldığı ürün listeleme (yeni).
    - payment_completed/: Sipariş tamamlandı ekranı ve sipariş sonrası işlemler (yeni).
    - categories/: Kategori yönetimi.
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
  - UI, business logic’ten ayrılmıştır.
  - Compose ile deklaratif UI.
  - **Kullanılan Teknolojiler:**
    - Jetpack Compose Multiplatform
    - Material3
    - Navigation Compose
    - Koin (koin-compose, koin-compose-viewmodel)
    - MessageBar KMP
    - Coil (coil3, coil3-compose)
    - Kotlin Coroutines & Flow
    - AndroidX Lifecycle ViewModel

- **Domain Layer (shared/domain/):**
  - Temel iş modelleri (Product, Customer, CartItem vs.).
  - Repository arayüzleri (interface) burada tanımlı.
  - **Kullanılan Teknolojiler:**
    - Kotlin Multiplatform
    - Kotlinx Serialization
    - Kendi interface’ler (Repository arayüzleri)

- **Data Layer (data/):**
  - Repository implementasyonları (ör. CustomerRepositoryImpl).
  - Firebase, Ktor, Storage gibi dış servislerle iletişim.
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
  - Tüm ViewModel ve repository’ler burada inject edilir.
  - **Kullanılan Teknolojiler:**
    - Koin (koin-core, koin-compose)
    - Kotlin Multiplatform

### Clean Architecture Uygulaması

- **Bağımlılık Yönü:** Data → Domain → Presentation (UI, ViewModel).
- **Test Edilebilirlik:** Repository arayüzleri sayesinde kolayca mocklanabilir.
- **Modülerlik:** Her feature kendi modülünde, bağımsız geliştirilebilir.
- **Navigation:** Ekranlar arası geçişler merkezi bir NavGraph ile yönetilir.

### Yeni Feature Modülleri
  - **admin_panel:** Admin kullanıcılar için ürünleri listeleme, arama ve yönetim paneli sunar.
  - **manage_product:** Adminlerin ürün ekleme, düzenleme ve silme işlemlerini gerçekleştirdiği ekran ve iş mantığı.
  - **products_overview:** Ana ekranda yeni ve indirimli ürünlerin öne çıkarıldığı, kullanıcıya hızlı erişim sağlayan ürün listeleme modülü.
  - **payment_completed:** Sipariş tamamlandıktan sonra kullanıcıya sipariş özeti ve başarı mesajı gösteren ekran.

---

## 5. Güvenlik

- **Authentication:** Firebase Auth ve Google Sign-In ile kullanıcı doğrulama.
- **Authorization:** Admin işlemleri için kullanıcıya özel rol kontrolü (isAdmin).
- **Veri Güvenliği:** Firestore ve Storage erişimleri sadece authenticated kullanıcıya açık.
- **Güvenli Depolama:** Multiplatform Settings ile hassas veriler local olarak güvenli şekilde saklanır.
- **Network Güvenliği:** Ktor ile HTTPS kullanımı, Firebase ile güvenli veri transferi.
- **Rol Bazlı Yetkilendirme:** Admin ve normal kullanıcı ayrımı, admin paneline erişim kontrolü.
- **Oturum Yönetimi:** Kullanıcı oturumu ve token yönetimi, signOut ile güvenli çıkış.

---

## 6. DevOps ve Build

- **Gradle ile çoklu modül yönetimi.**
- **Android ve iOS için ayrı build konfigürasyonları.**
- **GoogleService-Info.plist ve google-services.json ile platforma özel Firebase entegrasyonu.**
- **KMP ile tek kod tabanından iki platforma derleme.**

---

## 7. UI/UX

- **Material3 ve Compose ile modern, responsive arayüz.**
- **Animasyonlar ve geçiş efektleri.**
- **Karanlık ve aydınlık tema desteği.**
- **Kullanıcıya anlık mesaj ve hata gösterimi (MessageBar).**
- **BottomBar ve TopBar ile kolay navigasyon.**
- **Kullanıcıya özel bildirimler (push notification) desteği.**
- **Contact Us ekranında iletişim kartı, Box ve Alignment.Center ile sayfanın tam ortasına hizalanmıştır. Böylece tüm cihazlarda ve ekran boyutlarında kart ortalanır ve kullanıcı deneyimi iyileşir.**

---

## 8. Test Edilebilirlik

- **Repository arayüzleri ve ViewModel’ler kolayca test edilebilir.**
- **Mock ve fake veri ile UI testleri yapılabilir.**
- **Kotlin Multiplatform ile ortak testler yazılabilir.**

---

## 9. Genişletilebilirlik ve Bakım

- **Yeni bir feature eklemek için yeni bir modül açmak yeterli.**
- **Her feature kendi ViewModel ve repository’sine sahip.**
- **DI ile bağımlılıklar kolayca yönetilir.**
- **Kod okunabilirliği ve sürdürülebilirliği yüksek.**

- **Son Eklenen Feature'lar:**
  - **Admin Panel (admin_panel):** Ürünleri arama, filtreleme ve yönetme.
  - **Ürün Yönetimi (manage_product):** Ürün ekleme, güncelleme ve silme işlemleri.
  - **Ürünler Genel Bakış (products_overview):** Ana ekranda yeni ve indirimli ürünlerin listelenmesi.
  - **Ödeme Tamamlandı (payment_completed):** Sipariş sonrası kullanıcı bilgilendirme ve özet ekranı.
  - **Contact Us ekranında iletişim kartı ortalandı (Box + Alignment.Center ile).**

---

## 10. Eklenebilecekler / İyileştirme Önerileri

- **Unit ve UI test coverage’ı artırılabilir.**
- **CI/CD pipeline (GitHub Actions, Bitrise vs.) ile otomatik build ve test.**
- **Crashlytics ve Analytics ile hata ve kullanıcı davranışı takibi.**
- **Daha gelişmiş rol ve izin yönetimi.**
- **Offline-first desteği (local cache, sync).**
- **Daha detaylı logging ve monitoring.**
- **Kullanıcıya özel bildirimler (push notification).**
- **Daha fazla platform (web, desktop) için Compose Multiplatform genişletmesi.**
- **Push notification desteği eklendi.**

---

## 11. Kaynaklar

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Jetpack Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin DI](https://insert-koin.io/)
- [Firebase](https://firebase.google.com/)
- [Ktor](https://ktor.io/)
- [Coil](https://coil-kt.github.io/coil/)

---

Bu doküman, projeyi geliştirirken ve yeni modüller/özellikler eklerken temel referans olarak kullanılabilir. Daha fazla detay veya özel bir başlık istersen ekleyebilirim!

---

## 12. Yeni Feature Ekleme Rehberi

### 12.1. Adım Adım Feature Ekleme
1. **Yeni modül/folder oluştur:**
   - `feature/your_feature_name/` altında `commonMain/kotlin/com/kaaneneskpc/supplr/your_feature_name/` dizinini oluştur.
2. **Katmanları oluştur:**
   - `YourFeatureScreen.kt` (Presentation)
   - `YourFeatureViewModel.kt` (Presentation)
   - Gerekirse `domain/` ve `data/` alt klasörleri
3. **Repository interface ve implementasyonu ekle:**
   - `data/domain/YourFeatureRepository.kt` (interface)
   - `data/YourFeatureRepositoryImpl.kt` (implementasyon)
4. **DI (Koin) modülüne ekle:**
   - `di/src/commonMain/kotlin/com/nutrisport/di/KoinModule.kt` dosyasına yeni ViewModel ve repository’yi ekle.
5. **Navigation’a yeni ekranı ekle:**
   - `navigation/src/commonMain/kotlin/com/kaaneneskpc/supplr/navigation/NavGraph.kt` dosyasına yeni ekranı ekle.
6. **Gerekirse shared/domain’e yeni model/interface ekle.**
7. **Test ve dökümantasyon ekle.**

### 12.2. Kod Standartları ve Naming Convention
- Dosya ve class isimleri PascalCase, fonksiyon ve değişkenler camelCase.
- ViewModel’ler `YourFeatureViewModel`, ekranlar `YourFeatureScreen` olarak adlandırılır.
- Katmanlar arası bağımlılık: ViewModel → repository interface, UI’da business logic olmamalı.
- Stateless Compose component’ler tercih edilmeli.

### 12.3. Test ve Dökümantasyon Standartları
- Her yeni feature için en az birim testi (ViewModel, repository)
- Mock/fake veri ile UI testi
- Public fonksiyonlara ve önemli class’lara KDoc açıklaması
- Her feature için kısa bir README veya açıklama

### 12.4. Feature Checklist
- [ ] Modül/folder açıldı mı?
- [ ] Domain, Data, Presentation ayrımı yapıldı mı?
- [ ] DI’ya eklendi mi?
- [ ] Navigation’a eklendi mi?
- [ ] Test yazıldı mı?
- [ ] Dökümantasyon eklendi mi?
- [ ] Kod review yapıldı mı?

### 12.5. Kod Şablonları ve Örnekler

#### ViewModel Şablonu
```kotlin
class YourFeatureViewModel(
    private val yourFeatureRepository: YourFeatureRepository
) : ViewModel() {
    // State ve iş mantığı burada
}
```

#### Repository Interface ve Implementasyonu
```kotlin
// data/domain/YourFeatureRepository.kt
interface YourFeatureRepository {
    suspend fun doSomething(): ResultType
}

// data/YourFeatureRepositoryImpl.kt
class YourFeatureRepositoryImpl : YourFeatureRepository {
    override suspend fun doSomething(): ResultType {
        // Firebase, Ktor veya local işlemler
    }
}
```

#### Koin Module’a Ekleme
```kotlin
val sharedModule = module {
    single<YourFeatureRepository> { YourFeatureRepositoryImpl() }
    viewModelOf(::YourFeatureViewModel)
    // ... diğer bağımlılıklar
}
```

#### Navigation’a Ekleme
```kotlin
composable<Screen.YourFeature> {
    YourFeatureScreen()
}
```

#### Basit Compose Screen Şablonu
```kotlin
@Composable
fun YourFeatureScreen(viewModel: YourFeatureViewModel = koinViewModel()) {
    // UI ve state gözlemi
}
```

### 12.6. Sık Yapılan Hatalar ve Dikkat Edilmesi Gerekenler
- Katmanlar arası bağımlılık ihlali (UI’dan doğrudan data erişimi)
- UI’da business logic barındırmak
- DI’a eklemeyi unutmak
- Navigation’da route çakışmaları

### 12.7. Kod Review ve PR Standartları
- Açıklamalı ve küçük PR’lar açılmalı
- Kod review checklist kullanılmalı
- Test ve dökümantasyon kontrol edilmeli

### 12.8. Feature Flags ve Sürümleme
- Geliştirme aşamasındaki feature’lar için flag kullanımı
- Sürüm notu ekleme

### 12.9. Design/UX Dökümantasyonu
- Her yeni feature için Figma/UX dokümanı linki veya ekran akışı eklenmeli

---

Bu rehber ve şablonlar, yeni bir feature eklerken mimari tutarlılığı ve kod kalitesini korumanı sağlar. Her adımda bu başlıkları ve örnekleri takip edebilirsin.
