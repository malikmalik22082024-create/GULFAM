
package com.gulfam.ai

import android.app.Activity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.speech.tts.TextToSpeech
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.drawable.GradientDrawable
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var input: EditText
    private lateinit var replyView: TextView
    private lateinit var voiceButton: Button

    private var recognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    private val executor = Executors.newSingleThreadExecutor()

    private val workerUrl =
        "https://rough-lake-e3c4gulfam-ai.malikmalik22082024.workers.dev/"

    private val bg = Color.rgb(5, 12, 27)
    private val cyan = Color.rgb(0, 220, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        tts = TextToSpeech(this) { result ->
            if (result == TextToSpeech.SUCCESS) {
                val language = tts?.setLanguage(Locale.getDefault())
                ttsReady =
                    language != TextToSpeech.LANG_MISSING_DATA &&
                    language != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }

        val main = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(24, 30, 24, 24)
            setBackgroundColor(bg)
        }

        val title = TextView(this).apply {
            text = "G U L F A M"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
        }

        val subtitle = TextView(this).apply {
            text = "PERSONAL AI INTELLIGENCE"
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(Color.LTGRAY)
        }

        val orb = TextView(this).apply {
            text = "◉"
            textSize = 105f
            gravity = Gravity.CENTER
            setTextColor(cyan)
        }

        status = TextView(this).apply {
            text = "● GULFAM ONLINE"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.GREEN)
        }

        val greeting = TextView(this).apply {
            text = "Hello! I am GULFAM.\nYour personal AI assistant."
            textSize = 17f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }

        voiceButton = Button(this).apply {
            text = "🎙 START VOICE ASSISTANT"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 115, 175))

            setOnClickListener {
                startVoiceRecognition()
            }
        }

        val section = TextView(this).apply {
            text = "CHAT WITH GULFAM"
            textSize = 14f
            setTextColor(cyan)
            gravity = Gravity.CENTER
        }

        input = EditText(this).apply {
            hint = "Type your message..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 15f
            setPadding(16, 12, 16, 12)

            background = GradientDrawable().apply {
                setColor(Color.rgb(17, 30, 49))
                cornerRadius = 18f
                setStroke(1, cyan)
            }
        }

        val sendButton = Button(this).apply {
            text = "SEND MESSAGE"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 115, 175))

            setOnClickListener {
                sendMessage(input.text.toString().trim())
            }
        }

        replyView = TextView(this).apply {
            text = "GULFAM's response will appear here."
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(12, 18, 12, 18)
            gravity = Gravity.CENTER
        }

        val footer = TextView(this).apply {
            text = "GULFAM AI • FUTURE INTELLIGENCE"
            textSize = 10f
            gravity = Gravity.CENTER
            setTextColor(Color.GRAY)
        }

        main.addView(title)
        main.addView(subtitle)
        main.addView(orb)
        main.addView(status)
        main.addView(greeting)

        addSpace(main, 15)
        main.addView(voiceButton)

        addSpace(main, 18)
        main.addView(section)
        main.addView(input, matchWidth())

        addSpace(main, 8)
        main.addView(sendButton)
        main.addView(replyView)

        addSpace(main, 15)
        main.addView(footer)

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bg)
        scroll.addView(main)

        setContentView(scroll)
    }

    private fun sendMessage(message: String) {

        if (message.isBlank()) {
            Toast.makeText(
                this,
                "Type a message first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        status.text = "● GULFAM THINKING..."
        status.setTextColor(cyan)
        replyView.text = "Please wait..."

        executor.execute {

            var connection: HttpURLConnection? = null

            try {
                connection = URL(workerUrl).openConnection()
                    as HttpURLConnection

                connection.requestMethod = "POST"
                connection.connectTimeout = 20000
                connection.readTimeout = 60000
                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
                )

                val body = JSONObject()
                    .put("message", message)

                connection.outputStream.use {
                    it.write(
                        body.toString().toByteArray(Charsets.UTF_8)
                    )
                }

                val code = connection.responseCode

                val stream = if (code in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val responseText = stream.bufferedReader().use {
                    it.readText()
                }

                val result = JSONObject(responseText)

                if (code !in 200..299) {
                    throw Exception(
                        result.optString(
                            "error",
                            "HTTP error $code"
                        )
                    )
                }

                val reply = result.optString("reply", "")

                if (reply.isBlank()) {
                    throw Exception("Empty AI response")
                }

                runOnUiThread {
                    replyView.text = "GULFAM:\n\n$reply"

                    status.text = "● GULFAM ONLINE"
                    status.setTextColor(Color.GREEN)

                    speak(reply)
                }

            } catch (e: Exception) {

                runOnUiThread {
                    status.text = "● CONNECTION ERROR"
                    status.setTextColor(Color.RED)

                    replyView.text =
                        "Error: ${e.message}"
                }

            } finally {
                connection?.disconnect()
            }
        }
    }

    private fun startVoiceRecognition() {

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                arrayOf(Manifest.permission.RECORD_AUDIO),
                101
            )
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.text = "Speech recognition unavailable"
            return
        }

        try {
            recognizer?.destroy()

            recognizer =
                SpeechRecognizer.createSpeechRecognizer(this)

            recognizer?.setRecognitionListener(
                object : RecognitionListener {

                    override fun onReadyForSpeech(params: Bundle?) {
                        status.text = "● LISTENING..."
                        status.setTextColor(cyan)
                    }

                    override fun onBeginningOfSpeech() {}

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        status.text = "● PROCESSING VOICE..."
                    }

                    override fun onError(error: Int) {
                        status.text = "● VOICE ERROR: $error"
                        status.setTextColor(Color.RED)
                    }

                    override fun onResults(results: Bundle?) {

                        val words = results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                        val spokenText = words?.firstOrNull()

                        if (!spokenText.isNullOrBlank()) {
                            input.setText(spokenText)
                            sendMessage(spokenText)
                        } else {
                            status.text = "● NO SPEECH DETECTED"
                        }
                    }

                    override fun onPartialResults(
                        partialResults: Bundle?
                    ) {}

                    override fun onEvent(
                        eventType: Int,
                        params: Bundle?
                    ) {}
                }
            )

            val intent = Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault().toLanguageTag()
                )

                putExtra(
                    RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                    false
                )
            }

            recognizer?.startListening(intent)

        } catch (e: Exception) {
            status.text = "● VOICE START FAILED"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == 101 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startVoiceRecognition()
        } else if (requestCode == 101) {
            status.text = "● MICROPHONE PERMISSION DENIED"
        }
    }

    private fun speak(text: String) {
        if (ttsReady) {
            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "GULFAM_REPLY"
            )
        }
    }

    private fun addSpace(
        layout: LinearLayout,
        height: Int
    ) {
        layout.addView(
            View(this),
            LinearLayout.LayoutParams(1, height)
        )
    }

    private fun matchWidth(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroy() {
        recognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
        executor.shutdownNow()
        super.onDestroy()
    }
}
