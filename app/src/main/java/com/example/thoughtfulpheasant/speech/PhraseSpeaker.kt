package com.example.thoughtfulpheasant.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

data class SpeechTier(val pitch: Float, val rate: Float)

private const val TAG = "PhraseSpeaker"

/**
 * Owns the TextToSpeech engine's lifecycle and voice selection.
 * Uses the system default locale and voice.
 */
class PhraseSpeaker(
    context: Context,
    private val onReady: (Boolean) -> Unit
) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            val success = status == TextToSpeech.SUCCESS
            if (success) {
                // Default to system locale
                val locale = Locale.getDefault()
                val result = tts?.setLanguage(locale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Locale $locale is not supported or missing data")
                } else {
                    Log.d(TAG, "TTS Initialized successfully with locale: $locale")
                }
            } else {
                Log.e(TAG, "TTS Initialization failed with status: $status")
            }
            onReady(success)
        }
    }

    fun speak(phrase: String, tier: SpeechTier = SpeechTier(1.0f, 1.0f)) {
        if (phrase.isBlank()) return
        tts?.setPitch(tier.pitch)
        tts?.setSpeechRate(tier.rate)
        tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null, "JokeAppTTS")
    }

    fun stop() {
        tts?.stop()
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
