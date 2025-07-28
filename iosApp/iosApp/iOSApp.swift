import SwiftUI
import GoogleSignIn
import Firebase
import Stripe
import StripePaymentSheet

class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ app: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        
        // Note: Stripe will be configured dynamically from Kotlin when needed
        
        return true
    }

    func application(
        _application: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        var handled: Bool
        handled = GIDSignIn.sharedInstance.handle(url)
        if handled {
            return true
        }
        return false
    }



}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea().onOpenURL(perform: { url in
                GIDSignIn.sharedInstance.handle(url)
            })
        }
    }
}
