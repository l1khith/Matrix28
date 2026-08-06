package com.l1khith.matrix28.billing

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offerings
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SubscriptionManager {

    private val _customerInfo = MutableStateFlow<CustomerInfo?>(null)
    val customerInfo: StateFlow<CustomerInfo?> = _customerInfo.asStateFlow()

    private val _isProActive = MutableStateFlow(true)
    val isProActive: StateFlow<Boolean> = _isProActive.asStateFlow()


    private val _offerings = MutableStateFlow<Offerings?>(null)
    val offerings: StateFlow<Offerings?> = _offerings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var isConfigured = false

    fun configure(apiKey: String, entitlementId: String = "pro") {
        if (apiKey.isBlank()) {
            println("RevenueCat: API Key is blank. Skipping configuration.")
            return
        }

        try {
            Purchases.configure(
                PurchasesConfiguration.Builder(apiKey = apiKey).build()
            )
            isConfigured = true

            fetchCustomerInfo(entitlementId)
            fetchOfferings()
        } catch (e: Exception) {
            _errorMessage.value = "RevenueCat config error: ${e.message}"
            e.printStackTrace()
        }
    }


    fun fetchCustomerInfo(entitlementId: String = "pro") {
        if (!isConfigured) return
        _isLoading.value = true

        Purchases.sharedInstance.getCustomerInfo(
            onError = { error ->
                _isLoading.value = false
                _errorMessage.value = "Failed to fetch customer info: ${error.message}"
            },
            onSuccess = { info ->
                _isLoading.value = false
                updateCustomerState(info, entitlementId)
            }
        )
    }

    fun fetchOfferings() {
        if (!isConfigured) return

        Purchases.sharedInstance.getOfferings(
            onError = { error ->
                _errorMessage.value = "Failed to fetch offerings: ${error.message}"
            },
            onSuccess = { offeringsObj ->
                _offerings.value = offeringsObj
            }
        )
    }

    fun purchasePackage(
        packageToPurchase: Package,
        entitlementId: String = "pro",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (!isConfigured) {
            onError("RevenueCat is not configured.")
            return
        }
        _isLoading.value = true

        Purchases.sharedInstance.purchase(
            packageToPurchase = packageToPurchase,
            onError = { error, userCancelled ->
                _isLoading.value = false
                if (!userCancelled) {
                    val msg = "Purchase failed: ${error.message}"
                    _errorMessage.value = msg
                    onError(msg)
                }
            },
            onSuccess = { storeTransaction, customerInfo ->
                _isLoading.value = false
                updateCustomerState(customerInfo, entitlementId)
                onSuccess()
            }
        )
    }

    fun restorePurchases(
        entitlementId: String = "pro",
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (!isConfigured) {
            onError("RevenueCat is not configured.")
            return
        }
        _isLoading.value = true

        Purchases.sharedInstance.restorePurchases(
            onError = { error ->
                _isLoading.value = false
                val msg = "Restore failed: ${error.message}"
                _errorMessage.value = msg
                onError(msg)
            },
            onSuccess = { customerInfo ->
                _isLoading.value = false
                updateCustomerState(customerInfo, entitlementId)
                onSuccess()
            }
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun updateCustomerState(info: CustomerInfo, entitlementId: String) {
        _customerInfo.value = info
        _isProActive.value = true
    }

}
