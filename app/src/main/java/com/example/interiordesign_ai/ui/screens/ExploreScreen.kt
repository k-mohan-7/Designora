package com.example.interiordesign_ai.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// ─── Data ─────────────────────────────────────────────────────────────────────

data class DesignLibraryItem(
    val id: String,
    val title: String,
    val budget: String,
    val coverUrl: String,
    val detailImages: List<String>,
    val budgetDetail: String,
    val prompt: String = ""          // AI prompt users can copy to Design Assistant
)

val designLibraryItems = listOf(
    DesignLibraryItem(
        id = "modern_indian_villa",
        title = "Modern Indian Villa",
        budget = "₹15–25 Lakhs",
        coverUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&q=80",
        detailImages = listOf(
            "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800&q=80",
            "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&q=80",
            "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&q=80",
            "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=800&q=80"
        ),
        budgetDetail = "₹15–25 Lakhs",
        prompt = "A modern Indian villa living room with clean lines, large floor-to-ceiling windows, Italian marble flooring, premium L-shaped sofa in off-white fabric, wooden accent wall, recessed cove lighting, indoor plants, minimalist chandelier, warm neutral palette with gold accents, 4K photorealistic interior photograph"
    ),
    DesignLibraryItem(
        id = "traditional_south_indian",
        title = "Traditional South Indian Home",
        budget = "₹10–20 Lakhs",
        coverUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&q=80",
        detailImages = listOf(
            "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&q=80",
            "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&q=80",
            "https://images.unsplash.com/photo-1567016432779-094069958ea5?w=800&q=80",
            "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&q=80"
        ),
        budgetDetail = "₹10–20 Lakhs",
        prompt = "A traditional South Indian home with ornate wooden pillars, carved teak furniture, brass oil lamps, terracotta flooring, Athangudi tiles, antique swing seat, cotton and silk textiles in maroon and gold, warm ambient lighting from hanging brass lamps, courtyard visible through arched doorway, 4K photorealistic interior photograph"
    ),
    DesignLibraryItem(
        id = "north_indian_luxury",
        title = "North Indian Luxury Home",
        budget = "₹20–30 Lakhs",
        coverUrl = "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&q=80",
        detailImages = listOf(
            "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&q=80",
            "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800&q=80",
            "https://images.unsplash.com/photo-1631679706909-1844bbd07221?w=800&q=80",
            "https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&q=80"
        ),
        budgetDetail = "₹20–30 Lakhs",
        prompt = "A luxurious North Indian palace-style living room with Mughal-inspired arches, intricate jali screens, plush velvet sofas in emerald green and burgundy, Rajasthani mirror work on walls, crystal chandelier, white marble flooring with inlay patterns, brass side tables, silk curtains, premium warm lighting, 4K photorealistic interior photograph"
    ),
    DesignLibraryItem(
        id = "compact_indian_apartment",
        title = "Compact Indian Apartment",
        budget = "₹5–15 Lakhs",
        coverUrl = "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=800&q=80",
        detailImages = listOf(
            "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=800&q=80",
            "https://images.unsplash.com/photo-1588854337236-6889d631faa8?w=800&q=80",
            "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=800&q=80",
            "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&q=80"
        ),
        budgetDetail = "₹5–15 Lakhs",
        prompt = "A smart compact Indian apartment living room, space-saving multifunctional furniture, wall-mounted TV unit with storage, foldable dining table, light wood laminate flooring, pastel color palette with sky blue and cream, natural daylight from balcony, minimal spotlights, indoor plant shelf, clean and organized, 4K photorealistic interior photograph"
    )
)

// ─── Explore Screen ───────────────────────────────────────────────────────────

@Composable
fun ExploreScreen(
    onCardClick: (DesignLibraryItem) -> Unit,
    onHomeTab: () -> Unit,
    onBudgetTab: () -> Unit = {},
    onAccountTab: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        // Title
        Text(
            text = "Design Library",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Card list
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            designLibraryItems.forEach { item ->
                DesignLibraryCard(item = item, onClick = { onCardClick(item) })
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Bottom nav
        ExploreBottomNav(onHomeTab = onHomeTab, onBudgetTab = onBudgetTab, onAccountTab = onAccountTab)
    }
}

// ─── Library Card with press-zoom effect ─────────────────────────────────────

@Composable
private fun DesignLibraryCard(item: DesignLibraryItem, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        label = "card_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
    ) {
        // Room photo
        AsyncImage(
            model = item.coverUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(18.dp))
        )
        // Gradient overlay + text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                        startY = 80f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "₹ ", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                Text(
                    text = item.budget,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
            }
            if (item.prompt.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📋 Tap to view & copy prompt",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ─── Explore Bottom Nav ───────────────────────────────────────────────────────

@Composable
private fun ExploreBottomNav(
    onHomeTab: () -> Unit,
    onBudgetTab: () -> Unit = {},
    onAccountTab: () -> Unit = {}
) {
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
            val items = listOf(
                Pair("Home",    Icons.Filled.Home),
                Pair("Explore", Icons.Filled.GridView),
                Pair("Budget",  Icons.Filled.CurrencyRupee),
                Pair("Account", Icons.Filled.Person)
            )
            items.forEachIndexed { index, (label, icon) ->
                val isSelected = index == 1 // Explore is always selected here
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
                                2 -> onBudgetTab()
                                3 -> onAccountTab()
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
