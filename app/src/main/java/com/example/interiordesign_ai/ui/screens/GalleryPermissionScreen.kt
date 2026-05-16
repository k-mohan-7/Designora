package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GalleryPermissionScreen(
    onAllow: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large icon circle
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDDD8FF)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    drawGalleryIcon(this)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Gallery Access Required",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "We need access to your photo gallery to upload your room images for AI analysis and redesign.",
                fontSize = 14.sp,
                color = Color(0xFF888888),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Allow Access button
            Button(
                onClick = onAllow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp), spotColor = AppPurple.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppPurple)
            ) {
                Text(
                    text = "Allow Access",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Skip for Now",
                fontSize = 14.sp,
                color = Color(0xFF888888),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSkip
                )
            )
        }
    }
}

private fun drawGalleryIcon(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    val sw = w * 0.08f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val color = AppPurple

    // Outer rounded rectangle
    scope.drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.05f, h * 0.12f),
        size = Size(w * 0.90f, h * 0.78f),
        cornerRadius = CornerRadius(w * 0.14f),
        style = stroke
    )
    // Mountain/image inside
    val mountain = Path().apply {
        moveTo(w * 0.12f, h * 0.78f)
        lineTo(w * 0.38f, h * 0.48f)
        lineTo(w * 0.55f, h * 0.62f)
        lineTo(w * 0.68f, h * 0.50f)
        lineTo(w * 0.88f, h * 0.78f)
        close()
    }
    scope.drawPath(mountain, color = color.copy(alpha = 0.3f))
    scope.drawPath(mountain, color = color, style = stroke)

    // Sun circle top-right inside
    scope.drawCircle(
        color = color,
        radius = w * 0.09f,
        center = Offset(w * 0.73f, h * 0.33f),
        style = stroke
    )
}
