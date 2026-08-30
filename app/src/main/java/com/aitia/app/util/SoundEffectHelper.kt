package com.aitia.app.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundEffectHelper {

    private var isSoundEnabled: Boolean = true

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    /**
     * Plays a cheerful celebratory arpeggio chime when a bug is squashed / confetti explodes!
     * Zero external mp3 assets needed: Synthesizes pleasant sine waves dynamically via AudioTrack.
     */
    fun playCelebrationChime(scope: CoroutineScope) {
        if (!isSoundEnabled) return

        scope.launch(Dispatchers.Default) {
            runCatching {
                val sampleRate = 44100
                // Major arpeggio frequencies (C5, E5, G5, C6)
                val notes = listOf(523.25, 659.25, 783.99, 1046.50)
                val noteDurationMs = 90
                val totalSamples = (sampleRate * (noteDurationMs * notes.size) / 1000)
                val buffer = ShortArray(totalSamples)

                var offset = 0
                for (freq in notes) {
                    val noteSamples = (sampleRate * noteDurationMs) / 1000
                    for (i in 0 until noteSamples) {
                        val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                        val envelope = sin(Math.PI * i / noteSamples) // smooth attack & decay
                        val sample = (sin(angle) * envelope * Short.MAX_VALUE * 0.4).toInt()
                        if (offset + i < buffer.size) {
                            buffer[offset + i] = sample.toShort()
                        }
                    }
                    offset += noteSamples
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
            }
        }
    }

    /**
     * Plays a subtle high-tech pop sound on tool activation.
     */
    fun playTactilePop(scope: CoroutineScope) {
        if (!isSoundEnabled) return

        scope.launch(Dispatchers.Default) {
            runCatching {
                val sampleRate = 44100
                val durationMs = 35
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)
                val freq = 880.0

                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / freq)
                    val envelope = 1.0 - (i.toDouble() / numSamples)
                    val sample = (sin(angle) * envelope * Short.MAX_VALUE * 0.25).toInt()
                    buffer[i] = sample.toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
            }
        }
    }
}
