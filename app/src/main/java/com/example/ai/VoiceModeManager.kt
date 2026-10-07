package com.example.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

enum class VoiceState {
    DISCONNECTED,
    CONNECTING,
    LISTENING,
    THINKING,
    SPEAKING,
    MUTED
}

class VoiceModeManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _voiceState = MutableStateFlow(VoiceState.DISCONNECTED)
    val voiceState = _voiceState.asStateFlow()

    private val _amplitude = MutableStateFlow(0.1f)
    val amplitude = _amplitude.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript = _transcript.asStateFlow()

    private val _aiResponse = MutableStateFlow("")
    val aiResponse = _aiResponse.asStateFlow()

    private val _selectedVoice = MutableStateFlow("Breeze")
    val selectedVoice = _selectedVoice.asStateFlow()

    private var waveJob: Job? = null

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setPitch(1.05f)
                tts?.setSpeechRate(1.0f)
                isTtsReady = true

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _voiceState.value = VoiceState.SPEAKING
                    }

                    override fun onDone(utteranceId: String?) {
                        _voiceState.value = VoiceState.LISTENING
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _voiceState.value = VoiceState.LISTENING
                    }
                })
            }
        }
    }

    fun startSession() {
        _voiceState.value = VoiceState.CONNECTING
        scope.launch {
            delay(600)
            _voiceState.value = VoiceState.LISTENING
            _transcript.value = "Listening... Speak naturally"
            startWaveAnimation()
        }
    }

    fun stopSession() {
        tts?.stop()
        waveJob?.cancel()
        _voiceState.value = VoiceState.DISCONNECTED
        _transcript.value = ""
        _aiResponse.value = ""
        _amplitude.value = 0.05f
    }

    fun toggleMute() {
        if (_voiceState.value == VoiceState.MUTED) {
            _voiceState.value = VoiceState.LISTENING
        } else {
            tts?.stop()
            _voiceState.value = VoiceState.MUTED
        }
    }

    fun setVoice(voiceName: String) {
        _selectedVoice.value = voiceName
        when (voiceName) {
            "Breeze" -> { tts?.setPitch(1.05f); tts?.setSpeechRate(1.0f) }
            "Cove" -> { tts?.setPitch(0.92f); tts?.setSpeechRate(0.95f) }
            "Ember" -> { tts?.setPitch(0.85f); tts?.setSpeechRate(1.02f) }
            "Juniper" -> { tts?.setPitch(1.15f); tts?.setSpeechRate(1.05f) }
            "Sol" -> { tts?.setPitch(1.0f); tts?.setSpeechRate(1.08f) }
        }
    }

    fun submitSpokenQuery(userSpeech: String, onAiResponse: suspend (String) -> String) {
        if (_voiceState.value == VoiceState.MUTED) return

        _transcript.value = userSpeech
        _voiceState.value = VoiceState.THINKING

        scope.launch {
            delay(500)
            val answer = onAiResponse(userSpeech)
            _aiResponse.value = answer
            speak(answer)
        }
    }

    fun speak(text: String) {
        val cleanText = text.replace(Regex("[#*`_\\[\\]]"), "").take(400)
        if (isTtsReady && tts != null) {
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "VOICE_MODE_${System.currentTimeMillis()}")
        } else {
            _voiceState.value = VoiceState.SPEAKING
            scope.launch {
                delay(2500)
                _voiceState.value = VoiceState.LISTENING
            }
        }
    }

    private fun startWaveAnimation() {
        waveJob?.cancel()
        waveJob = scope.launch(Dispatchers.Default) {
            var step = 0f
            while (isActive) {
                step += 0.2f
                val base = when (_voiceState.value) {
                    VoiceState.SPEAKING -> 0.7f + (kotlin.math.sin(step) * 0.25f).toFloat()
                    VoiceState.LISTENING -> 0.35f + (kotlin.math.sin(step * 0.7) * 0.15f).toFloat()
                    VoiceState.THINKING -> 0.5f + (kotlin.math.cos(step * 1.5) * 0.2f).toFloat()
                    else -> 0.1f
                }
                _amplitude.value = base.coerceIn(0.05f, 1.0f)
                delay(50)
            }
        }
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        waveJob?.cancel()
    }
}
