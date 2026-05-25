package com.example.ui.screens

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.sin

object KidsSoundSynth {
    suspend fun playTone(effectType: String) = withContext(Dispatchers.Default) {
        try {
            val sampleRate = 22050
            val numSamples: Int
            val doubleSamples: DoubleArray
            
            when (effectType) {
                "magic" -> {
                    // Ascending chime sweep
                    val duration = 0.4
                    numSamples = (sampleRate * duration).toInt()
                    doubleSamples = DoubleArray(numSamples)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val freq = 800.0 + (1000.0 * (t / duration))
                        doubleSamples[i] = sin(2.0 * Math.PI * freq * t) * 0.25
                    }
                }
                "laser" -> {
                    // Descending laser sweep
                    val duration = 0.25
                    numSamples = (sampleRate * duration).toInt()
                    doubleSamples = DoubleArray(numSamples)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val freq = 1600.0 - (1200.0 * (t / duration))
                        doubleSamples[i] = sin(2.0 * Math.PI * freq * t) * 0.25
                    }
                }
                "robot" -> {
                    // Dual robotic sound
                    val duration = 0.3
                    numSamples = (sampleRate * duration).toInt()
                    doubleSamples = DoubleArray(numSamples)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val freq = if (t < 0.15) 880.0 else 440.0
                        doubleSamples[i] = sin(2.0 * Math.PI * freq * t) * 0.25
                    }
                }
                "roar" -> {
                    // Low growling rumble sweep
                    val duration = 0.45
                    numSamples = (sampleRate * duration).toInt()
                    doubleSamples = DoubleArray(numSamples)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val freq = 160.0 - (90.0 * (t / duration))
                        val amplitudeMod = sin(2.0 * Math.PI * 35.0 * t) * 0.1 + 0.15
                        doubleSamples[i] = sin(2.0 * Math.PI * freq * t) * amplitudeMod
                    }
                }
                "victory" -> {
                    // Cheerful chord notes arpeggio
                    val duration = 0.5
                    numSamples = (sampleRate * duration).toInt()
                    doubleSamples = DoubleArray(numSamples)
                    val frequencies = listOf(523.25, 659.25, 783.99, 1046.50) // C5, E5, G5, C6
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val noteIndex = ((t / duration) * frequencies.size).toInt().coerceIn(0, frequencies.size - 1)
                        doubleSamples[i] = sin(2.0 * Math.PI * frequencies[noteIndex] * t) * 0.25
                    }
                }
                else -> return@withContext
            }

            // Convert to 16-bit PCM scale
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                samples[i] = (doubleSamples[i] * Short.MAX_VALUE).toInt().toShort()
            }

            val bufferSize = numSamples * 2
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, numSamples)
            audioTrack.playShortWindow()
            delay((numSamples * 1000L) / sampleRate)
            audioTrack.safeClean()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun AudioTrack.playShortWindow() {
        try { play() } catch (t: Throwable) { t.printStackTrace() }
    }

    private fun AudioTrack.safeClean() {
        try {
            stop()
            release()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }
}
