package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.interiordesign_ai.viewmodel.DesignViewModel

@Composable
fun MyDesignsScreen(
    onBack: () -> Unit,
    designViewModel: DesignViewModel = viewModel()
) {
    val designs by designViewModel.designs.collectAsState()

    // Full image viewer state
    var viewerImageUrl by remember { mutableStateOf<String?>(null) }
    var viewerTitle    by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { designViewModel.loadDesigns() }

    // Show full-screen image viewer when an image is tapped
    if (viewerImageUrl != null) {
        FullImageViewer(
            imageUrl = viewerImageUrl!!,
            title    = viewerTitle,
            onDismiss = { viewerImageUrl = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                text = "My Designs",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
        }

        if (designs.isEmpty()) {
            // Empty state — centered
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0EDF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFCCC8E8),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "No saved designs",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1A1A2E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Designs you generate will appear here.",
                        fontSize = 13.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(designs, key = { it.id }) { design ->
                    val imgUrl = design.generatedImageUrl.ifBlank { design.originalImageUrl }
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier.clickable {
                            viewerImageUrl = imgUrl
                            viewerTitle    = design.title
                        }
                    ) {
                        Column {
                            Box {
                                AsyncImage(
                                    model = design.generatedImageUrl.ifBlank { design.originalImageUrl },
                                    contentDescription = design.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                                )
                                // Heart toggle
                                IconButton(
                                    onClick = { designViewModel.toggleSave(design.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (design.isSaved == 1) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Save",
                                        tint = if (design.isSaved == 1) Color(0xFFE53935) else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = design.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1A1A2E),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = design.designStyle.ifBlank { design.source },
                                        fontSize = 11.sp,
                                        color = Color(0xFF999999)
                                    )
                                }
                                IconButton(
                                    onClick = { designViewModel.deleteDesign(design.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFE53935),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}
