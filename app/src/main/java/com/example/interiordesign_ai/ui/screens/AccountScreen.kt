package com.example.interiordesign_ai.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.interiordesign_ai.viewmodel.AuthViewModel
import com.example.interiordesign_ai.viewmodel.ProfileViewModel
import java.io.File

@Composable
fun AccountScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel:    AuthViewModel    = viewModel(),
    onProfileDetails:   () -> Unit = {},
    onMyDesigns:        () -> Unit = {},
    onNotificationPrefs:() -> Unit = {},
    onPrivacySecurity:  () -> Unit = {},
    onHelpSupport:      () -> Unit = {},
    onContactUs:        () -> Unit = {},
    onAboutApp:         () -> Unit = {},
    onTerms:            () -> Unit = {},
    onLogout:           () -> Unit = {},
    onHomeTab:          () -> Unit = {},
    onExploreTab:       () -> Unit = {},
    onBudgetTab:        () -> Unit = {}
) {
    val user by profileViewModel.user.collectAsState()
    val profileImagePath by profileViewModel.profileImagePath.collectAsState()

    LaunchedEffect(Unit) { profileViewModel.loadProfile() }

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            AccountBottomNav(selectedTab = 3, onHomeTab = onHomeTab, onExploreTab = onExploreTab, onBudgetTab = onBudgetTab)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            Text(
                text = "Account",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Profile Avatar ───────────────────────────────────────────────
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                if (profileImagePath.isNotEmpty() && File(profileImagePath).exists()) {
                    AsyncImage(
                        model = File(profileImagePath),
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Default Avatar",
                            tint = Color(0xFF888888),
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(AppPurple)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user.name.ifBlank { "User" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = user.email.ifBlank { "" },
                fontSize = 13.sp,
                color = Color(0xFF888888),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Personal Information ─────────────────────────────────────────
            AccountSectionLabel("Personal Information")
            Spacer(modifier = Modifier.height(8.dp))
            AccountMenuCard {
                AccountMenuItem(
                    icon  = Icons.Filled.Person,
                    title = "Profile Details",
                    subtitle = "Name, DOB, Gender",
                    onClick = onProfileDetails
                )
                AccountDivider()
                AccountMenuItem(
                    icon  = Icons.Filled.Favorite,
                    title = "My Designs",
                    subtitle = "View saved designs",
                    onClick = onMyDesigns
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── App Preferences ──────────────────────────────────────────────
            AccountSectionLabel("App Preferences")
            Spacer(modifier = Modifier.height(8.dp))
            AccountMenuCard {
                AccountMenuItem(
                    icon  = Icons.Filled.Notifications,
                    title = "Notifications",
                    subtitle = "On",
                    onClick = onNotificationPrefs
                )
                AccountDivider()
                AccountMenuItem(
                    icon  = Icons.Filled.Lock,
                    title = "Privacy & Security",
                    subtitle = null,
                    onClick = onPrivacySecurity
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Support ──────────────────────────────────────────────────────
            AccountSectionLabel("Support")
            Spacer(modifier = Modifier.height(8.dp))
            AccountMenuCard {
                AccountMenuItem(
                    icon  = Icons.Filled.HelpOutline,
                    title = "Help & Support",
                    subtitle = null,
                    onClick = onHelpSupport
                )
                AccountDivider()
                AccountMenuItem(
                    icon  = Icons.Filled.Email,
                    title = "Contact Us",
                    subtitle = null,
                    onClick = onContactUs
                )
                AccountDivider()
                AccountMenuItem(
                    icon  = Icons.Filled.Info,
                    title = "About This App",
                    subtitle = null,
                    onClick = onAboutApp
                )
                AccountDivider()
                AccountMenuItem(
                    icon  = Icons.Filled.Description,
                    title = "Terms & Conditions",
                    subtitle = null,
                    onClick = onTerms
                )
                AccountDivider()
                AccountMenuItem(
                    icon       = Icons.Filled.ExitToApp,
                    title      = "Logout",
                    subtitle   = null,
                    titleColor = Color(0xFFE53935),
                    onClick = {
                        authViewModel.logout()
                        onLogout()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─── Reusable composables ─────────────────────────────────────────────────────

@Composable
private fun AccountSectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AppPurple,
        letterSpacing = 0.4.sp
    )
}

@Composable
private fun AccountMenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun AccountDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 56.dp),
        color = Color(0xFFF0F0F0),
        thickness = 1.dp
    )
}

@Composable
private fun AccountMenuItem(
    icon:       ImageVector,
    title:      String,
    subtitle:   String?,
    titleColor: Color = Color(0xFF1A1A2E),
    onClick:    () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(AppPurple.copy(alpha = 0.10f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AppPurple,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCCCCCC),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Account Bottom Nav ───────────────────────────────────────────────────────

@Composable
private fun AccountBottomNav(
    selectedTab: Int,
    onHomeTab: () -> Unit,
    onExploreTab: () -> Unit = {},
    onBudgetTab: () -> Unit = {}
) {
    val items = listOf(
        Pair("Home",    Icons.Filled.Home),
        Pair("Explore", Icons.Filled.GridView),
        Pair("Budget",  Icons.Filled.CurrencyRupee),
        Pair("Account", Icons.Filled.Person)
    )
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (label, icon) ->
                val isSelected = index == selectedTab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            when (index) {
                                0 -> onHomeTab()
                                1 -> onExploreTab()
                                2 -> onBudgetTab()
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) AppPurple else Color(0xFFAAAAAA),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) AppPurple else Color(0xFFAAAAAA)
                    )
                }
            }
        }
    }
}
