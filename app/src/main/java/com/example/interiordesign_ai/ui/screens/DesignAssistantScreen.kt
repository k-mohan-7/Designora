package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.interiordesign_ai.viewmodel.DesignOpState
import com.example.interiordesign_ai.viewmodel.DesignViewModel

@Composable
fun DesignAssistantScreen(
    onBack: () -> Unit,
    designViewModel: DesignViewModel = viewModel()
) {
    var prompt by remember { mutableStateOf("") }

    val opState         by designViewModel.opState.collectAsState()
    val generatedImage  by designViewModel.generatedImageUrl.collectAsState()
    val snackbarState   = remember { SnackbarHostState() }

    val isLoading = opState is DesignOpState.Loading

    LaunchedEffect(opState) {
        if (opState is DesignOpState.Error) {
            snackbarState.showSnackbar((opState as DesignOpState.Error).message)
            designViewModel.resetOpState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        containerColor = AppBg
    ) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(AppBg)
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1A1A2E),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Design Assistant",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Header card ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEDE8FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✨", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Describe your dream space",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tell us about your room size, style preference, budget, and what you need help with.",
                        fontSize = 13.sp,
                        color = Color(0xFF777777),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Prompt label ───────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "YOUR PROMPT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppPurple,
                    letterSpacing = 1.sp
                )
                Text(
                    text = " *",
                    fontSize = 12.sp,
                    color = Color(0xFFE53935),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Prompt text area ───────────────────────────────────────────
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = {
                    Text(
                        text = "e.g. Design my 600 sq.ft house in modern style under medium budget and suggest furniture.",
                        fontSize = 13.sp,
                        color = Color(0xFFBBBBBB),
                        lineHeight = 19.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = AppPurple
                ),
                maxLines = 8
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Example card ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFEDE8FF))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✨", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Example",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppPurple
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"I need a traditional Indian living room design for a 12×15 ft space. Budget is 3 Lakhs. Include a jhoola and wooden sofa.\"",
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF555555),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── AI Generate button ─────────────────────────────────────────
            Button(
                onClick = {
                    if (prompt.isNotBlank()) {
                        designViewModel.generateFromPrompt(prompt)
                    }
                },
                enabled = !isLoading && prompt.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B61C8))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                } else {
                    Text(
                        text = "AI Generate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // ── Generated Image Result ─────────────────────────────────────
            if (generatedImage != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Generated Design", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = generatedImage,
                    contentDescription = "Generated Design",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val url = generatedImage ?: ""
                        designViewModel.saveDesignFromAssistant(url, prompt)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2DB87A))
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Design", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
    }
}
