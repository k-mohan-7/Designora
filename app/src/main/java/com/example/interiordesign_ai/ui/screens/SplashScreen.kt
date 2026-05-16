package com.example.interiordesign_ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.interiordesign_ai.session.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onLoggedIn: () -> Unit = {}, onGuest: () -> Unit = {}) {
    val context        = LocalContext.current
    val sessionManager = SessionManager(context)

    LaunchedEffect(Unit) {
        delay(2000L)
        val userId = sessionManager.getUserId()
        if (userId > 0) onLoggedIn() else onGuest()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors  = listOf(Color(0xFF5A20E8), Color(0xFF2D0F9E)),
                    center  = Offset.Unspecified,
                    radius  = 1400f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // White rounded card with house icon
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(26.dp))
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(64.dp)) {
                    drawHouseWithStar(this)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text        = "InterioDecor AI",
                fontSize    = 30.sp,
                fontWeight  = FontWeight.Bold,
                color       = Color.White,
                textAlign   = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text       = "Design Smarter. Build Better.",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Normal,
                color      = Color.White.copy(alpha = 0.85f),
                textAlign  = TextAlign.Center
            )
        }
    }
}

private fun drawHouseWithStar(scope: DrawScope) {
    val w  = scope.size.width
    val h  = scope.size.height
    val sw = w * 0.07f
    val stroke = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val houseColor = Color(0xFF1A1A1A)

    // ── Roof triangle ──
    val roofPath = Path().apply {
        moveTo(w * 0.08f, h * 0.52f)
        lineTo(w * 0.5f,  h * 0.10f)
        lineTo(w * 0.92f, h * 0.52f)
    }
    scope.drawPath(roofPath, color = houseColor, style = stroke)

    // ── Left wall ──
    scope.drawLine(
        color       = houseColor,
        start       = Offset(w * 0.18f, h * 0.49f),
        end         = Offset(w * 0.18f, h * 0.92f),
        strokeWidth = sw,
        cap         = StrokeCap.Round
    )
    // ── Right wall ──
    scope.drawLine(
        color       = houseColor,
        start       = Offset(w * 0.82f, h * 0.49f),
        end         = Offset(w * 0.82f, h * 0.92f),
        strokeWidth = sw,
        cap         = StrokeCap.Round
    )
    // ── Bottom wall ──
    scope.drawLine(
        color       = houseColor,
        start       = Offset(w * 0.18f, h * 0.92f),
        end         = Offset(w * 0.82f, h * 0.92f),
        strokeWidth = sw,
        cap         = StrokeCap.Round
    )

    // ── Door ──
    val doorPath = Path().apply {
        moveTo(w * 0.40f, h * 0.92f)
        lineTo(w * 0.40f, h * 0.67f)
        lineTo(w * 0.60f, h * 0.67f)
        lineTo(w * 0.60f, h * 0.92f)
    }
    scope.drawPath(doorPath, color = houseColor, style = stroke)

    // ── Yellow sparkle / star at top-right ──
    val starColor = Color(0xFFFFC107)
    val cx = w * 0.80f
    val cy = h * 0.08f
    val r  = w * 0.09f
    val starPath = Path().apply {
        moveTo(cx,          cy - r)
        lineTo(cx + r*0.3f, cy - r*0.3f)
        lineTo(cx + r,      cy)
        lineTo(cx + r*0.3f, cy + r*0.3f)
        lineTo(cx,          cy + r)
        lineTo(cx - r*0.3f, cy + r*0.3f)
        lineTo(cx - r,      cy)
        lineTo(cx - r*0.3f, cy - r*0.3f)
        close()
    }
    scope.drawPath(starPath, color = starColor)
}
