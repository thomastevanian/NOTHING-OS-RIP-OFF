package com.example.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

enum class WallpaperPattern(val displayName: String) {
    DOT_GRID("Uniform Dot Grid"),
    DOT_GRADIENT("Dot-Matrix Gradient"),
    CONCENTRIC_CIRCLES("Concentric Circles"),
    DOT_MATRIX_TEXT("Dot Matrix Typo"),
    HALFTONE("Halftone Wave"),
    MINIMAL_RED_DOT("Pure Black & Red Dot"),
    LIGHT_GRID("Light Matrix Grid")
}

data class WallpaperConfig(
    val pattern: WallpaperPattern = WallpaperPattern.DOT_GRID,
    val dotRadius: Float = 4f,
    val spacing: Float = 28f,
    val dotColor: Int = Color.WHITE,
    val bgColor: Int = Color.BLACK,
    val customText: String = "NOTHING",
    val seed: Long = 42L,
    val invert: Boolean = false
)

data class WallpaperPreset(
    val id: String,
    val name: String,
    val config: WallpaperConfig
)

object WallpaperGenerator {

    val presets: List<WallpaperPreset> = listOf(
        // Dark minimal presets
        WallpaperPreset("p1", "Void Matrix", WallpaperConfig(WallpaperPattern.DOT_GRID, 3.5f, 26f, Color.WHITE, Color.BLACK)),
        WallpaperPreset("p2", "Red Accent Void", WallpaperConfig(WallpaperPattern.MINIMAL_RED_DOT, 12f, 30f, Color.parseColor("#D71921"), Color.BLACK)),
        WallpaperPreset("p3", "Vertical Fade", WallpaperConfig(WallpaperPattern.DOT_GRADIENT, 6f, 24f, Color.WHITE, Color.BLACK)),
        WallpaperPreset("p4", "Concentric Pulse", WallpaperConfig(WallpaperPattern.CONCENTRIC_CIRCLES, 4f, 28f, Color.WHITE, Color.BLACK)),
        WallpaperPreset("p5", "Nothing Wordmark", WallpaperConfig(WallpaperPattern.DOT_MATRIX_TEXT, 5f, 22f, Color.WHITE, Color.BLACK, customText = "(NOTHING)")),
        WallpaperPreset("p6", "Dotline 5.0", WallpaperConfig(WallpaperPattern.DOT_MATRIX_TEXT, 5f, 22f, Color.parseColor("#D71921"), Color.BLACK, customText = "DOTLINE")),
        WallpaperPreset("p7", "Halftone Wave", WallpaperConfig(WallpaperPattern.HALFTONE, 8f, 24f, Color.WHITE, Color.BLACK)),
        WallpaperPreset("p8", "Dense Mesh", WallpaperConfig(WallpaperPattern.DOT_GRID, 2.5f, 16f, Color.parseColor("#8A8A8A"), Color.BLACK)),
        WallpaperPreset("p9", "Sparse Galaxy", WallpaperConfig(WallpaperPattern.DOT_GRID, 4f, 44f, Color.WHITE, Color.BLACK, seed = 101L)),
        WallpaperPreset("p10", "Red Wave Halftone", WallpaperConfig(WallpaperPattern.HALFTONE, 7f, 26f, Color.parseColor("#D71921"), Color.BLACK)),
        WallpaperPreset("p11", "Inverse Fade", WallpaperConfig(WallpaperPattern.DOT_GRADIENT, 7f, 28f, Color.parseColor("#8A8A8A"), Color.BLACK, invert = true)),
        WallpaperPreset("p12", "Radar Rings", WallpaperConfig(WallpaperPattern.CONCENTRIC_CIRCLES, 3.5f, 22f, Color.parseColor("#D71921"), Color.BLACK)),

        // Light & Inverted presets
        WallpaperPreset("p13", "Whiteout Grid", WallpaperConfig(WallpaperPattern.LIGHT_GRID, 3.5f, 24f, Color.BLACK, Color.parseColor("#F5F5F5"))),
        WallpaperPreset("p14", "Light Gradient", WallpaperConfig(WallpaperPattern.DOT_GRADIENT, 6f, 24f, Color.BLACK, Color.parseColor("#EAEAEA"))),
        WallpaperPreset("p15", "Light Red Dot", WallpaperConfig(WallpaperPattern.MINIMAL_RED_DOT, 14f, 30f, Color.parseColor("#D71921"), Color.parseColor("#F3F3F3"))),
        WallpaperPreset("p16", "Paper Concentric", WallpaperConfig(WallpaperPattern.CONCENTRIC_CIRCLES, 4f, 28f, Color.parseColor("#333333"), Color.parseColor("#F0F0F0"))),
        WallpaperPreset("p17", "Light Halftone", WallpaperConfig(WallpaperPattern.HALFTONE, 7f, 24f, Color.BLACK, Color.parseColor("#FAFAFA"))),
        WallpaperPreset("p18", "Minimal Stone", WallpaperConfig(WallpaperPattern.DOT_GRID, 2.5f, 20f, Color.parseColor("#666666"), Color.parseColor("#E8E8E8"))),

        // Hybrid Red & Monochrome
        WallpaperPreset("p19", "Red Matrix Grid", WallpaperConfig(WallpaperPattern.DOT_GRID, 3.5f, 26f, Color.parseColor("#D71921"), Color.BLACK)),
        WallpaperPreset("p20", "Dual Center", WallpaperConfig(WallpaperPattern.CONCENTRIC_CIRCLES, 5f, 36f, Color.WHITE, Color.BLACK, seed = 777L)),
        WallpaperPreset("p21", "Android 17", WallpaperConfig(WallpaperPattern.DOT_MATRIX_TEXT, 5f, 22f, Color.WHITE, Color.BLACK, customText = "A17·OS")),
        WallpaperPreset("p22", "Subtle Dust", WallpaperConfig(WallpaperPattern.DOT_GRID, 1.8f, 18f, Color.parseColor("#444444"), Color.BLACK)),
        WallpaperPreset("p23", "Deep Horizon", WallpaperConfig(WallpaperPattern.DOT_GRADIENT, 9f, 32f, Color.WHITE, Color.BLACK, seed = 999L)),
        WallpaperPreset("p24", "Final Dot", WallpaperConfig(WallpaperPattern.MINIMAL_RED_DOT, 8f, 24f, Color.WHITE, Color.BLACK))
    )

    fun generate(width: Int, height: Int, config: WallpaperConfig): Bitmap {
        val w = maxOf(360, width)
        val h = maxOf(640, height)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bg = if (config.invert) config.dotColor else config.bgColor
        val fg = if (config.invert) config.bgColor else config.dotColor

        canvas.drawColor(bg)

        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fg
            style = Paint.Style.FILL
        }

        when (config.pattern) {
            WallpaperPattern.DOT_GRID, WallpaperPattern.LIGHT_GRID -> {
                val step = config.spacing
                var y = step
                while (y < h) {
                    var x = step
                    while (x < w) {
                        canvas.drawCircle(x, y, config.dotRadius, dotPaint)
                        x += step
                    }
                    y += step
                }
            }

            WallpaperPattern.DOT_GRADIENT -> {
                val step = config.spacing
                var y = step
                while (y < h) {
                    val progressY = y / h.toFloat()
                    val radiusScale = (1f - progressY).coerceIn(0.1f, 1.5f)
                    var x = step
                    while (x < w) {
                        canvas.drawCircle(x, y, config.dotRadius * radiusScale, dotPaint)
                        x += step
                    }
                    y += step
                }
            }

            WallpaperPattern.CONCENTRIC_CIRCLES -> {
                val cx = w / 2f
                val cy = h / 2f
                val maxRadius = hypot(cx, cy)
                val ringStep = config.spacing * 1.5f
                var r = ringStep
                while (r < maxRadius) {
                    val circumference = 2 * Math.PI * r
                    val dotsOnRing = (circumference / config.spacing).toInt().coerceAtLeast(8)
                    for (i in 0 until dotsOnRing) {
                        val angle = 2 * Math.PI * i / dotsOnRing
                        val x = cx + (r * cos(angle)).toFloat()
                        val y = cy + (r * sin(angle)).toFloat()
                        canvas.drawCircle(x, y, config.dotRadius, dotPaint)
                    }
                    r += ringStep
                }
                // Center dot
                canvas.drawCircle(cx, cy, config.dotRadius * 1.5f, dotPaint)
            }

            WallpaperPattern.HALFTONE -> {
                val step = config.spacing
                val cx = w / 2f
                val cy = h * 0.45f
                var y = step
                while (y < h) {
                    var x = step
                    while (x < w) {
                        val dist = hypot(x - cx, y - cy)
                        val wave = (sin(dist / 60.0) + 1.0) / 2.0
                        val r = (config.dotRadius * wave.toFloat() * 1.8f).coerceAtLeast(0.5f)
                        canvas.drawCircle(x, y, r, dotPaint)
                        x += step
                    }
                    y += step
                }
            }

            WallpaperPattern.MINIMAL_RED_DOT -> {
                // Subtle dot grid with one prominent red dot
                val step = config.spacing * 1.8f
                val faintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#181818")
                    style = Paint.Style.FILL
                }
                var y = step
                while (y < h) {
                    var x = step
                    while (x < w) {
                        canvas.drawCircle(x, y, 2f, faintPaint)
                        x += step
                    }
                    y += step
                }
                // Center accent dot
                val redPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#D71921")
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(w / 2f, h / 2f, config.dotRadius, redPaint)
            }

            WallpaperPattern.DOT_MATRIX_TEXT -> {
                // Background grid
                val step = config.spacing
                val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#151515")
                    style = Paint.Style.FILL
                }
                var gy = step
                while (gy < h) {
                    var gx = step
                    while (gx < w) {
                        canvas.drawCircle(gx, gy, 1.8f, gridPaint)
                        gx += step
                    }
                    gy += step
                }

                // Render dot matrix text in center
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = fg
                    textSize = 48f
                    typeface = Typeface.MONOSPACE
                    textAlign = Paint.Align.CENTER
                    letterSpacing = 0.25f
                }
                canvas.drawText(config.customText, w / 2f, h / 2f, textPaint)
                // Red accent dot next to text
                val redDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#D71921")
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(w / 2f, h / 2f + 40f, 5f, redDotPaint)
            }
        }

        return bitmap
    }

    suspend fun applyWallpaper(context: Context, bitmap: Bitmap, flag: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val wm = WallpaperManager.getInstance(context)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    wm.setBitmap(bitmap, null, true, flag)
                } else {
                    wm.setBitmap(bitmap)
                }
                true
            } catch (e: Exception) {
                Log.e("WallpaperGenerator", "Failed to apply wallpaper", e)
                false
            }
        }
    }
}
