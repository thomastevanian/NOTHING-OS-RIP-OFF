package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalendarEvent
import com.example.data.WeatherData
import com.example.ui.theme.DotlineCardDark
import com.example.ui.theme.DotlineOutlineDark
import com.example.ui.theme.DotlineRed
import com.example.ui.theme.DotlineTextPrimaryDark
import com.example.ui.theme.DotlineTextSecondaryDark
import com.example.ui.theme.DotoFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SpaceMonoFontFamily
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun BuiltinClockWidget(
    timeString: String,
    dateString: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeString,
                color = DotlineTextPrimaryDark,
                fontFamily = DotoFontFamily,
                fontSize = 42.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateString.uppercase(),
                color = DotlineTextSecondaryDark,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun BuiltinBatteryWidget(
    batteryLevel: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DotRingBattery(
                percentage = batteryLevel,
                isCharging = isCharging,
                sizeDp = 64.dp
            )
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = "$batteryLevel%",
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 28.sp
                )
                Text(
                    text = if (isCharging) "CHARGING" else "BATTERY",
                    color = if (isCharging) DotlineRed else DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
fun BuiltinWeatherWidget(
    weather: WeatherData,
    city: String = "London",
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = weather.temperature,
                    color = DotlineTextPrimaryDark,
                    fontFamily = DotoFontFamily,
                    fontSize = 38.sp
                )
                Text(
                    text = weather.condition,
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 10.5.sp,
                    letterSpacing = 0.8.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = city.uppercase(),
                    color = DotlineTextPrimaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "H:${weather.high} L:${weather.low}",
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun BuiltinDateWidget(
    dayString: String,
    monthString: String,
    weekdayString: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayString,
                color = DotlineTextPrimaryDark,
                fontFamily = DotoFontFamily,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$weekdayString, $monthString".uppercase(),
                color = DotlineTextSecondaryDark,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun BuiltinCalendarWidget(
    nextEvent: CalendarEvent?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(DotlineRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "UPCOMING EVENT",
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (nextEvent != null) {
                Text(
                    text = nextEvent.title,
                    color = DotlineTextPrimaryDark,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = nextEvent.time,
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    text = "No upcoming events",
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun BuiltinWorldClockWidget(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val now = Date()
    val fmt = SimpleDateFormat("HH:mm", Locale.US)

    fmt.timeZone = TimeZone.getTimeZone("Europe/London")
    val lonTime = fmt.format(now)

    fmt.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
    val tokTime = fmt.format(now)

    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LON", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 11.sp)
                Text(lonTime, color = DotlineTextPrimaryDark, fontFamily = DotoFontFamily, fontSize = 24.sp)
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(DotlineOutlineDark)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TYO", color = DotlineTextSecondaryDark, fontFamily = SpaceMonoFontFamily, fontSize = 11.sp)
                Text(tokTime, color = DotlineTextPrimaryDark, fontFamily = DotoFontFamily, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun BuiltinQuickNotesWidget(
    noteContent: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    NothingCard(
        modifier = modifier.fillMaxSize(),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NOTE",
                    color = DotlineTextSecondaryDark,
                    fontFamily = SpaceMonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit note",
                    tint = DotlineTextSecondaryDark,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = noteContent,
                color = DotlineTextPrimaryDark,
                fontFamily = SpaceMonoFontFamily,
                fontSize = 11.5.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
