package com.example

import android.app.Application
import android.content.Context
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DotlineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupCrashHandler()
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                val stackTrace = sw.toString()

                val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                val logEntry = "=== CRASH AT $time ===\nThread: ${thread.name}\n$stackTrace\n\n"

                val file = File(filesDir, CRASH_LOG_FILE)
                file.appendText(logEntry)
                Log.e("DotlineCrash", logEntry)
            } catch (e: Exception) {
                Log.e("DotlineCrash", "Failed to write crash log", e)
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        const val CRASH_LOG_FILE = "crash_log.txt"
        lateinit var instance: DotlineApplication
            private set

        fun getAppContext(): Context = instance.applicationContext
    }
}
