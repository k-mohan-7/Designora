package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacySecurityScreen(onBack: () -> Unit = {}) {
    Scaffold(containerColor = AppBg) { innerPadding ->
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
                    text = "Privacy Policy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Policy card ───────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    PolicySection(
                        number = "1",
                        title  = "Data Collection",
                        body   = "We collect personal information such as name, email, and phone number when you create an account. We also store images you upload for design generation."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PolicySection(
                        number = "2",
                        title  = "Image Usage",
                        body   = "Images uploaded to InterioDecor AI are processed securely. We do not share your personal room photos with third parties without your explicit consent."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PolicySection(
                        number = "3",
                        title  = "Security",
                        body   = "We implement industry-standard security measures to protect your data. However, no method of transmission over the internet is 100% secure."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PolicySection(
                        number = "4",
                        title  = "Cookies & Tracking",
                        body   = "We may use cookies and similar technologies to enhance your experience. You can opt out of non-essential cookies through your device settings."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PolicySection(
                        number = "5",
                        title  = "Your Rights",
                        body   = "You have the right to access, correct, or delete your personal data at any time. Contact our support team to exercise these rights."
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PolicySection(number: String, title: String, body: String) {
    Text(
        text = "$number. $title",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1A1A2E)
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = body,
        fontSize = 13.sp,
        color = Color(0xFF888888),
        lineHeight = 20.sp
    )
}
