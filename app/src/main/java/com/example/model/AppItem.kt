package com.example.model

import android.content.ComponentName
import android.os.UserHandle

data class AppItem(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isWorkProfile: Boolean = false,
    val isSuspended: Boolean = false,
    val installTime: Long = 0L,
    val launchCount: Int = 0
) {
    val id: String get() = "$packageName/$activityName"
    val componentName: ComponentName get() = ComponentName(packageName, activityName)
}

data class FolderItem(
    val id: String,
    val title: String,
    val appIds: List<String>
)
