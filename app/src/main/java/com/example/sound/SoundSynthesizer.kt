package com.example.sound

import android.content.ContentValues
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.sin

data class SoundPreset(
    val id: String,
    val name: String,
    val category: SoundCategory,
    val durationMs: Int
)

enum class SoundCategory {
    RINGTONE, NOTIFICATION, ALARM, UI_CLICK
}

object SoundSynthesizer {

    private const val SAMPLE_RATE = 44100
    private var activeTrack: AudioTrack? = null

    val ringtones = listOf(
        SoundPreset("ring_1", "Dotline Pulse", SoundCategory.RINGTONE, 3200),
        SoundPreset("ring_2", "Matrix Rhythm", SoundCategory.RINGTONE, 2800),
        SoundPreset("ring_3", "Analog Resonance", SoundCategory.RINGTONE, 3000),
        SoundPreset("ring_4", "Minimal Cadence", SoundCategory.RINGTONE, 3200),
        SoundPreset("ring_5", "Binary Ping", SoundCategory.RINGTONE, 2500),
        SoundPreset("ring_6", "Sub Harmonic", SoundCategory.RINGTONE, 3000),
        SoundPreset("ring_7", "Dotline Metro", SoundCategory.RINGTONE, 2600),
        SoundPreset("ring_8", "Circuit Flow", SoundCategory.RINGTONE, 3400)
    )

    val notifications = listOf(
        SoundPreset("notif_1", "Dot Bleep", SoundCategory.NOTIFICATION, 120),
        SoundPreset("notif_2", "Double Pip", SoundCategory.NOTIFICATION, 180),
        SoundPreset("notif_3", "Glass Tap", SoundCategory.NOTIFICATION, 140),
        SoundPreset("notif_4", "Radar Click", SoundCategory.NOTIFICATION, 100),
        SoundPreset("notif_5", "Sub Snap", SoundCategory.NOTIFICATION, 160),
        SoundPreset("notif_6", "Rising Tone", SoundCategory.NOTIFICATION, 150),
        SoundPreset("notif_7", "Falling Tone", SoundCategory.NOTIFICATION, 150),
        SoundPreset("notif_8", "Matrix Chirp", SoundCategory.NOTIFICATION, 200),
        SoundPreset("notif_9", "Minimal Chord", SoundCategory.NOTIFICATION, 250),
        SoundPreset("notif_10", "Blip Low", SoundCategory.NOTIFICATION, 160),
        SoundPreset("notif_11", "Square Tick", SoundCategory.NOTIFICATION, 90),
        SoundPreset("notif_12", "Red Dot Ping", SoundCategory.NOTIFICATION, 220)
    )

    val alarms = listOf(
        SoundPreset("alarm_1", "Dawn Matrix", SoundCategory.ALARM, 3500),
        SoundPreset("alarm_2", "Staccato Alert", SoundCategory.ALARM, 3000),
        SoundPreset("alarm_3", "Resonant Alarm", SoundCategory.ALARM, 3200),
        SoundPreset("alarm_4", "Pulse Wake", SoundCategory.ALARM, 3600),
        SoundPreset("alarm_5", "Digital Horizon", SoundCategory.ALARM, 3800),
        SoundPreset("alarm_6", "Vibrant Bell", SoundCategory.ALARM, 3200)
    )

    val uiClicks = listOf(
        SoundPreset("click_1", "Soft Tick", SoundCategory.UI_CLICK, 30),
        SoundPreset("click_2", "Micro Click", SoundCategory.UI_CLICK, 20),
        SoundPreset("click_3", "Dot Pop", SoundCategory.UI_CLICK, 45),
        SoundPreset("click_4", "Haptic Ping", SoundCategory.UI_CLICK, 40),
        SoundPreset("click_5", "Snap", SoundCategory.UI_CLICK, 35),
        SoundPreset("click_6", "Switch Tap", SoundCategory.UI_CLICK, 50)
    )

    /**
     * Synthesizes 16-bit PCM samples for the chosen preset.
     */
    fun generateSamples(preset: SoundPreset): ShortArray {
        val totalSamples = (SAMPLE_RATE * (preset.durationMs / 1000.0)).toInt()
        val buffer = ShortArray(totalSamples)

        when (preset.id) {
            // Notifications
            "notif_1" -> generateSineBurst(buffer, 1200.0, 0, 70)
            "notif_2" -> {
                generateSineBurst(buffer, 880.0, 0, 40)
                generateSineBurst(buffer, 1320.0, 60, 50)
            }
            "notif_3" -> generateBellPing(buffer, 1760.0, 0, 100)
            "notif_4" -> generatePerccusiveClick(buffer, 0, 20)
            "notif_5" -> {
                generateSineBurst(buffer, 120.0, 0, 80)
                generatePerccusiveClick(buffer, 0, 15)
            }
            "notif_6" -> generateFrequencySweep(buffer, 500.0, 1200.0, 0, 120)
            "notif_7" -> generateFrequencySweep(buffer, 1300.0, 600.0, 0, 120)
            "notif_8" -> {
                generateSineBurst(buffer, 1000.0, 0, 30)
                generateSineBurst(buffer, 1250.0, 50, 30)
                generateSineBurst(buffer, 1500.0, 100, 40)
            }
            "notif_9" -> {
                generateSineBurst(buffer, 523.25, 0, 200, 0.5)
                generateSineBurst(buffer, 659.25, 0, 200, 0.5)
            }
            "notif_10" -> generateSineBurst(buffer, 260.0, 0, 120)
            "notif_11" -> generateSquarePulse(buffer, 440.0, 0, 50)
            "notif_12" -> generateBellPing(buffer, 2200.0, 0, 180)

            // UI Clicks
            "click_1" -> generatePerccusiveClick(buffer, 0, 8)
            "click_2" -> generateSineBurst(buffer, 2400.0, 0, 6)
            "click_3" -> generateSineBurst(buffer, 400.0, 0, 18)
            "click_4" -> generateFrequencySweep(buffer, 800.0, 1800.0, 0, 14)
            "click_5" -> generatePerccusiveClick(buffer, 0, 15)
            "click_6" -> {
                generatePerccusiveClick(buffer, 0, 8)
                generatePerccusiveClick(buffer, 15, 8)
            }

            // Ringtones
            "ring_1" -> { // Dotline Pulse: 4 rhythmic bursts
                val stepMs = 700
                for (i in 0..3) {
                    val offset = i * stepMs
                    generateSineBurst(buffer, 440.0, offset, 80)
                    generateSineBurst(buffer, 660.0, offset + 120, 80)
                    generateSineBurst(buffer, 880.0, offset + 240, 100)
                }
            }
            "ring_2" -> { // Matrix Rhythm
                val freqs = listOf(587.33, 880.0, 1174.66, 880.0)
                for (i in 0 until 8) {
                    val offset = i * 320
                    val freq = freqs[i % freqs.size]
                    generateBellPing(buffer, freq, offset, 120)
                }
            }
            "ring_3" -> { // Analog Resonance
                for (i in 0..3) {
                    val offset = i * 700
                    generateFrequencySweep(buffer, 350.0, 700.0, offset, 220)
                    generateSineBurst(buffer, 1050.0, offset + 260, 100)
                }
            }
            "ring_4" -> { // Minimal Cadence
                for (i in 0..3) {
                    val offset = i * 750
                    generateSineBurst(buffer, 392.0, offset, 180, 0.6)
                    generateSineBurst(buffer, 587.33, offset + 50, 180, 0.6)
                }
            }
            "ring_5" -> { // Binary Ping
                for (i in 0..5) {
                    val offset = i * 380
                    generateBellPing(buffer, if (i % 2 == 0) 900.0 else 1350.0, offset, 140)
                }
            }
            "ring_6" -> { // Sub Harmonic
                for (i in 0..3) {
                    val offset = i * 700
                    generateSineBurst(buffer, 130.0, offset, 200, 0.8)
                    generateBellPing(buffer, 1040.0, offset + 100, 100, 0.4)
                }
            }
            "ring_7" -> { // Dotline Metro
                for (i in 0..7) {
                    val offset = i * 300
                    if (i % 2 == 0) {
                        generateSineBurst(buffer, 1200.0, offset, 40)
                    } else {
                        generatePerccusiveClick(buffer, offset, 20)
                    }
                }
            }
            "ring_8" -> { // Circuit Flow
                val notes = listOf(523.25, 659.25, 783.99, 1046.50)
                for (i in 0..7) {
                    val offset = i * 380
                    generateBellPing(buffer, notes[i % notes.size], offset, 180)
                }
            }

            // Alarms
            else -> {
                // Repeating ascending alerts
                val stepMs = 600
                for (i in 0 until (preset.durationMs / stepMs)) {
                    val offset = i * stepMs
                    generateSineBurst(buffer, 600.0 + (i % 3) * 300.0, offset, 180)
                    generateSineBurst(buffer, 900.0 + (i % 3) * 300.0, offset + 200, 200)
                }
            }
        }

        return buffer
    }

    private fun generateSineBurst(
        buffer: ShortArray,
        freq: Double,
        offsetMs: Int,
        durationMs: Int,
        gain: Double = 0.85
    ) {
        val startSample = (SAMPLE_RATE * (offsetMs / 1000.0)).toInt()
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val endSample = minOf(buffer.size, startSample + numSamples)

        for (i in startSample until endSample) {
            val t = (i - startSample).toDouble() / SAMPLE_RATE
            val progress = (i - startSample).toDouble() / numSamples
            // Exponential envelope
            val env = (1.0 - progress) * (1.0 - progress)
            val sample = sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE * gain
            val existing = buffer[i].toInt()
            val mixed = (existing + sample.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = mixed.toShort()
        }
    }

    private fun generateBellPing(
        buffer: ShortArray,
        freq: Double,
        offsetMs: Int,
        durationMs: Int,
        gain: Double = 0.85
    ) {
        val startSample = (SAMPLE_RATE * (offsetMs / 1000.0)).toInt()
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val endSample = minOf(buffer.size, startSample + numSamples)

        for (i in startSample until endSample) {
            val t = (i - startSample).toDouble() / SAMPLE_RATE
            val progress = (i - startSample).toDouble() / numSamples
            val env = Math.exp(-progress * 5.0)
            val sample = sin(2.0 * PI * freq * t) * env * Short.MAX_VALUE * gain
            val existing = buffer[i].toInt()
            val mixed = (existing + sample.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = mixed.toShort()
        }
    }

    private fun generateFrequencySweep(
        buffer: ShortArray,
        startFreq: Double,
        endFreq: Double,
        offsetMs: Int,
        durationMs: Int
    ) {
        val startSample = (SAMPLE_RATE * (offsetMs / 1000.0)).toInt()
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val endSample = minOf(buffer.size, startSample + numSamples)

        var phase = 0.0
        for (i in startSample until endSample) {
            val progress = (i - startSample).toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * PI * currentFreq / SAMPLE_RATE
            val env = sin(PI * progress) // smooth window
            val sample = sin(phase) * env * Short.MAX_VALUE * 0.85
            val existing = buffer[i].toInt()
            buffer[i] = (existing + sample.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    private fun generateSquarePulse(
        buffer: ShortArray,
        freq: Double,
        offsetMs: Int,
        durationMs: Int
    ) {
        val startSample = (SAMPLE_RATE * (offsetMs / 1000.0)).toInt()
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val endSample = minOf(buffer.size, startSample + numSamples)

        for (i in startSample until endSample) {
            val t = (i - startSample).toDouble() / SAMPLE_RATE
            val progress = (i - startSample).toDouble() / numSamples
            val env = (1.0 - progress)
            val sinVal = sin(2.0 * PI * freq * t)
            val sq = if (sinVal >= 0) 1.0 else -1.0
            val sample = sq * env * Short.MAX_VALUE * 0.4
            val existing = buffer[i].toInt()
            buffer[i] = (existing + sample.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    private fun generatePerccusiveClick(
        buffer: ShortArray,
        offsetMs: Int,
        durationMs: Int
    ) {
        val startSample = (SAMPLE_RATE * (offsetMs / 1000.0)).toInt()
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val endSample = minOf(buffer.size, startSample + numSamples)

        for (i in startSample until endSample) {
            val progress = (i - startSample).toDouble() / numSamples
            val env = (1.0 - progress) * (1.0 - progress)
            val noise = (Math.random() * 2.0 - 1.0)
            val sample = noise * env * Short.MAX_VALUE * 0.6
            val existing = buffer[i].toInt()
            buffer[i] = (existing + sample.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    fun playPreview(preset: SoundPreset) {
        stopPreview()
        try {
            val samples = generateSamples(preset)
            val minBufSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufSize = maxOf(minBufSize, samples.size * 2)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            activeTrack = track
        } catch (e: Exception) {
            Log.e("SoundSynthesizer", "Failed to play sound preview", e)
        }
    }

    fun stopPreview() {
        try {
            activeTrack?.stop()
            activeTrack?.release()
        } catch (_: Exception) {}
        activeTrack = null
    }

    /**
     * Converts raw PCM samples to a standard RIFF/WAV format byte array.
     */
    fun createWavBytes(samples: ShortArray): ByteArray {
        val byteData = ByteArray(samples.size * 2)
        ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(samples)

        val totalDataLen = byteData.size + 36
        val sampleRate = SAMPLE_RATE
        val channels = 1
        val byteRate = SAMPLE_RATE * channels * 2

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // Subchunk1Size
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // AudioFormat PCM = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * 2).toByte() // BlockAlign
        header[33] = 0
        header[34] = 16 // BitsPerSample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (byteData.size and 0xff).toByte()
        header[41] = ((byteData.size shr 8) and 0xff).toByte()
        header[42] = ((byteData.size shr 16) and 0xff).toByte()
        header[43] = ((byteData.size shr 24) and 0xff).toByte()

        val out = ByteArrayOutputStream()
        out.write(header)
        out.write(byteData)
        return out.toByteArray()
    }

    suspend fun saveToMediaStore(context: Context, preset: SoundPreset): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                val wavBytes = createWavBytes(generateSamples(preset))
                val filename = "Dotline_${preset.name.replace(" ", "_")}.wav"

                val isRingtone = preset.category == SoundCategory.RINGTONE
                val isNotif = preset.category == SoundCategory.NOTIFICATION
                val isAlarm = preset.category == SoundCategory.ALARM

                val relativePath = when {
                    isRingtone -> "Ringtones/"
                    isNotif -> "Notifications/"
                    else -> "Alarms/"
                }

                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Audio.Media.TITLE, "Dotline ${preset.name}")
                    put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
                    put(MediaStore.Audio.Media.IS_RINGTONE, if (isRingtone) 1 else 0)
                    put(MediaStore.Audio.Media.IS_NOTIFICATION, if (isNotif) 1 else 0)
                    put(MediaStore.Audio.Media.IS_ALARM, if (isAlarm) 1 else 0)
                    put(MediaStore.Audio.Media.IS_MUSIC, 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath)
                        put(MediaStore.Audio.Media.IS_PENDING, 1)
                    }
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { os ->
                        os.write(wavBytes)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                        resolver.update(uri, values, null, null)
                    }
                    return@withContext uri
                }
            } catch (e: Exception) {
                Log.e("SoundSynthesizer", "Failed to save sound to MediaStore", e)
            }
            null
        }
    }
}
