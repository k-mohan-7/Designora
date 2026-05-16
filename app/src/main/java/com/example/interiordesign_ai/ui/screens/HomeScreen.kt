package com.example.interiordesign_ai.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.interiordesign_ai.session.SessionManager
import com.example.interiordesign_ai.viewmodel.DesignViewModel
import com.example.interiordesign_ai.viewmodel.NotificationsViewModel

// ─── Image data ──────────────────────────────────────────────────────────────

private data class StyleItem(val label: String, val imageUrl: String, val prompt: String)

private val inspirationItems = listOf(
    StyleItem(
        "Modern Living",
        "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=600&q=80",
        "A modern living room with a plush velvet sofa, glass coffee table, abstract wall art, recessed LED lights, light oak flooring, indoor potted plants, neutral beige and grey palette, floor-to-ceiling windows with sheer curtains, 4K photorealistic interior photograph"
    ),
    StyleItem(
        "Minimalist",
        "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&q=80",
        "A minimalist living room with clean white walls, single low-profile sofa in light grey, walnut wood side table, one statement pendant light, no clutter, natural sunlight streaming in, warm wood flooring, 4K photorealistic interior photograph"
    ),
    StyleItem(
        "Contemporary",
        "https://images.unsplash.com/photo-1567016432779-094069958ea5?w=600&q=80",
        "A contemporary living room with asymmetric shelving, charcoal sectional sofa, geometric rug, chrome floor lamp, concrete accent wall, open plan layout, smart home touch panel, neutral tones with teal accents, 4K photorealistic interior photograph"
    ),
    StyleItem(
        "Luxury",
        "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=600&q=80",
        "A luxury living room with Italian marble flooring, crystal chandelier, gold-trimmed furniture, velvet tufted sofa in royal blue, ornate mirror, silk drapes, ambient cove lighting, fresh flower arrangement on marble table, 4K photorealistic interior photograph"
    ),
)

private val trendingStyles = listOf(
    StyleItem(
        "Japandi",
        "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=600&q=80",
        "A Japandi style living room blending Japanese minimalism with Scandinavian warmth, low wooden platform sofa, tatami-inspired rug, wabi-sabi pottery, natural linen textiles, muted earth tones, paper lantern pendant, indoor bonsai plant, 4K photorealistic interior photograph"
    ),
    StyleItem(
        "Bohemian",
        "https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=600&q=80",
        "A bohemian living room with macramé wall hanging, layered vintage rugs, rattan peacock chair, floor cushions, hanging plants, warm fairy lights, terracotta pots, eclectic mix of patterns and textures, warm sunset lighting, 4K photorealistic interior photograph"
    ),
    StyleItem(
        "Industrial",
        "https://images.unsplash.com/photo-1588854337236-6889d631faa8?w=600&q=80",
        "An industrial loft living room with exposed red brick walls, black iron pipe shelving, distressed leather Chesterfield sofa, Edison bulb pendant lights, concrete floors, metal coffee table, large factory-style windows, dark moody atmosphere, 4K photorealistic interior photograph"
    ),
    StyleItem(
        "Scandi",
        "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600&q=80",
        "A Scandinavian living room with white walls, light birch wood furniture, cozy sheepskin throw on a simple grey sofa, candles on wooden tray, large window with natural light, minimalist floating shelves, hygge atmosphere, soft pastel accents, 4K photorealistic interior photograph"
    ),
)

// ─── HomeScreen ──────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    designViewModel: DesignViewModel             = viewModel(),
    notificationsViewModel: NotificationsViewModel = viewModel(),
    onNotifications: () -> Unit = {},
    onUpload: () -> Unit = {},
    onDesign: () -> Unit = {},
    onSaved: () -> Unit = {},
    onViewAll: () -> Unit = {},
    onExplore: () -> Unit = {},
    onBudget: () -> Unit = {},
    onAccount: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val sessionManager = remember { SessionManager(context) }
    val userName      by sessionManager.userNameFlow().collectAsState(initial = "")
    val recentDesigns by designViewModel.recentDesigns.collectAsState()
    val unreadCount   by notificationsViewModel.unreadCount.collectAsState()

    // Full image viewer state
    var viewerImageUrl by remember { mutableStateOf<String?>(null) }
    var viewerTitle    by remember { mutableStateOf("") }
    var viewerPrompt   by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        designViewModel.loadRecentDesigns()
        notificationsViewModel.loadNotifications()
    }

    // Show full-screen image viewer when an image is tapped
    if (viewerImageUrl != null) {
        FullImageViewer(
            imageUrl = viewerImageUrl!!,
            title    = viewerTitle,
            onDismiss = { viewerImageUrl = null }
        )
    }

    // Show prompt copy dialog when a style card is tapped
    var showPromptDialog by remember { mutableStateOf(false) }
    if (showPromptDialog && viewerPrompt.isNotBlank()) {
        AlertDialog(
            onDismissRequest = { showPromptDialog = false },
            title = {
                Text("✨ $viewerTitle", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column {
                    Text(
                        text = viewerPrompt,
                        fontSize = 13.sp,
                        color = Color(0xFF33691E),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFE8F5E9))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(viewerPrompt))
                                Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                                showPromptDialog = false
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null,
                            tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Prompt", fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Paste in Design Assistant to generate a similar design!",
                        fontSize = 11.sp,
                        color = Color(0xFF888888)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewerImageUrl = null
                    viewerTitle = ""
                    viewerPrompt = ""
                    showPromptDialog = false
                }) { Text("View Full Image", color = AppPurple) }
            },
            dismissButton = {
                TextButton(onClick = { showPromptDialog = false }) {
                    Text("Close", color = Color(0xFF888888))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            HomeBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                onExplore = onExplore,
                onBudget  = onBudget,
                onAccount = onAccount
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hi, ${userName.ifBlank { "User" }} ",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                        Text(text = "👋", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Ready to transform your space?",
                        fontSize = 13.sp,
                        color = Color(0xFF888888)
                    )
                }
                // Bell with red dot
                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNotifications() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = Color(0xFF555555),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935))
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── 3 Quick Action Cards ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    modifier  = Modifier.weight(1f),
                    bgColor   = Color(0xFFEAE4FF),
                    label     = "Upload",
                    onClick   = onUpload,
                    drawIcon  = { m ->
                        Canvas(modifier = m) {
                            val w = size.width; val h = size.height
                            val sw = w * 0.10f
                            val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            drawLine(AppPurple, Offset(w * 0.5f, h * 0.62f), Offset(w * 0.5f, h * 0.16f), sw, StrokeCap.Round)
                            val arrow = Path().apply {
                                moveTo(w * 0.24f, h * 0.38f); lineTo(w * 0.5f, h * 0.16f); lineTo(w * 0.76f, h * 0.38f)
                            }
                            drawPath(arrow, color = AppPurple, style = stroke)
                            val tray = Path().apply {
                                moveTo(w * 0.12f, h * 0.70f); lineTo(w * 0.12f, h * 0.88f)
                                lineTo(w * 0.88f, h * 0.88f); lineTo(w * 0.88f, h * 0.70f)
                            }
                            drawPath(tray, color = AppPurple, style = stroke)
                        }
                    }
                )
                QuickActionCard(
                    modifier  = Modifier.weight(1f),
                    bgColor   = Color(0xFFFFE8EA),
                    label     = "Design",
                    onClick   = onDesign,
                    drawIcon  = { m ->
                        Canvas(modifier = m) {
                            val c = Color(0xFFE53935)
                            val w = size.width; val h = size.height
                            val sw = w * 0.09f
                            val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val palette = Path().apply {
                                moveTo(w * 0.5f, h * 0.06f)
                                cubicTo(w * 0.15f, h * 0.06f, w * 0.05f, h * 0.48f, w * 0.20f, h * 0.68f)
                                cubicTo(w * 0.35f, h * 0.90f, w * 0.65f, h * 0.90f, w * 0.80f, h * 0.68f)
                                cubicTo(w * 0.95f, h * 0.48f, w * 0.85f, h * 0.06f, w * 0.5f, h * 0.06f)
                                close()
                            }
                            drawPath(palette, color = c, style = stroke)
                            drawCircle(c, w * 0.07f, Offset(w * 0.28f, h * 0.36f))
                            drawCircle(c, w * 0.07f, Offset(w * 0.50f, h * 0.22f))
                            drawCircle(c, w * 0.07f, Offset(w * 0.72f, h * 0.36f))
                        }
                    }
                )
                QuickActionCard(
                    modifier  = Modifier.weight(1f),
                    bgColor   = Color(0xFFFFE8EA),
                    label     = "Saved",
                    onClick   = onSaved,
                    drawIcon  = { m ->
                        Canvas(modifier = m) {
                            val c = Color(0xFFE53935)
                            val w = size.width; val h = size.height
                            val sw = w * 0.09f
                            val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val heart = Path().apply {
                                moveTo(w * 0.5f, h * 0.82f)
                                cubicTo(w * 0.10f, h * 0.55f, w * 0.05f, h * 0.22f, w * 0.28f, h * 0.18f)
                                cubicTo(w * 0.40f, h * 0.15f, w * 0.50f, h * 0.28f, w * 0.50f, h * 0.35f)
                                cubicTo(w * 0.50f, h * 0.28f, w * 0.60f, h * 0.15f, w * 0.72f, h * 0.18f)
                                cubicTo(w * 0.95f, h * 0.22f, w * 0.90f, h * 0.55f, w * 0.50f, h * 0.82f)
                                close()
                            }
                            drawPath(heart, color = c, style = stroke)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ── Recent Section ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🕐", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Recent",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onViewAll() }
                ) {
                    Text(text = "View All", fontSize = 13.sp, color = AppPurple, fontWeight = FontWeight.Medium)
                    Text(text = " >", fontSize = 13.sp, color = AppPurple)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (recentDesigns.isEmpty()) {
                // placeholder while loading or no designs yet
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE0DAF5))
                        )
                    }
                }
            } else {
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentDesigns) { design ->
                        val imgUrl = design.generatedImageUrl.ifBlank { design.originalImageUrl }
                        Card(
                            shape     = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier  = Modifier
                                .width(160.dp)
                                .height(110.dp)
                                .clickable {
                                    viewerImageUrl = imgUrl
                                    viewerTitle    = design.title
                                }
                        ) {
                            Box {
                                AsyncImage(
                                    model          = imgUrl.ifBlank { null },
                                    contentDescription = design.title,
                                    contentScale   = ContentScale.Crop,
                                    modifier       = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.45f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text     = design.title,
                                        fontSize = 11.sp,
                                        color    = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scroll indicator dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AppPurple)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFCCC8E8))
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ── AI Tip of the Day ────────────────────────────────────────────
            AiTipCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                onTryDesign = onDesign
            )

            Spacer(modifier = Modifier.height(26.dp))

            // ── Inspiration Section ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "✨", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Inspiration",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(inspirationItems.size) { index ->
                    val item = inspirationItems[index]
                    InspirationCard(
                        label = item.label,
                        imageUrl = item.imageUrl,
                        onClick = {
                            viewerTitle = item.label
                            viewerPrompt = item.prompt
                            showPromptDialog = true
                            viewerImageUrl = item.imageUrl
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // ── Trending Styles ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "📈", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Trending Styles",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TrendingStyleCard(
                        label = trendingStyles[0].label,
                        imageUrl = trendingStyles[0].imageUrl,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewerTitle = trendingStyles[0].label
                            viewerPrompt = trendingStyles[0].prompt
                            showPromptDialog = true
                            viewerImageUrl = trendingStyles[0].imageUrl
                        }
                    )
                    TrendingStyleCard(
                        label = trendingStyles[1].label,
                        imageUrl = trendingStyles[1].imageUrl,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewerTitle = trendingStyles[1].label
                            viewerPrompt = trendingStyles[1].prompt
                            showPromptDialog = true
                            viewerImageUrl = trendingStyles[1].imageUrl
                        }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TrendingStyleCard(
                        label = trendingStyles[2].label,
                        imageUrl = trendingStyles[2].imageUrl,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewerTitle = trendingStyles[2].label
                            viewerPrompt = trendingStyles[2].prompt
                            showPromptDialog = true
                            viewerImageUrl = trendingStyles[2].imageUrl
                        }
                    )
                    TrendingStyleCard(
                        label = trendingStyles[3].label,
                        imageUrl = trendingStyles[3].imageUrl,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewerTitle = trendingStyles[3].label
                            viewerPrompt = trendingStyles[3].prompt
                            showPromptDialog = true
                            viewerImageUrl = trendingStyles[3].imageUrl
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Footer ───────────────────────────────────────────────────────
            Text(
                text = "MADE FOR INDIA WITH ❤️",
                fontSize = 12.sp,
                color = Color(0xFFAAAAAA),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// ─── Quick Action Card ────────────────────────────────────────────────────────

@Composable
private fun QuickActionCard(
    modifier: Modifier,
    bgColor: Color,
    label: String,
    onClick: () -> Unit = {},
    drawIcon: @Composable (Modifier) -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp), spotColor = Color(0x18000000))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                drawIcon(Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
        }
    }
}

// ─── AI Tip Card ─────────────────────────────────────────────────────────────

@Composable
private fun AiTipCard(modifier: Modifier = Modifier, onTryDesign: () -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AppPurple)
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        // Faint watermark circles
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-20).dp)
        )
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.BottomEnd)
                .offset(x = 10.dp, y = 20.dp)
        )

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "✨", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI TIP OF THE DAY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.75f),
                    letterSpacing = 1.2.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "\"Stick to a 60-30-10 color rule for balanced palettes.\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onTryDesign,
                shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f)),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(text = "Try AI Design  →", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

// ─── Inspiration Card ─────────────────────────────────────────────────────────

@Composable
private fun InspirationCard(label: String, imageUrl: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        startY = 60f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "📋 Tap for prompt",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

// ─── Trending Style Card ──────────────────────────────────────────────────────

@Composable
private fun TrendingStyleCard(label: String, imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                        startY = 50f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "📋 Tap for prompt",
                fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

// ─── Bottom Navigation ────────────────────────────────────────────────────────

@Composable
private fun HomeBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onExplore: () -> Unit = {},
    onBudget:  () -> Unit = {},
    onAccount: () -> Unit = {}
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
                                1 -> onExplore()
                                2 -> onBudget()
                                3 -> onAccount()
                                else -> onTabSelected(index)
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
