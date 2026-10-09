package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SpaceMonoFontFamily

@Composable
fun NothingCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = DotlineCardDark,
    borderColor: Color = DotlineOutlineDark,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardModifier = modifier
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun NothingSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackWidth = 44.dp
    val trackHeight = 24.dp
    val thumbSize = 16.dp
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 4.dp,
        label = "switchThumb"
    )

    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(12.dp))
            .background(if (checked) Color(0xFF1E1E1E) else Color(0xFF121212))
            .border(1.dp, if (checked) DotlineRed else DotlineOutlineDark, RoundedCornerShape(12.dp))
            .clickable(
                role = Role.Switch,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(if (checked) DotlineRed else Color(0xFF6E6E6E))
        )
    }
}

@Composable
fun NothingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val bg = when {
        !enabled -> Color(0xFF1C1C1C)
        isPrimary -> Color.White
        else -> Color(0xFF141414)
    }
    val fg = when {
        !enabled -> Color(0xFF555555)
        isPrimary -> Color.Black
        else -> Color.White
    }
    val border = if (isPrimary) Color.Transparent else DotlineOutlineDark

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(24.dp))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = fg,
            fontFamily = SpaceMonoFontFamily,
            fontSize = 12.sp,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun NothingPillSearch(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search apps...",
    onClear: () -> Unit = { onQueryChange("") }
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .background(Color(0xFF101010))
            .border(1.dp, DotlineOutlineDark, shape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = DotlineTextSecondaryDark,
            modifier = Modifier.size(20.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (query.isEmpty()) {
                Text(
                    text = placeholder,
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                textStyle = TextStyle(
                    color = DotlineTextPrimaryDark,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 14.sp
                ),
                cursorBrush = SolidColor(DotlineRed),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onClear),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = DotlineTextSecondaryDark,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
