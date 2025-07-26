import Foundation
import Stripe
import StripePaymentSheet

@objc public class StripePaymentBridge: NSObject {
    
    @objc public static let shared = StripePaymentBridge()
    
    private var paymentSheet: PaymentSheet?
    private var completion: ((Bool, String?) -> Void)?
    
    private override init() {
        super.init()
    }
    
    @objc public func configureStripe(publishableKey: String) {
        StripeAPI.defaultPublishableKey = publishableKey
        NSLog("Stripe configured with key: \(String(publishableKey.prefix(20)))...")
    }
    
    @objc public func presentPaymentSheet(
        clientSecret: String,
        completion: @escaping (Bool, String?) -> Void
    ) {
        self.completion = completion
        
        guard let viewController = UIApplication.shared.windows.first?.rootViewController else {
            completion(false, "No root view controller found")
            return
        }
        
        var configuration = PaymentSheet.Configuration()
        configuration.merchantDisplayName = "Supplr"
        configuration.allowsDelayedPaymentMethods = true
        
        let paymentSheet = PaymentSheet(paymentIntentClientSecret: clientSecret, configuration: configuration)
        
        paymentSheet.present(from: viewController) { [weak self] paymentResult in
            switch paymentResult {
            case .completed:
                self?.completion?(true, nil)
            case .canceled:
                self?.completion?(false, "Payment was canceled")
            case .failed(let error):
                self?.completion?(false, error.localizedDescription)
            }
        }
    }
}

// Extension to expose to Kotlin
extension StripePaymentBridge {
    @objc public func processPayment(
        clientSecret: String,
        callback: @escaping (Bool, String?) -> Void
    ) {
        presentPaymentSheet(clientSecret: clientSecret) { success, error in
            DispatchQueue.main.async {
                callback(success, error)
            }
        }
    }
} 