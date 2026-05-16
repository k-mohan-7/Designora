package com.example.interiordesign_ai.ui.screens

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interiordesign_ai.viewmodel.AuthState
import com.example.interiordesign_ai.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onCreateAccount: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameErr by remember { mutableStateOf("") }
    var phoneErr by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf("") }
    var passErr by remember { mutableStateOf("") }
    var confirmErr by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                authViewModel.resetState()
                onCreateAccount()
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    val isLoading = authState is AuthState.Loading

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp)
                .padding(top = 52.dp, bottom = 32.dp)
        ) {
            // Back arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF333333),
                modifier = Modifier
                    .size(26.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack
                    )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create Account",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Join InterioDecor AI today",
                fontSize = 14.sp,
                color = Color(0xFF888888)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // White card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x22000000))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    AuthFieldLabel(text = "Full Name")
                    Spacer(modifier = Modifier.height(6.dp))
                    AuthTextField(
                        value = fullName,
                        onValueChange = { fullName = it; nameErr = "" },
                        placeholder = "John Doe"
                    )
                    if (nameErr.isNotEmpty()) {
                        Text(nameErr, color = Color(0xFFE53935), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthFieldLabel(text = "Phone Number")
                    Spacer(modifier = Modifier.height(6.dp))
                    AuthTextField(
                        value = phone,
                        onValueChange = { phone = it; phoneErr = "" },
                        placeholder = "+91 98765 43210",
                        keyboardType = KeyboardType.Phone
                    )
                    if (phoneErr.isNotEmpty()) {
                        Text(phoneErr, color = Color(0xFFE53935), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthFieldLabel(text = "Email")
                    Spacer(modifier = Modifier.height(6.dp))
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it; emailErr = "" },
                        placeholder = "name@example.com",
                        keyboardType = KeyboardType.Email
                    )
                    if (emailErr.isNotEmpty()) {
                        Text(emailErr, color = Color(0xFFE53935), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthFieldLabel(text = "Password")
                    Spacer(modifier = Modifier.height(6.dp))
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it; passErr = "" },
                        placeholder = "••••••••",
                        isPassword = true
                    )
                    if (passErr.isNotEmpty()) {
                        Text(passErr, color = Color(0xFFE53935), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthFieldLabel(text = "Confirm Password")
                    Spacer(modifier = Modifier.height(6.dp))
                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmErr = "" },
                        placeholder = "••••••••",
                        isPassword = true
                    )
                    if (confirmErr.isNotEmpty()) {
                        Text(confirmErr, color = Color(0xFFE53935), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            var valid = true
                            if (fullName.isBlank()) { nameErr = "Name is required"; valid = false }
                            if (phone.isBlank()) { phoneErr = "Phone is required"; valid = false }
                            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                                emailErr = "Enter a valid email"; valid = false
                            }
                            if (password.length < 8) { passErr = "Password must be at least 8 characters"; valid = false }
                            if (confirmPassword != password) { confirmErr = "Passwords do not match"; valid = false }
                            if (valid) {
                                authViewModel.register(
                                    fullName.trim(),
                                    phone.trim(),
                                    email.trim(),
                                    password
                                )
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp), spotColor = AppPurple.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppPurple)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Create Account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
