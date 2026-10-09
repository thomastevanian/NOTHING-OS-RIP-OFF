package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.LruCache
import com.example.model.AppItem
import java.io.File
import java.io.FileOutputStream

class IconManager(private val context: Context) {

    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8 // 1/8th of available memory

    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    private val diskCacheDir = File(context.cacheDir, "dotline_icons").apply { mkdirs() }

    fun getIcon(
        appItem: AppItem,
        style: String = "monochrome", // "monochrome", "original", "monochrome_red"
        shape: String = "circle",     // "circle", "squircle"
        sizePx: Int = 144
    ): Bitmap {
        val cacheKey = "${appItem.packageName}_${style}_${shape}_$sizePx"

        synchronized(memoryCache) {
            memoryCache.get(cacheKey)?.let { return it }
        }

        // Check disk
        val diskFile = File(diskCacheDir, "$cacheKey.png")
        if (diskFile.exists()) {
            try {
                val diskBitmap = BitmapFactory.decodeFile(diskFile.absolutePath)
                if (diskBitmap != null) {
                    synchronized(memoryCache) {
                        memoryCache.put(cacheKey, diskBitmap)
                    }
                    return diskBitmap
                }
            } catch (_: Exception) {}
        }

        // Generate icon
        val bitmap = renderIcon(appItem, style, shape, sizePx)

        // Save to cache
        synchronized(memoryCache) {
            memoryCache.put(cacheKey, bitmap)
        }
        try {
            FileOutputStream(diskFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: Exception) {
            Log.e("IconManager", "Failed to write icon disk cache", e)
        }

        return bitmap
    }

    private fun renderIcon(
        appItem: AppItem,
        iconStyle: String,
        shape: String,
        sizePx: Int
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0E0E0E")
            this.style = Paint.Style.FILL
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2A2A2A")
            this.style = Paint.Style.STROKE
            strokeWidth = sizePx * 0.02f
        }

        val padding = sizePx * 0.04f
        val rect = RectF(padding, padding, sizePx - padding, sizePx - padding)

        // 1. Draw shape background
        if (shape == "squircle") {
            val radius = sizePx * 0.28f
            canvas.drawRoundRect(rect, radius, radius, bgPaint)
            canvas.drawRoundRect(rect, radius, radius, borderPaint)
        } else {
            val cx = sizePx / 2f
            val cy = sizePx / 2f
            val radius = (sizePx - padding * 2) / 2f
            canvas.drawCircle(cx, cy, radius, bgPaint)
            canvas.drawCircle(cx, cy, radius, borderPaint)
        }

        // 2. Draw glyph / icon
        val glyphType = BuiltinGlyphs.getGlyphTypeForPackage(appItem.packageName)
        val glyphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (iconStyle == "monochrome_red") Color.parseColor("#D71921") else Color.WHITE
        }

        if (iconStyle != "original" && glyphType != null) {
            // Use built-in vector glyph
            val glyphBounds = RectF(
                sizePx * 0.22f,
                sizePx * 0.22f,
                sizePx * 0.78f,
                sizePx * 0.78f
            )
            BuiltinGlyphs.drawGlyph(canvas, glyphBounds, glyphType, glyphPaint)
        } else {
            // Load application drawable
            val originalDrawable = loadAppDrawable(appItem)
            if (originalDrawable != null) {
                if (iconStyle == "original") {
                    // Draw raw icon inside bounds
                    val innerPadding = (sizePx * 0.18f).toInt()
                    originalDrawable.setBounds(
                        innerPadding,
                        innerPadding,
                        sizePx - innerPadding,
                        sizePx - innerPadding
                    )
                    originalDrawable.draw(canvas)
                } else {
                    // Convert original drawable into a clean white silhouette using alpha mask
                    val innerSize = (sizePx * 0.58f).toInt()
                    val monoBitmap = Bitmap.createBitmap(innerSize, innerSize, Bitmap.Config.ARGB_8888)
                    val monoCanvas = Canvas(monoBitmap)
                    originalDrawable.setBounds(0, 0, innerSize, innerSize)
                    originalDrawable.draw(monoCanvas)

                    // Apply white tint with alpha preservation
                    val silhouettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        colorFilter = PorterDuffColorFilter(
                            if (iconStyle == "monochrome_red") Color.parseColor("#D71921") else Color.WHITE,
                            PorterDuff.Mode.SRC_IN
                        )
                    }
                    val offset = (sizePx - innerSize) / 2f
                    canvas.drawBitmap(monoBitmap, offset, offset, silhouettePaint)
                }
            } else {
                // Fallback generic glyph
                val glyphBounds = RectF(
                    sizePx * 0.22f,
                    sizePx * 0.22f,
                    sizePx * 0.78f,
                    sizePx * 0.78f
                )
                BuiltinGlyphs.drawGlyph(canvas, glyphBounds, BuiltinGlyphs.GlyphType.GENERIC, glyphPaint)
            }
        }

        return bitmap
    }

    private fun loadAppDrawable(appItem: AppItem): Drawable? {
        return try {
            val pm = context.packageManager
            pm.getActivityIcon(appItem.componentName)
        } catch (_: Exception) {
            try {
                context.packageManager.getApplicationIcon(appItem.packageName)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun onTrimMemory() {
        memoryCache.evictAll()
    }
}
