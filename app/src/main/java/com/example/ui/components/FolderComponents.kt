package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppItem
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceMonoFontFamily

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun FolderItemView(
    title: String,
    apps: List<AppItem>,
    iconBitmaps: Map<String, Bitmap>,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 52.dp,
    showLabel: Boolean = true,
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
        // 2x2 miniature icons on 24dp rounded dark tile
        Box(
            modifier = Modifier
                .size(sizeDp)
                .clip(RoundedCornerShape(18.dp))
                .background(DotlineCardDark)
                .border(1.dp, DotlineOutlineDark, RoundedCornerShape(18.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.size(sizeDp - 12.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniIcon(apps.getOrNull(0), iconBitmaps)
                    MiniIcon(apps.getOrNull(1), iconBitmaps)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MiniIcon(apps.getOrNull(2), iconBitmaps)
                    MiniIcon(apps.getOrNull(3), iconBitmaps)
                }
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
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

@Composable
private fun MiniIcon(app: AppItem?, iconBitmaps: Map<String, Bitmap>) {
    val size = 15.dp
    if (app != null) {
        val bmp = iconBitmaps[app.id]
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(size)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Color(0xFF222222))
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFF141414))
        )
    }
}

@Composable
fun FolderDialog(
    title: String,
    apps: List<AppItem>,
    iconBitmaps: Map<String, Bitmap>,
    onTitleChange: (String) -> Unit,
    onAppClick: (AppItem) -> Unit,
    onDismiss: () -> Unit
) {
    var editingTitle by remember { mutableStateOf(title) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0E0E0E))
                .border(1.dp, DotlineOutlineDark, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = editingTitle,
                        onValueChange = {
                            editingTitle = it
                            onTitleChange(it)
                        },
                        textStyle = TextStyle(
                            color = DotlineTextPrimaryDark,
                            fontFamily = SpaceMonoFontFamily,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        ),
                        cursorBrush = SolidColor(DotlineRed),
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = DotlineTextSecondaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                DotSeparator(spacing = 6.dp)
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(apps, key = { it.id }) { app ->
                        AppItemView(
                            appItem = app,
                            iconBitmap = iconBitmaps[app.id],
                            iconSizeDp = 48.dp,
                            showLabel = true,
                            onClick = {
                                onAppClick(app)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}
