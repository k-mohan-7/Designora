package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationSettingsScreen(onBack: () -> Unit = {}) {

    var budgetAlerts         by remember { mutableStateOf(true) }
    var aiCompletionAlerts   by remember { mutableStateOf(true) }
    var regenerationAlerts   by remember { mutableStateOf(true) }
    var newDesignAlerts      by remember { mutableStateOf(false) }

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
                    text = "Notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Toggles card ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    NotifToggleRow(
                        title    = "Budget Alerts",
                        subtitle = "Get notified when costs exceed limits",
                        checked  = budgetAlerts,
                        onCheckedChange = { budgetAlerts = it }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFF0F0F0), thickness = 1.dp
                    )
                    NotifToggleRow(
                        title    = "AI Completion Alerts",
                        subtitle = "News about new AI features",
                        checked  = aiCompletionAlerts,
                        onCheckedChange = { aiCompletionAlerts = it }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFF0F0F0), thickness = 1.dp
                    )
                    NotifToggleRow(
                        title    = "Regeneration Notifications",
                        subtitle = "Alerts when regeneration is complete",
                        checked  = regenerationAlerts,
                        onCheckedChange = { regenerationAlerts = it }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color(0xFFF0F0F0), thickness = 1.dp
                    )
                    NotifToggleRow(
                        title    = "New Design Alerts",
                        subtitle = "Weekly inspiration digest",
                        checked  = newDesignAlerts,
                        onCheckedChange = { newDesignAlerts = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // ── Save Settings ─────────────────────────────────────────────────
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPurple)
            ) {
                Text(
                    text = "Save Settings",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun NotifToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E))
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF999999))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor       = Color.White,
                checkedTrackColor       = AppPurple,
                uncheckedThumbColor     = Color.White,
                uncheckedTrackColor     = Color(0xFFDDDDDD),
                uncheckedBorderColor    = Color(0xFFDDDDDD)
            )
        )
    }
}
