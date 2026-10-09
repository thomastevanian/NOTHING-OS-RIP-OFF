package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DotSeparator(
    modifier: Modifier = Modifier,
    dotColor: Color = DotlineOutlineDark,
    dotRadius: Dp = 1.5.dp,
    spacing: Dp = 8.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(dotRadius * 2)
    ) {
        val r = dotRadius.toPx()
        val step = spacing.toPx()
        var x = r
        while (x < size.width) {
            drawCircle(
                color = dotColor,
                radius = r,
                center = Offset(x, size.height / 2f)
            )
            x += step
        }
    }
}

@Composable
fun DotPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = DotlineRed,
    inactiveColor: Color = DotlineOutlineDark,
    dotSize: Dp = 6.dp,
    spacing: Dp = 10.dp
) {
    Canvas(
        modifier = modifier
            .height(dotSize * 2)
            .padding(horizontal = 8.dp)
    ) {
        val r = dotSize.toPx() / 2f
        val step = spacing.toPx() + dotSize.toPx()
        val totalWidth = pageCount * dotSize.toPx() + (pageCount - 1) * spacing.toPx()
        val startX = (size.width - totalWidth) / 2f

        for (i in 0 until pageCount) {
            val cx = startX + i * step + r
            val cy = size.height / 2f
            val isCurrent = i == currentPage

            drawCircle(
                color = if (isCurrent) activeColor else inactiveColor,
                radius = if (isCurrent) r * 1.25f else r,
                center = Offset(cx, cy)
            )
        }
    }
}

@Composable
fun DottedGridOverlay(
    modifier: Modifier = Modifier,
    dotColor: Color = Color(0x33FFFFFF),
    spacing: Dp = 32.dp
) {
    Canvas(modifier = modifier) {
        val step = spacing.toPx()
        var y = step
        while (y < size.height) {
            var x = step
            while (x < size.width) {
                drawCircle(
                    color = dotColor,
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
                x += step
            }
            y += step
        }
    }
}

@Composable
fun DotRingBattery(
    percentage: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 80.dp,
    dotCount: Int = 24
) {
    Box(modifier = modifier.size(sizeDp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = (minOf(size.width, size.height) - 16.dp.toPx()) / 2f
            val activeDots = ((percentage / 100f) * dotCount).toInt().coerceIn(0, dotCount)

            for (i in 0 until dotCount) {
                val angle = Math.toRadians((i * (360.0 / dotCount) - 90)).toDouble()
                val x = cx + (radius * cos(angle)).toFloat()
                val y = cy + (radius * sin(angle)).toFloat()
                val isActive = i < activeDots

                drawCircle(
                    color = when {
                        isActive && isCharging -> DotlineRed
                        isActive -> Color.White
                        else -> Color(0xFF2A2A2A)
                    },
                    radius = if (isActive) 3.5.dp.toPx() else 2.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun DotProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    dotCount: Int = 16,
    activeColor: Color = Color.White,
    inactiveColor: Color = Color(0xFF2A2A2A)
) {
    Canvas(modifier = modifier.height(10.dp)) {
        val totalWidth = size.width
        val step = totalWidth / dotCount
        val activeLimit = (progress * dotCount).toInt()

        for (i in 0 until dotCount) {
            val cx = i * step + step / 2f
            val cy = size.height / 2f
            drawCircle(
                color = if (i <= activeLimit) activeColor else inactiveColor,
                radius = 3.dp.toPx(),
                center = Offset(cx, cy)
            )
        }
    }
}
