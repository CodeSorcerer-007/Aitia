package com.aitia.app.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AudioRecorderHelper(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentOutputFile: File? = null
    private var amplitudeJob: Job? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0.0f)
    val currentAmplitude: StateFlow<Float> = _currentAmplitude.asStateFlow()

    private val _amplitudes = MutableStateFlow<List<Float>>(emptyList())
    val amplitudes: StateFlow<List<Float>> = _amplitudes.asStateFlow()

    private val _durationSeconds = MutableStateFlow(0)
    val durationSeconds: StateFlow<Int> = _durationSeconds.asStateFlow()

    fun startRecording(coroutineScope: CoroutineScope): File? {
        try {
            stopPlayback()
            val audioDir = File(context.cacheDir, "audio_memos").apply { mkdirs() }
            val outputFile = File(audioDir, "audio_memo_${System.currentTimeMillis()}.m4a")
            currentOutputFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            _isRecording.value = true
            _amplitudes.value = emptyList()
            _durationSeconds.value = 0

            amplitudeJob = coroutineScope.launch(Dispatchers.IO) {
                var elapsedMs = 0
                while (isActive && _isRecording.value) {
                    val maxAmp = runCatching { mediaRecorder?.maxAmplitude ?: 0 }.getOrDefault(0)
                    val normalized = (maxAmp.toFloat() / 32767.0f).coerceIn(0.05f, 1.0f)
                    _currentAmplitude.value = normalized
                    _amplitudes.value = (_amplitudes.value + normalized).takeLast(40)
                    delay(100)
                    elapsedMs += 100
                    _durationSeconds.value = elapsedMs / 1000
                }
            }
            return outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            _isRecording.value = false
            return null
        }
    }

    fun stopRecording(): File? {
        amplitudeJob?.cancel()
        amplitudeJob = null
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
            _isRecording.value = false
            _currentAmplitude.value = 0.0f
        }
        return currentOutputFile
    }

    fun play(file: File, onComplete: () -> Unit = {}) {
        stopPlayback()
        if (!file.exists()) return

        try {
            val player = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    onComplete()
                }
                start()
            }
            mediaPlayer = player
            _isPlaying.value = true
        } catch (e: IOException) {
            e.printStackTrace()
            _isPlaying.value = false
        }
    }

    fun stopPlayback() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
            _isPlaying.value = false
        }
    }

    fun release() {
        stopRecording()
        stopPlayback()
    }
}
