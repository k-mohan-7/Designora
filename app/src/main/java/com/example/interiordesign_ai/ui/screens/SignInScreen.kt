package com.example.interiordesign_ai.ui.screens

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interiordesign_ai.R
import com.example.interiordesign_ai.viewmodel.AuthState
import com.example.interiordesign_ai.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

val AppBg = Color(0xFFEEEAFF)
val AppPurple = Color(0xFF3D1DDB)

@Composable
fun SignInScreen(
    onSignIn: () -> Unit,
    onCreateAccount: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailErr by remember { mutableStateOf("") }
    var passErr  by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                authViewModel.resetState()
                onSignIn()
            }
            is AuthState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                }
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppBg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App logo
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Designora Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(18.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign in to your account",
                    fontSize = 14.sp,
                    color = Color(0xFF888888)
                )

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x22000000))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 28.dp)
                ) {
                    Column {
                        AuthFieldLabel(text = "Email Address")
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

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                var valid = true
                                if (email.isBlank()) {
                                    emailErr = "Email is required"; valid = false
                                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                    emailErr = "Enter a valid email"; valid = false
                                }
                                if (password.isBlank()) {
                                    passErr = "Password is required"; valid = false
                                }
                                if (valid) authViewModel.login(email.trim(), password)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp), spotColor = AppPurple.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppPurple),
                            enabled = authState !is AuthState.Loading
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Sign In",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Don't have an account? ",
                                fontSize = 14.sp,
                                color = Color(0xFF888888)
                            )
                            Text(
                                text = "Create Account",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppPurple,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onCreateAccount
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF333333)
    )
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFFBBBBBB),
                fontSize = 15.sp
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = AppPurple,
            unfocusedIndicatorColor = Color(0xFFE0E0E0),
            cursorColor = AppPurple,
            focusedTextColor = Color(0xFF1A1A2E),
            unfocusedTextColor = Color(0xFF1A1A2E)
        )
    )
}
