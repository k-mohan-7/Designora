package com.example.interiordesign_ai.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.interiordesign_ai.viewmodel.ProfileState
import com.example.interiordesign_ai.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val user         by profileViewModel.user.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()
    val profileImagePath by profileViewModel.profileImagePath.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName    by remember { mutableStateOf("") }
    var phone       by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var address     by remember { mutableStateOf("") }
    var location    by remember { mutableStateOf("") }
    var gender      by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Select", "Female", "Male", "Other", "Prefer not to say")

    // Image picker
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Copy to internal storage for persistence
            val destFile = File(context.filesDir, "profile_photo.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            profileViewModel.saveProfileImage(destFile.absolutePath)
        }
    }

    // Pre-fill from loaded profile
    LaunchedEffect(user) {
        if (user.name.isNotEmpty()) {
            fullName = user.name
            phone    = user.phone
            email    = user.email
            address  = user.address
            location = user.location
            gender   = user.gender
        }
    }

    LaunchedEffect(Unit) { profileViewModel.loadProfile() }

    // Handle save state
    LaunchedEffect(profileState) {
        when (profileState) {
            is ProfileState.Success -> {
                snackbarHostState.showSnackbar("Profile updated successfully!")
                profileViewModel.resetState()
                onBack()
            }
            is ProfileState.Error -> {
                snackbarHostState.showSnackbar((profileState as ProfileState.Error).message)
                profileViewModel.resetState()
            }
            else -> Unit
        }
    }

    // Date picker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var dobFallback by remember { mutableStateOf("") }     // pre-filled from server
    val selectedDateStr = datePickerState.selectedDateMillis?.let { millis ->
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.format(Date(millis))
    } ?: dobFallback

    // Also pre-fill dob from user
    LaunchedEffect(user) {
        if (user.dob.isNotEmpty()) dobFallback = user.dob
    }

    val activityHistory = listOf(
        Triple("Design Saved",       "17 Feb 2026", "10:25 am"),
        Triple("Design Downloaded",  "17 Feb 2026", "10:22 am"),
        Triple("Password Changed",   "17 Feb 2026", "09:08 am"),
        Triple("Password Changed",   "17 Feb 2026", "09:07 am")
    )

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK", color = AppPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color(0xFF888888))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = AppPurple,
                    todayDateBorderColor      = AppPurple,
                    selectedYearContainerColor = AppPurple
                )
            )
        }
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost   = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // ── Top bar ──────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1A1A2E),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Profile photo ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { imagePicker.launch("image/*") }
            ) {
                val imageModel: Any = if (profileImagePath.isNotEmpty() && File(profileImagePath).exists()) {
                    File(profileImagePath)
                } else {
                    Icons.Filled.Person
                }

                if (profileImagePath.isNotEmpty() && File(profileImagePath).exists()) {
                    AsyncImage(
                        model = File(profileImagePath),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Default Avatar",
                            tint = Color(0xFF888888),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppPurple)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Full Name ─────────────────────────────────────────────────────
            ProfileFieldLabel("Full Name")
            ProfileTextField(value = fullName, onValueChange = { fullName = it }, placeholder = "Your full name")

            Spacer(modifier = Modifier.height(14.dp))

            // ── DOB + Gender row ──────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Date of Birth
                Column(modifier = Modifier.weight(1f)) {
                    ProfileFieldLabel("Date of Birth")
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { showDatePicker = true }
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (selectedDateStr.isEmpty()) "mm/dd/yyyy" else selectedDateStr,
                                fontSize = 14.sp,
                                color = if (selectedDateStr.isEmpty()) Color(0xFFBBBBBB) else Color(0xFF1A1A2E)
                            )
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF888888),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Gender
                Column(modifier = Modifier.weight(1f)) {
                    ProfileFieldLabel("Gender")
                    Spacer(modifier = Modifier.height(6.dp))
                    ExposedDropdownMenuBox(
                        expanded = genderExpanded,
                        onExpandedChange = { genderExpanded = !genderExpanded }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .menuAnchor()
                                .padding(horizontal = 14.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (gender.isEmpty() || gender == "Select") "Select" else gender,
                                    fontSize = 14.sp,
                                    color = if (gender.isEmpty() || gender == "Select") Color(0xFFBBBBBB) else Color(0xFF1A1A2E)
                                )
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        ExposedDropdownMenu(
                            expanded = genderExpanded,
                            onDismissRequest = { genderExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontSize = 14.sp,
                                            color = if (option == gender) AppPurple else Color(0xFF1A1A2E),
                                            fontWeight = if (option == gender) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        gender = option
                                        genderExpanded = false
                                    },
                                    modifier = Modifier.background(
                                        if (option == gender) AppPurple.copy(alpha = 0.08f) else Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── Phone ─────────────────────────────────────────────────────────
            ProfileFieldLabel("Phone")
            ProfileTextField(value = phone, onValueChange = { phone = it }, placeholder = "+91 00000 00000")

            Spacer(modifier = Modifier.height(14.dp))

            // ── Email ─────────────────────────────────────────────────────────
            ProfileFieldLabel("Email")
            ProfileTextField(value = email, onValueChange = { email = it }, placeholder = "email@example.com")

            Spacer(modifier = Modifier.height(14.dp))

            // ── Address ───────────────────────────────────────────────────────
            ProfileFieldLabel("Address")
            ProfileTextField(value = address, onValueChange = { address = it }, placeholder = "Enter your full address")

            Spacer(modifier = Modifier.height(14.dp))

            // ── Location ──────────────────────────────────────────────────────
            ProfileFieldLabel("Location")
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("City", color = Color(0xFFBBBBBB)) },
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, contentDescription = null,
                        tint = Color(0xFFAAAAAA), modifier = Modifier.size(18.dp))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor   = Color.White,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = AppPurple
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Save Changes ──────────────────────────────────────────────────
            val isLoading = profileState is ProfileState.Loading
            Button(
                onClick = {
                    profileViewModel.updateProfile(
                        name     = fullName,
                        phone    = phone,
                        email    = email,
                        address  = address,
                        location = location,
                        gender   = gender,
                        dob      = selectedDateStr
                    )
                },
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPurple)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color  = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Change Password ───────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null,
                            tint = Color(0xFF888888), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Change Password", fontSize = 15.sp, color = Color(0xFF1A1A2E),
                            fontWeight = FontWeight.Medium)
                    }
                    Icon(Icons.Filled.ArrowBack, contentDescription = null,
                        tint = Color(0xFFAAAAAA), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Activity History ──────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.History, contentDescription = null,
                    tint = AppPurple, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Activity History", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E))
            }
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    activityHistory.forEachIndexed { idx, (action, date, time) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(action, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1A1A2E))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(date, fontSize = 12.sp, color = Color(0xFF999999))
                            }
                            Box(
                                modifier = Modifier
                                    .background(AppPurple.copy(alpha = 0.10f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(time, fontSize = 12.sp, color = AppPurple,
                                    fontWeight = FontWeight.Medium)
                            }
                        }
                        if (idx < activityHistory.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color(0xFFF0F0F0), thickness = 1.dp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

@Composable
private fun ProfileFieldLabel(text: String) {
    Text(text = text, fontSize = 13.sp, color = Color(0xFF555555))
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun ProfileTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFFBBBBBB)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor   = Color.White,
            unfocusedBorderColor    = Color.Transparent,
            focusedBorderColor      = AppPurple
        )
    )
}
