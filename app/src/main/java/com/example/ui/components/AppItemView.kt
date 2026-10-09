package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.SpaceMonoFontFamily

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AppItemView(
    appItem: AppItem,
    iconBitmap: Bitmap?,
    modifier: Modifier = Modifier,
    iconSizeDp: Dp = 52.dp,
    showLabel: Boolean = true,
    hasNotification: Boolean = false,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(iconSizeDp),
            contentAlignment = Alignment.Center
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = appItem.label,
                    modifier = Modifier.size(iconSizeDp)
                )
            } else {
                // Placeholder
                Box(
                    modifier = Modifier
                        .size(iconSizeDp)
                        .clip(CircleShape)
                        .background(Color(0xFF141414))
                        .border(1.dp, Color(0xFF2A2A2A), CircleShape)
                )
            }

            // Notification Dot
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(DotlineRed)
                )
            }

            // Work profile badge
            if (appItem.isWorkProfile) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8A8A8A))
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = appItem.label,
                color = DotlineTextPrimaryDark,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 10.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
