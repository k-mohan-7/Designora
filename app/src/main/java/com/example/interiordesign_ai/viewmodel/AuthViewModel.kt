package com.example.interiordesign_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interiordesign_ai.model.LoginRequest
import com.example.interiordesign_ai.model.RegisterRequest
import com.example.interiordesign_ai.network.RetrofitClient
import com.example.interiordesign_ai.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val message: String = "") : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionManager(app)
    private val api = RetrofitClient.api

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun resetState() { _authState.value = AuthState.Idle }

    fun register(name: String, phone: String, email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = api.register(RegisterRequest(name, phone, email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data!!
                    session.saveSession(user.id, user.name, user.email, user.phone)
                    _authState.value = AuthState.Success("Account created successfully!")
                } else {
                    val msg = response.body()?.message ?: "Registration failed. Please try again."
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message ?: "Check your connection"}")
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()!!.data!!
                    session.saveSession(user.id, user.name, user.email, user.phone)
                    _authState.value = AuthState.Success("Welcome back, ${user.name}!")
                } else {
                    val msg = response.body()?.message ?: "Login failed. Check your credentials."
                    _authState.value = AuthState.Error(msg)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.message ?: "Check your connection"}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            session.clearSession()
        }
    }
}
