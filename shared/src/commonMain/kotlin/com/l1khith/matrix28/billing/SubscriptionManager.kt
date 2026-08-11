package com.l1khith.matrix28.billing

import com.l1khith.matrix28.repository.UserPreferencesRepository
import com.l1khith.matrix28.repository.createDataStore
import com.l1khith.matrix28.utils.currentTimeMillis
import com.l1khith.matrix28.utils.showPlatformToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SubscriptionManager {

    private val _isProActive = MutableStateFlow(false)
    val isProActive: StateFlow<Boolean> = _isProActive.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var prefsRepo: UserPreferencesRepository? = null

    private var isConfigured = false

    private var devTapCount = 0
    private var lastDevTapTime = 0L

    private fun getRepo(): UserPreferencesRepository {
        val current = prefsRepo
        if (current != null) return current
        val newRepo = UserPreferencesRepository(createDataStore())
        prefsRepo = newRepo
        return newRepo
    }

    fun initDataStore(scope: CoroutineScope) {
        try {
            val repo = getRepo()
            scope.launch(Dispatchers.Default) {
                repo.isProUser.collect { isPro ->
                    _isProActive.value = isPro
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onDevModeTap(): Boolean {
        val now = currentTimeMillis()
        if (now - lastDevTapTime > 2000) devTapCount = 0
        lastDevTapTime = now
        devTapCount++
        if (devTapCount >= 5) {
            devTapCount = 0
            _isProActive.value = !_isProActive.value
            return true
        }
        return false
    }

    fun isDevModeActive(): Boolean = _isProActive.value && !isConfigured

    fun toggleProMode(scope: CoroutineScope) {
        val newStatus = !_isProActive.value
        _isProActive.value = newStatus

        scope.launch(Dispatchers.Default) {
            try {
                val repo = getRepo()
                repo.updateIsProUser(newStatus)

                withContext(Dispatchers.Main.immediate) {
                    val message = if (newStatus) {
                        "Pro Mode Activated (Testing Phase)"
                    } else {
                        "Pro Mode Deactivated (Testing Phase)"
                    }
                    showPlatformToast(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setProActive(isPro: Boolean) {
        _isProActive.value = isPro
    }

    fun configure(apiKey: String, entitlementId: String = "pro") {
        if (apiKey.isBlank()) {
            _isProActive.value = false
            return
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
