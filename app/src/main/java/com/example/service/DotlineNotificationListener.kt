package com.example.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DotlineNotificationListener : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        instance = this
        updateActiveNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        updateActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        updateActiveNotifications()
    }

    private fun updateActiveNotifications() {
        try {
            val active = activeNotifications ?: return
            val packages = active.map { it.packageName }.toSet()
            _activePackages.value = packages
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) instance = null
    }

    companion object {
        var instance: DotlineNotificationListener? = null
            private set

        private val _activePackages = MutableStateFlow<Set<String>>(emptySet())
        val activePackages: StateFlow<Set<String>> = _activePackages.asStateFlow()
    }
}
