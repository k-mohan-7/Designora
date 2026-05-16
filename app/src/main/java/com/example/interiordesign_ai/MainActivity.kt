package com.example.interiordesign_ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.interiordesign_ai.ui.screens.AboutAppScreen
import com.example.interiordesign_ai.ui.screens.AccountScreen
import com.example.interiordesign_ai.ui.screens.BudgetEstimatorScreen
import com.example.interiordesign_ai.ui.screens.ContactUsScreen
import com.example.interiordesign_ai.ui.screens.HelpSupportScreen
import com.example.interiordesign_ai.ui.screens.TermsConditionsScreen
import com.example.interiordesign_ai.ui.screens.DesignAssistantScreen
import com.example.interiordesign_ai.ui.screens.DesignDetailScreen
import com.example.interiordesign_ai.ui.screens.EditProfileScreen
import com.example.interiordesign_ai.ui.screens.ExploreScreen
import com.example.interiordesign_ai.ui.screens.GalleryPermissionScreen
import com.example.interiordesign_ai.ui.screens.HomeScreen
import com.example.interiordesign_ai.ui.screens.MyDesignsScreen
import com.example.interiordesign_ai.ui.screens.NotificationPermissionScreen
import com.example.interiordesign_ai.ui.screens.NotificationSettingsScreen
import com.example.interiordesign_ai.ui.screens.NotificationsScreen
import com.example.interiordesign_ai.ui.screens.OnboardingScreen
import com.example.interiordesign_ai.ui.screens.PrivacySecurityScreen
import com.example.interiordesign_ai.ui.screens.SignInScreen
import com.example.interiordesign_ai.ui.screens.SignUpScreen
import com.example.interiordesign_ai.ui.screens.UploadCustomizationScreen
import com.example.interiordesign_ai.session.SessionManager
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.runBlocking
import com.example.interiordesign_ai.ui.screens.WelcomePostLoginScreen
import com.example.interiordesign_ai.ui.screens.designLibraryItems
import com.example.interiordesign_ai.ui.theme.InteriorDesignTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InteriorDesignTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val startDestination = remember {
        val userId = runBlocking { SessionManager(context).getUserId() }
        if (userId > 0) "home" else "onboarding"
    }
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = startDestination) {

        // 1. Onboarding (3 pages) → Get Started → Sign In
        composable("onboarding") {
            OnboardingScreen(onGetStarted = {
                navController.navigate("signin") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        // 3. Sign In → direct to Home
        composable("signin") {
            SignInScreen(
                onSignIn = {
                    navController.navigate("home") {
                        popUpTo("signin") { inclusive = true }
                    }
                },
                onCreateAccount = { navController.navigate("signup") }
            )
        }

        // 4. Sign Up → direct to Home
        composable("signup") {
            SignUpScreen(
                onBack = { navController.navigateUp() },
                onCreateAccount = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // 5. Gallery Permission → Notification Permission
        composable("gallery_permission") {
            GalleryPermissionScreen(
                onAllow = { navController.navigate("notification_permission") },
                onSkip  = { navController.navigate("notification_permission") }
            )
        }

        // 6. Notification Permission → Welcome Post-Login
        composable("notification_permission") {
            NotificationPermissionScreen(
                onAllow = { navController.navigate("welcome_post_login") },
                onSkip  = { navController.navigate("welcome_post_login") }
            )
        }

        // 7. Welcome Post-Login (2 sec auto-navigate) → Home
        composable("welcome_post_login") {
            WelcomePostLoginScreen(onDone = {
                navController.navigate("home") {
                    popUpTo("signin") { inclusive = true }
                }
            })
        }

        // 8. Home screen
        composable("home") {
            HomeScreen(
                onNotifications = { navController.navigate("notifications") },
                onUpload        = { navController.navigate("upload_customization") },
                onDesign        = { navController.navigate("design_assistant") },
                onSaved         = { navController.navigate("my_designs") },
                onViewAll       = { navController.navigate("my_designs") },
                onExplore       = { navController.navigate("explore") },
                onBudget        = { navController.navigate("budget") },
                onAccount       = { navController.navigate("account") }
            )
        }

        // 9. Notifications
        composable("notifications") {
            NotificationsScreen(onBack = { navController.navigateUp() })
        }

        // 10. Upload & Customization
        composable("upload_customization") {
            UploadCustomizationScreen(onBack = { navController.navigateUp() })
        }

        // 11. Design Assistant
        composable("design_assistant") {
            DesignAssistantScreen(onBack = { navController.navigateUp() })
        }

        // 12. My Designs
        composable("my_designs") {
            MyDesignsScreen(onBack = { navController.navigateUp() })
        }

        // 13. Explore / Design Library
        composable("explore") {
            ExploreScreen(
                onCardClick  = { item -> navController.navigate("design_detail/${item.id}") },
                onHomeTab    = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onBudgetTab  = { navController.navigate("budget") { popUpTo("home") { inclusive = false }; launchSingleTop = true } },
                onAccountTab = { navController.navigate("account") { popUpTo("home") { inclusive = false }; launchSingleTop = true } }
            )
        }

        // 14. Design Detail (parameterised by id)
        composable("design_detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val item = designLibraryItems.find { it.id == id }
            if (item != null) {
                DesignDetailScreen(item = item, onBack = { navController.navigateUp() })
            }
        }

        // 15. Budget Estimator
        composable("budget") {
            BudgetEstimatorScreen(
                onBack       = { navController.navigateUp() },
                onHomeTab    = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onExploreTab = { navController.navigate("explore") { popUpTo("home") { inclusive = false }; launchSingleTop = true } },
                onAccountTab = { navController.navigate("account") { popUpTo("home") { inclusive = false }; launchSingleTop = true } }
            )
        }

        // 16. Account
        composable("account") {
            AccountScreen(
                onProfileDetails    = { navController.navigate("edit_profile") },
                onMyDesigns         = { navController.navigate("my_designs") },
                onNotificationPrefs = { navController.navigate("notification_settings") },
                onPrivacySecurity   = { navController.navigate("privacy_security") },
                onHelpSupport       = { navController.navigate("help_support") },
                onContactUs         = { navController.navigate("contact_us") },
                onAboutApp          = { navController.navigate("about_app") },
                onTerms             = { navController.navigate("terms_conditions") },
                onLogout            = {
                    navController.navigate("signin") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onHomeTab    = { navController.navigate("home") { popUpTo("home") { inclusive = true }; launchSingleTop = true } },
                onExploreTab = { navController.navigate("explore") { popUpTo("home") { inclusive = false }; launchSingleTop = true } },
                onBudgetTab  = { navController.navigate("budget") { popUpTo("home") { inclusive = false }; launchSingleTop = true } }
            )
        }

        // 17. Edit Profile
        composable("edit_profile") {
            EditProfileScreen(onBack = { navController.navigateUp() })
        }

        // 18. Notification Settings
        composable("notification_settings") {
            NotificationSettingsScreen(onBack = { navController.navigateUp() })
        }

        // 19. Privacy & Security
        composable("privacy_security") {
            PrivacySecurityScreen(onBack = { navController.navigateUp() })
        }

        // 20. Help & Support
        composable("help_support") {
            HelpSupportScreen(onBack = { navController.navigateUp() })
        }

        // 21. Contact Us
        composable("contact_us") {
            ContactUsScreen(onBack = { navController.navigateUp() })
        }

        // 22. About This App
        composable("about_app") {
            AboutAppScreen(onBack = { navController.navigateUp() })
        }

        // 23. Terms & Conditions
        composable("terms_conditions") {
            TermsConditionsScreen(onBack = { navController.navigateUp() })
        }
    }
}
