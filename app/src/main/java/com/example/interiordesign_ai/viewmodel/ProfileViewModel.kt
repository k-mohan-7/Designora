package com.example.interiordesign_ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.interiordesign_ai.model.UpdateProfileRequest
import com.example.interiordesign_ai.model.User
import com.example.interiordesign_ai.network.RetrofitClient
import com.example.interiordesign_ai.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle    : ProfileState()
    object Loading : ProfileState()
    data class Success(val message: String = "") : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionManager(app)
    private val api = RetrofitClient.api

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _profileImagePath = MutableStateFlow("")
    val profileImagePath: StateFlow<String> = _profileImagePath.asStateFlow()

    init {
        viewModelScope.launch {
            session.profileImagePathFlow().collect { _profileImagePath.value = it }
        }
    }

    fun resetState() { _profileState.value = ProfileState.Idle }

    fun saveProfileImage(path: String) {
        viewModelScope.launch {
            session.saveProfileImagePath(path)
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            val userId = session.getUserId()
            if (userId == 0) return@launch
            try {
                val response = api.getProfile(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _user.value = response.body()!!.data ?: User()
                }
            } catch (_: Exception) {}
        }
    }

    fun updateProfile(
        name: String,
        phone: String,
        email: String,
        address: String,
        location: String,
        gender: String,
        dob: String
    ) {
        _profileState.value = ProfileState.Loading
        viewModelScope.launch {
            try {
                val userId = session.getUserId()
                if (userId == 0) {
                    _profileState.value = ProfileState.Error("Session expired")
                    return@launch
                }
                val response = api.updateProfile(
                    UpdateProfileRequest(userId, name, phone, email, address, location, gender, dob)
                )
                if (response.isSuccessful && response.body()?.success == true) {
                    val updated = response.body()!!.data ?: User()
                    _user.value = updated
                    session.saveSession(userId, updated.name, updated.email, updated.phone)
                    _profileState.value = ProfileState.Success("Profile updated successfully!")
                } else {
                    _profileState.value = ProfileState.Error(response.body()?.message ?: "Update failed")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Network error: ${e.message}")
            }
        }
    }
}
