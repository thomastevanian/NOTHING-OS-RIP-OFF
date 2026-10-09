package com.example.model

data class LauncherConfig(
    val gridCols: Int = 5,
    val gridRows: Int = 6,
    val iconStyle: String = "monochrome", // "monochrome", "original", "monochrome_red"
    val iconShape: String = "circle", // "circle", "squircle"
    val iconSizeDp: Int = 52,
    val showLabels: Boolean = true,
    val themeMode: String = "dark", // "dark", "light", "system"
    val clockStyle: String = "dotmatrix", // "dotmatrix", "normal"
    val timeFormat: String = "24", // "12", "24"
    val hideClock: Boolean = false,
    val notificationDots: Boolean = false,
    val doubleTapLock: Boolean = false,
    val autoOpenKeyboard: Boolean = false,
    val infiniteScroll: Boolean = false,
    val quickNotesContent: String = "Nothing to remember.",
    val weatherCity: String = "London",
    val weatherTemp: String = "20°C",
    val weatherCondition: String = "CLEAR SKY"
)
