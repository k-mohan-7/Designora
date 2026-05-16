package com.example.interiordesign_ai.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ─── Data ───────────────────────────────────────────────────────────────────

data class OnboardingPage(
    val cardGradientStart: Color,
    val cardGradientEnd: Color,
    val accentColor: Color,
    val title: String,
    val subtitle: String,
    val features: List<Pair<ImageVector, String>>,
    val iconType: CardIconType
)

enum class CardIconType { CAMERA, BRUSH, FOLDER }

private val pages = listOf(
    OnboardingPage(
        cardGradientStart = Color(0xFF4B0FDB),
        cardGradientEnd   = Color(0xFF2A068A),
        accentColor       = Color(0xFF4B0FDB),
        title    = "AI Interior Design Made Simple",
        subtitle = "Upload your room photo and instantly transform it with AI-powered design.",
        features = listOf(
            Icons.Filled.CropFree    to "Smart room detection",
            Icons.Filled.Brush       to "Style-based customization",
            Icons.Filled.AutoAwesome to "Realistic design output"
        ),
        iconType = CardIconType.CAMERA
    ),
    OnboardingPage(
        cardGradientStart = Color(0xFF0B2557),
        cardGradientEnd   = Color(0xFF091C40),
        accentColor       = Color(0xFF1565C0),
        title    = "Customize Every Detail",
        subtitle = "Personalize your space according to your taste and budget.",
        features = listOf(
            Icons.Filled.Palette       to "Choose colors & furniture",
            Icons.Filled.Lightbulb     to "Select lighting preference",
            Icons.Filled.LocationOn    to "Location-based design logic",
            Icons.Filled.CurrencyRupee to "Budget-aware recommendations"
        ),
        iconType = CardIconType.BRUSH
    ),
    OnboardingPage(
        cardGradientStart = Color(0xFF0A3D26),
        cardGradientEnd   = Color(0xFF062819),
        accentColor       = Color(0xFF1B7A45),
        title    = "Plan. Save. Share.",
        subtitle = "Organize your ideas and make smarter renovation decisions.",
        features = listOf(
            Icons.Filled.CurrencyRupee to "Smart budget estimation",
            Icons.Filled.Favorite      to "Save favorite designs",
            Icons.Filled.CompareArrows to "Before & After comparison",
            Icons.Filled.Share         to "Easy sharing options"
        ),
        iconType = CardIconType.FOLDER
    )
)

// ─── Main Screen ─────────────────────────────────────────────────────────────

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit = {}) {
    val pagerState  = rememberPagerState(pageCount = { pages.size })
    val scope       = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    val isLastPage = currentPage == pages.lastIndex

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(
                state           = pagerState,
                modifier        = Modifier.weight(1f),
                userScrollEnabled = true
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex])
            }

            // Bottom bar: dots + button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Page indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = index == currentPage
                        val dotWidth: Dp by animateDpAsState(
                            targetValue   = if (isSelected) 28.dp else 8.dp,
                            animationSpec = tween(300),
                            label         = "dotWidth"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(dotWidth)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) pages[currentPage].accentColor
                                    else Color(0xFFDDDDDD)
                                )
                        )
                    }
                }

                if (isLastPage) {
                    // "Get Started" pill button
                    Button(
                        onClick = onGetStarted,
                        shape  = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4B0FDB)
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                    ) {
                        Text(
                            text       = "Get Started",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(18.dp)
                        )
                    }
                } else {
                    // Arrow circle button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(pages[currentPage].accentColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null
                            ) {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentPage + 1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint               = Color.White,
                            modifier           = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Skip button (top right overlay)
        if (!isLastPage) {
            Text(
                text       = "Skip",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Medium,
                color      = Color(0xFF888888),
                modifier   = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 52.dp, end = 24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) {
                        scope.launch {
                            pagerState.animateScrollToPage(pages.lastIndex)
                        }
                    }
            )
        }
    }
}

// ─── Single Page Layout ───────────────────────────────────────────────────────

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // ── Top gradient card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(page.cardGradientStart, page.cardGradientEnd),
                        start  = Offset(0f, 0f),
                        end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Decorative dot top-left
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.dp, top = 20.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.35f))
            )
            // Decorative dot bottom-right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 32.dp, bottom = 24.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.35f))
            )

            // White icon card
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(48.dp)) {
                    when (page.iconType) {
                        CardIconType.CAMERA -> drawCameraIcon(this)
                        CardIconType.BRUSH  -> drawBrushIcon(this)
                        CardIconType.FOLDER -> drawFolderIcon(this)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Text content ──
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text       = page.title,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF111111),
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text      = page.subtitle,
                fontSize  = 14.sp,
                color     = Color(0xFF777777),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Feature list ──
            page.features.forEach { (icon, label) ->
                FeatureRow(icon = icon, label = label, accentColor = page.accentColor)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FeatureRow(icon: ImageVector, label: String, accentColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accentColor.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = accentColor,
                modifier           = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text       = label,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium,
            color      = Color(0xFF333333)
        )
    }
}

// ─── Card Icon Drawers ────────────────────────────────────────────────────────

private fun drawCameraIcon(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    val sw = w * 0.08f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val color = Color(0xFF4B0FDB)

    // Camera body
    scope.drawRoundRect(
        color        = color,
        topLeft      = Offset(w * 0.04f, h * 0.28f),
        size         = Size(w * 0.92f, h * 0.58f),
        cornerRadius = CornerRadius(w * 0.12f),
        style        = stroke
    )
    // Lens
    scope.drawCircle(
        color  = color,
        radius = w * 0.18f,
        center = Offset(w * 0.5f, h * 0.57f),
        style  = stroke
    )
    // Notch on top
    val notch = Path().apply {
        moveTo(w * 0.35f, h * 0.28f)
        lineTo(w * 0.38f, h * 0.16f)
        lineTo(w * 0.62f, h * 0.16f)
        lineTo(w * 0.65f, h * 0.28f)
    }
    scope.drawPath(notch, color = color, style = stroke)
}

private fun drawBrushIcon(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    val sw = w * 0.08f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val color = Color(0xFF1565C0)

    // Handle (diagonal line)
    scope.drawLine(
        color       = color,
        start       = Offset(w * 0.75f, h * 0.10f),
        end         = Offset(w * 0.32f, h * 0.62f),
        strokeWidth = sw,
        cap         = StrokeCap.Round
    )
    // Brush head (rounded rect at bottom)
    scope.drawRoundRect(
        color        = color,
        topLeft      = Offset(w * 0.18f, h * 0.62f),
        size         = Size(w * 0.38f, h * 0.28f),
        cornerRadius = CornerRadius(w * 0.10f),
        style        = stroke
    )
}

private fun drawFolderIcon(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    val sw = w * 0.08f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val color = Color(0xFF1B7A45)

    // Folder body
    scope.drawRoundRect(
        color        = color,
        topLeft      = Offset(w * 0.06f, h * 0.32f),
        size         = Size(w * 0.88f, h * 0.56f),
        cornerRadius = CornerRadius(w * 0.12f),
        style        = stroke
    )
    // Folder tab at top-left
    val tab = Path().apply {
        moveTo(w * 0.06f, h * 0.38f)
        lineTo(w * 0.06f, h * 0.28f)
        lineTo(w * 0.38f, h * 0.28f)
        lineTo(w * 0.46f, h * 0.38f)
    }
    scope.drawPath(tab, color = color, style = stroke)
}
