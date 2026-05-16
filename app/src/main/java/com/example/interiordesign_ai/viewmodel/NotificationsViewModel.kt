package com.example.interiordesign_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interiordesign_ai.model.AppNotification
import com.example.interiordesign_ai.model.MarkReadRequest
import com.example.interiordesign_ai.network.RetrofitClient
import com.example.interiordesign_ai.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionManager(app)
    private val api = RetrofitClient.api

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    fun loadNotifications() {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val response = api.getNotifications(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data
                    _notifications.value = data?.notifications ?: emptyList()
                    _unreadCount.value   = data?.unreadCount ?: 0
                }
            } catch (_: Exception) {}
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                api.markRead(MarkReadRequest(userId, 0))
                _notifications.value = _notifications.value.map { it.copy(isRead = 1) }
                _unreadCount.value = 0
            } catch (_: Exception) {}
        }
    }

    fun markOneRead(notifId: Int) {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                api.markRead(MarkReadRequest(userId, notifId))
                _notifications.value = _notifications.value.map {
                    if (it.id == notifId) it.copy(isRead = 1) else it
                }
                _unreadCount.value = _notifications.value.count { it.isRead == 0 }
            } catch (_: Exception) {}
        }
    }
}
