package com.example.data

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

object BuiltinGlyphs {

    enum class GlyphType {
        PHONE, MESSAGES, CAMERA, GALLERY, CLOCK, CALENDAR, CALCULATOR, SETTINGS,
        BROWSER, MAIL, SEARCH, MAPS, STORE, YOUTUBE, MUSIC, SPOTIFY,
        WHATSAPP, INSTAGRAM, TWITTER, TIKTOK, DISCORD, TELEGRAM, NETFLIX,
        FILES, CONTACTS, NOTES, HEALTH, SHOPPING, CODE, GENERIC
    }

    fun getGlyphTypeForPackage(packageName: String): GlyphType? {
        val pkg = packageName.lowercase()
        return when {
            pkg.contains("dialer") || pkg.contains("phone") || pkg.contains("call") -> GlyphType.PHONE
            pkg.contains("messaging") || pkg.contains("message") || pkg.contains("sms") || pkg.contains("mms") -> GlyphType.MESSAGES
            pkg.contains("camera") -> GlyphType.CAMERA
            pkg.contains("gallery") || pkg.contains("photos") -> GlyphType.GALLERY
            pkg.contains("clock") || pkg.contains("deskclock") -> GlyphType.CLOCK
            pkg.contains("calendar") -> GlyphType.CALENDAR
            pkg.contains("calculator") -> GlyphType.CALCULATOR
            pkg.contains("settings") -> GlyphType.SETTINGS
            pkg.contains("chrome") || pkg.contains("browser") || pkg.contains("firefox") || pkg.contains("sbrowser") -> GlyphType.BROWSER
            pkg.contains("gm") || pkg.contains("mail") || pkg.contains("outlook") -> GlyphType.MAIL
            pkg.contains("googlequicksearchbox") || pkg.contains("search") -> GlyphType.SEARCH
            pkg.contains("maps") || pkg.contains("waze") -> GlyphType.MAPS
            pkg.contains("vending") || pkg.contains("store") -> GlyphType.STORE
            pkg.contains("youtube") && !pkg.contains("music") -> GlyphType.YOUTUBE
            pkg.contains("music") || pkg.contains("spotify") || pkg.contains("sound") -> GlyphType.MUSIC
            pkg.contains("whatsapp") -> GlyphType.WHATSAPP
            pkg.contains("instagram") -> GlyphType.INSTAGRAM
            pkg.contains("twitter") || pkg.contains("x.android") -> GlyphType.TWITTER
            pkg.contains("tiktok") || pkg.contains("musically") -> GlyphType.TIKTOK
            pkg.contains("discord") -> GlyphType.DISCORD
            pkg.contains("telegram") -> GlyphType.TELEGRAM
            pkg.contains("netflix") -> GlyphType.NETFLIX
            pkg.contains("file") || pkg.contains("documentsui") || pkg.contains("myfiles") -> GlyphType.FILES
            pkg.contains("contacts") || pkg.contains("people") -> GlyphType.CONTACTS
            pkg.contains("note") || pkg.contains("keep") || pkg.contains("memo") -> GlyphType.NOTES
            pkg.contains("health") || pkg.contains("fit") || pkg.contains("shealth") -> GlyphType.HEALTH
            pkg.contains("amazon") || pkg.contains("shop") || pkg.contains("ebay") -> GlyphType.SHOPPING
            pkg.contains("github") || pkg.contains("git") -> GlyphType.CODE
            else -> null
        }
    }

    /**
     * Draws the minimalist Nothing OS style vector silhouette onto the canvas within [bounds].
     */
    fun drawGlyph(canvas: Canvas, bounds: RectF, type: GlyphType, paint: Paint) {
        val cx = bounds.centerX()
        val cy = bounds.centerY()
        val w = bounds.width()
        val h = bounds.height()
        val s = minOf(w, h)

        val strokePaint = Paint(paint).apply {
            style = Paint.Style.STROKE
            strokeWidth = s * 0.08f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        val fillPaint = Paint(paint).apply {
            style = Paint.Style.FILL
        }

        when (type) {
            GlyphType.PHONE -> {
                // Curved handset receiver
                val path = Path().apply {
                    moveTo(cx - s * 0.22f, cy - s * 0.22f)
                    cubicTo(cx - s * 0.10f, cy - s * 0.28f, cx + s * 0.10f, cy - s * 0.28f, cx + s * 0.22f, cy - s * 0.22f)
                    lineTo(cx + s * 0.18f, cy - s * 0.08f)
                    lineTo(cx + s * 0.08f, cy - s * 0.10f)
                    cubicTo(cx + s * 0.04f, cy + s * 0.02f, cx - s * 0.04f, cy + s * 0.02f, cx - s * 0.08f, cy - s * 0.10f)
                    close()
                }
                canvas.drawPath(path, strokePaint)
                canvas.drawCircle(cx, cy + s * 0.12f, s * 0.04f, fillPaint)
            }

            GlyphType.MESSAGES -> {
                // Minimal speech bubble with dot-matrix dots
                val rect = RectF(cx - s * 0.28f, cy - s * 0.22f, cx + s * 0.28f, cy + s * 0.18f)
                canvas.drawRoundRect(rect, s * 0.12f, s * 0.12f, strokePaint)
                // Chat dots
                canvas.drawCircle(cx - s * 0.12f, cy - s * 0.02f, s * 0.035f, fillPaint)
                canvas.drawCircle(cx, cy - s * 0.02f, s * 0.035f, fillPaint)
                canvas.drawCircle(cx + s * 0.12f, cy - s * 0.02f, s * 0.035f, fillPaint)
            }

            GlyphType.CAMERA -> {
                // Minimal camera body + lens
                val body = RectF(cx - s * 0.26f, cy - s * 0.16f, cx + s * 0.26f, cy + s * 0.22f)
                canvas.drawRoundRect(body, s * 0.08f, s * 0.08f, strokePaint)
                canvas.drawCircle(cx, cy + s * 0.03f, s * 0.10f, strokePaint)
                canvas.drawCircle(cx + s * 0.16f, cy - s * 0.08f, s * 0.03f, fillPaint)
            }

            GlyphType.GALLERY -> {
                // Minimal photo frame with geometric mountain + sun
                val frame = RectF(cx - s * 0.25f, cy - s * 0.22f, cx + s * 0.25f, cy + s * 0.22f)
                canvas.drawRoundRect(frame, s * 0.08f, s * 0.08f, strokePaint)
                canvas.drawCircle(cx + s * 0.10f, cy - s * 0.08f, s * 0.04f, fillPaint)
                val peak = Path().apply {
                    moveTo(cx - s * 0.18f, cy + s * 0.14f)
                    lineTo(cx - s * 0.05f, cy - s * 0.02f)
                    lineTo(cx + s * 0.08f, cy + s * 0.14f)
                }
                canvas.drawPath(peak, strokePaint)
            }

            GlyphType.CLOCK -> {
                // Minimal dial with dot-matrix hour markers
                canvas.drawCircle(cx, cy, s * 0.26f, strokePaint)
                // Hands pointing to 10:10
                canvas.drawLine(cx, cy, cx - s * 0.10f, cy - s * 0.10f, strokePaint)
                canvas.drawLine(cx, cy, cx + s * 0.14f, cy - s * 0.06f, strokePaint)
            }

            GlyphType.CALENDAR -> {
                val page = RectF(cx - s * 0.25f, cy - s * 0.20f, cx + s * 0.25f, cy + s * 0.24f)
                canvas.drawRoundRect(page, s * 0.06f, s * 0.06f, strokePaint)
                canvas.drawLine(cx - s * 0.25f, cy - s * 0.08f, cx + s * 0.25f, cy - s * 0.08f, strokePaint)
                canvas.drawCircle(cx - s * 0.10f, cy + s * 0.08f, s * 0.03f, fillPaint)
                canvas.drawCircle(cx + s * 0.10f, cy + s * 0.08f, s * 0.03f, fillPaint)
            }

            GlyphType.CALCULATOR -> {
                val box = RectF(cx - s * 0.22f, cy - s * 0.28f, cx + s * 0.22f, cy + s * 0.28f)
                canvas.drawRoundRect(box, s * 0.08f, s * 0.08f, strokePaint)
                // Display
                canvas.drawLine(cx - s * 0.14f, cy - s * 0.16f, cx + s * 0.14f, cy - s * 0.16f, strokePaint)
                // Keypad dots
                for (r in 0..2) {
                    for (c in 0..1) {
                        canvas.drawCircle(cx - s * 0.08f + c * s * 0.16f, cy - s * 0.02f + r * s * 0.10f, s * 0.025f, fillPaint)
                    }
                }
            }

            GlyphType.SETTINGS -> {
                // Minimal cogwheel
                canvas.drawCircle(cx, cy, s * 0.15f, strokePaint)
                for (i in 0 until 6) {
                    val angle = Math.toRadians((i * 60).toDouble())
                    val x1 = cx + (Math.cos(angle) * s * 0.18f).toFloat()
                    val y1 = cy + (Math.sin(angle) * s * 0.18f).toFloat()
                    val x2 = cx + (Math.cos(angle) * s * 0.26f).toFloat()
                    val y2 = cy + (Math.sin(angle) * s * 0.26f).toFloat()
                    canvas.drawLine(x1, y1, x2, y2, strokePaint)
                }
            }

            GlyphType.BROWSER -> {
                // Globe with latitude / longitude
                canvas.drawCircle(cx, cy, s * 0.26f, strokePaint)
                canvas.drawLine(cx - s * 0.26f, cy, cx + s * 0.26f, cy, strokePaint)
                val oval = RectF(cx - s * 0.12f, cy - s * 0.26f, cx + s * 0.12f, cy + s * 0.26f)
                canvas.drawOval(oval, strokePaint)
            }

            GlyphType.MAIL -> {
                val env = RectF(cx - s * 0.26f, cy - s * 0.18f, cx + s * 0.26f, cy + s * 0.18f)
                canvas.drawRoundRect(env, s * 0.06f, s * 0.06f, strokePaint)
                val flap = Path().apply {
                    moveTo(cx - s * 0.26f, cy - s * 0.16f)
                    lineTo(cx, cy + s * 0.04f)
                    lineTo(cx + s * 0.26f, cy - s * 0.16f)
                }
                canvas.drawPath(flap, strokePaint)
            }

            GlyphType.SEARCH -> {
                canvas.drawCircle(cx - s * 0.06f, cy - s * 0.06f, s * 0.16f, strokePaint)
                canvas.drawLine(cx + s * 0.06f, cy + s * 0.06f, cx + s * 0.24f, cy + s * 0.24f, strokePaint)
            }

            GlyphType.MAPS -> {
                val pin = Path().apply {
                    moveTo(cx, cy + s * 0.26f)
                    cubicTo(cx - s * 0.22f, cy + s * 0.05f, cx - s * 0.22f, cy - s * 0.18f, cx, cy - s * 0.18f)
                    cubicTo(cx + s * 0.22f, cy - s * 0.18f, cx + s * 0.22f, cy + s * 0.05f, cx, cy + s * 0.26f)
                    close()
                }
                canvas.drawPath(pin, strokePaint)
                canvas.drawCircle(cx, cy - s * 0.06f, s * 0.05f, fillPaint)
            }

            GlyphType.STORE -> {
                // Minimal shopping bag with handle
                val bag = RectF(cx - s * 0.22f, cy - s * 0.12f, cx + s * 0.22f, cy + s * 0.24f)
                canvas.drawRoundRect(bag, s * 0.06f, s * 0.06f, strokePaint)
                val handle = RectF(cx - s * 0.10f, cy - s * 0.24f, cx + s * 0.10f, cy - s * 0.02f)
                canvas.drawArc(handle, 180f, 180f, false, strokePaint)
            }

            GlyphType.YOUTUBE -> {
                val playBox = RectF(cx - s * 0.28f, cy - s * 0.18f, cx + s * 0.28f, cy + s * 0.18f)
                canvas.drawRoundRect(playBox, s * 0.10f, s * 0.10f, strokePaint)
                val triangle = Path().apply {
                    moveTo(cx - s * 0.06f, cy - s * 0.09f)
                    lineTo(cx + s * 0.09f, cy)
                    lineTo(cx - s * 0.06f, cy + s * 0.09f)
                    close()
                }
                canvas.drawPath(triangle, fillPaint)
            }

            GlyphType.MUSIC -> {
                val note = Path().apply {
                    moveTo(cx - s * 0.06f, cy + s * 0.12f)
                    lineTo(cx - s * 0.06f, cy - s * 0.18f)
                    lineTo(cx + s * 0.16f, cy - s * 0.12f)
                    lineTo(cx + s * 0.16f, cy + s * 0.08f)
                }
                canvas.drawPath(note, strokePaint)
                canvas.drawCircle(cx - s * 0.12f, cy + s * 0.14f, s * 0.07f, fillPaint)
                canvas.drawCircle(cx + s * 0.10f, cy + s * 0.10f, s * 0.07f, fillPaint)
            }

            GlyphType.FILES -> {
                val doc = RectF(cx - s * 0.22f, cy - s * 0.25f, cx + s * 0.22f, cy + s * 0.25f)
                canvas.drawRoundRect(doc, s * 0.06f, s * 0.06f, strokePaint)
                canvas.drawLine(cx - s * 0.12f, cy - s * 0.08f, cx + s * 0.12f, cy - s * 0.08f, strokePaint)
                canvas.drawLine(cx - s * 0.12f, cy + s * 0.02f, cx + s * 0.12f, cy + s * 0.02f, strokePaint)
                canvas.drawLine(cx - s * 0.12f, cy + s * 0.12f, cx + s * 0.04f, cy + s * 0.12f, strokePaint)
            }

            GlyphType.CONTACTS -> {
                canvas.drawCircle(cx, cy - s * 0.08f, s * 0.10f, strokePaint)
                val shoulders = Path().apply {
                    moveTo(cx - s * 0.22f, cy + s * 0.22f)
                    cubicTo(cx - s * 0.20f, cy + s * 0.08f, cx + s * 0.20f, cy + s * 0.08f, cx + s * 0.22f, cy + s * 0.22f)
                }
                canvas.drawPath(shoulders, strokePaint)
            }

            GlyphType.NOTES -> {
                val pad = RectF(cx - s * 0.22f, cy - s * 0.24f, cx + s * 0.22f, cy + s * 0.24f)
                canvas.drawRoundRect(pad, s * 0.06f, s * 0.06f, strokePaint)
                canvas.drawLine(cx - s * 0.12f, cy - s * 0.06f, cx + s * 0.12f, cy - s * 0.06f, strokePaint)
                canvas.drawLine(cx - s * 0.12f, cy + s * 0.04f, cx + s * 0.12f, cy + s * 0.04f, strokePaint)
            }

            GlyphType.HEALTH -> {
                // Heart with pulse line
                val pulse = Path().apply {
                    moveTo(cx - s * 0.24f, cy)
                    lineTo(cx - s * 0.10f, cy)
                    lineTo(cx - s * 0.04f, cy - s * 0.16f)
                    lineTo(cx + s * 0.04f, cy + s * 0.16f)
                    lineTo(cx + s * 0.10f, cy)
                    lineTo(cx + s * 0.24f, cy)
                }
                canvas.drawPath(pulse, strokePaint)
            }

            GlyphType.TWITTER -> {
                // Minimal 'X'
                canvas.drawLine(cx - s * 0.18f, cy - s * 0.18f, cx + s * 0.18f, cy + s * 0.18f, strokePaint)
                canvas.drawLine(cx + s * 0.18f, cy - s * 0.18f, cx - s * 0.18f, cy + s * 0.18f, strokePaint)
            }

            GlyphType.CODE -> {
                // < / >
                val leftTag = Path().apply {
                    moveTo(cx - s * 0.10f, cy - s * 0.14f)
                    lineTo(cx - s * 0.22f, cy)
                    lineTo(cx - s * 0.10f, cy + s * 0.14f)
                }
                val rightTag = Path().apply {
                    moveTo(cx + s * 0.10f, cy - s * 0.14f)
                    lineTo(cx + s * 0.22f, cy)
                    lineTo(cx + s * 0.10f, cy + s * 0.14f)
                }
                canvas.drawPath(leftTag, strokePaint)
                canvas.drawPath(rightTag, strokePaint)
                canvas.drawLine(cx + s * 0.04f, cy - s * 0.16f, cx - s * 0.04f, cy + s * 0.16f, strokePaint)
            }

            else -> {
                // Minimal Nothing OS dot matrix cluster
                for (r in -1..1) {
                    for (c in -1..1) {
                        canvas.drawCircle(cx + c * s * 0.12f, cy + r * s * 0.12f, s * 0.035f, fillPaint)
                    }
                }
            }
        }
    }
}
