package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interiordesign_ai.R
import kotlinx.coroutines.delay

@Composable
fun WelcomePostLoginScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 40.dp)
        ) {
            // App logo
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Designora Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(22.dp), spotColor = Color(0x22000000))
                    .clip(RoundedCornerShape(22.dp))
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Let's design your dream space.",
                fontSize = 15.sp,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center
            )
        }
    }
}
