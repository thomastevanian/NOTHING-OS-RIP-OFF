package com.example.data

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.os.UserHandle
import android.util.Log
import com.example.model.AppItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppRepository(private val context: Context) {

    private val _apps = MutableStateFlow<List<AppItem>>(emptyList())
    val apps: StateFlow<List<AppItem>> = _apps.asStateFlow()

    private val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
    private val scope = CoroutineScope(Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val launcherAppsCallback = object : LauncherApps.Callback() {
        override fun onPackageAdded(packageName: String?, user: UserHandle?) {
            reloadApps()
        }

        override fun onPackageRemoved(packageName: String?, user: UserHandle?) {
            reloadApps()
        }

        override fun onPackageChanged(packageName: String?, user: UserHandle?) {
            reloadApps()
        }

        override fun onPackagesAvailable(packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean) {
            reloadApps()
        }

        override fun onPackagesUnavailable(packageNames: Array<out String>?, user: UserHandle?, replacing: Boolean) {
            reloadApps()
        }

        override fun onPackagesSuspended(packageNames: Array<out String>?, user: UserHandle?) {
            reloadApps()
        }

        override fun onPackagesUnsuspended(packageNames: Array<out String>?, user: UserHandle?) {
            reloadApps()
        }
    }

    init {
        try {
            launcherApps?.registerCallback(launcherAppsCallback, mainHandler)
        } catch (e: Exception) {
            Log.e("AppRepository", "Failed to register LauncherApps callback", e)
        }
        reloadApps()
    }

    fun reloadApps() {
        scope.launch {
            val list = mutableListOf<AppItem>()
            val myUser = Process.myUserHandle()

            try {
                if (launcherApps != null) {
                    val profiles = launcherApps.profiles
                    for (user in profiles) {
                        val isWork = user != myUser
                        val activityList: List<LauncherActivityInfo> =
                            launcherApps.getActivityList(null, user)
                        for (info in activityList) {
                            val pkg = info.applicationInfo.packageName
                            // Don't include Dotline launcher in app drawer
                            if (pkg == context.packageName) continue

                            list.add(
                                AppItem(
                                    packageName = pkg,
                                    activityName = info.componentName.className,
                                    label = info.label?.toString() ?: pkg,
                                    isWorkProfile = isWork,
                                    isSuspended = info.applicationInfo.isResourceOverlay
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AppRepository", "LauncherApps query failed, falling back to PackageManager", e)
            }

            if (list.isEmpty()) {
                // Fallback to PackageManager
                try {
                    val pm = context.packageManager
                    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
                    val resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                    for (ri in resolveInfos) {
                        val pkg = ri.activityInfo.packageName
                        if (pkg == context.packageName) continue
                        val label = ri.loadLabel(pm)?.toString() ?: pkg
                        list.add(
                            AppItem(
                                packageName = pkg,
                                activityName = ri.activityInfo.name,
                                label = label
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.e("AppRepository", "PackageManager query failed", e)
                }
            }

            // Deduplicate and sort alphabetically
            val sorted = list.distinctBy { it.id }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.label })
            _apps.value = sorted
        }
    }

    fun launchApp(appItem: AppItem): Boolean {
        return try {
            if (launcherApps != null) {
                launcherApps.startMainActivity(appItem.componentName, Process.myUserHandle(), null, null)
                true
            } else {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_LAUNCHER)
                    component = appItem.componentName
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
                context.startActivity(intent)
                true
            }
        } catch (e: Exception) {
            Log.e("AppRepository", "Failed to launch app: ${appItem.packageName}", e)
            false
        }
    }

    fun openAppInfo(appItem: AppItem) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:${appItem.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AppRepository", "Failed to open app info", e)
        }
    }

    fun uninstallApp(appItem: AppItem) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = android.net.Uri.parse("package:${appItem.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("AppRepository", "Failed to request uninstall", e)
        }
    }

    fun cleanup() {
        try {
            launcherApps?.unregisterCallback(launcherAppsCallback)
        } catch (_: Exception) {}
    }
}
