package com.example.model

enum class WidgetType(val defaultColSpan: Int, val defaultRowSpan: Int, val title: String) {
    CLOCK_DOTMATRIX(4, 2, "Dot-Matrix Clock"),
    DATE(2, 2, "Calendar Date"),
    BATTERY(2, 2, "Battery Status"),
    WEATHER(4, 2, "Weather"),
    CALENDAR(4, 2, "Upcoming Events"),
    WORLD_CLOCK(4, 2, "World Clock"),
    QUICK_NOTES(2, 2, "Quick Notes")
}

sealed class LauncherGridItem {
    abstract val id: String
    abstract val page: Int
    abstract val col: Int
    abstract val row: Int
    abstract val colSpan: Int
    abstract val rowSpan: Int

    data class App(
        override val id: String,
        override val page: Int,
        override val col: Int,
        override val row: Int,
        val appItem: AppItem,
        override val colSpan: Int = 1,
        override val rowSpan: Int = 1
    ) : LauncherGridItem()

    data class Folder(
        override val id: String,
        override val page: Int,
        override val col: Int,
        override val row: Int,
        val title: String,
        val apps: List<AppItem>,
        override val colSpan: Int = 1,
        override val rowSpan: Int = 1
    ) : LauncherGridItem()

    data class BuiltinWidget(
        override val id: String,
        override val page: Int,
        override val col: Int,
        override val row: Int,
        val type: WidgetType,
        override val colSpan: Int = type.defaultColSpan,
        override val rowSpan: Int = type.defaultRowSpan
    ) : LauncherGridItem()
}
