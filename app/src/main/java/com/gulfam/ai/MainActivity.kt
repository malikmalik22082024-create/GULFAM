
package com.gulfam.ai

import android.app.Activity
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var orb: TextView
    private lateinit var input: EditText
    private lateinit var sendButton: Button
    private lateinit var voiceButton: Button

    private val bg = Color.rgb(5, 12, 27)
    private val cyan = Color.rgb(0, 220, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

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

        orb = TextView(this).apply {
            text = "◉"
            textSize = 115f
            gravity = Gravity.CENTER
            setTextColor(cyan)
        }

        status = TextView(this).apply {
            text = "● SYSTEM READY"
            textSize = 14f
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
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO), 101
                    )
                } else {
                    status.text = "MICROPHONE READY"
                    status.setTextColor(cyan)
                }
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

        sendButton = Button(this).apply {
            text = "SEND MESSAGE"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 115, 175))

            setOnClickListener {
                val message = input.text.toString().trim()

                if (message.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Please type a message",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Grok AI connection is not configured yet",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        val footer = TextView(this).apply {
            text = "GULFAM AI  •  FUTURE INTELLIGENCE"
            textSize = 10f
            gravity = Gravity.CENTER
            setTextColor(Color.GRAY)
        }

        main.addView(title)
        main.addView(subtitle)
        main.addView(orb)
        main.addView(status)
        main.addView(greeting)

        addSpace(main, 20)

        main.addView(voiceButton)

        addSpace(main, 22)

        main.addView(section)
        main.addView(input, matchWidth())

        addSpace(main, 8)

        main.addView(sendButton)

        addSpace(main, 25)

        main.addView(footer)

        setContentView(main)
    }

    private fun addSpace(layout: LinearLayout, height: Int) {
        layout.addView(View(this),
            LinearLayout.LayoutParams(1, height))
    }

    private fun matchWidth(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}
