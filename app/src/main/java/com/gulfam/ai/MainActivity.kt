
package com.gulfam.ai

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.graphics.Typeface

class MainActivity : Activity() {

    private lateinit var status: TextView
    private lateinit var speakButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(5, 12, 25)
        window.navigationBarColor = Color.rgb(5, 12, 25)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 30, 28, 30)
            setBackgroundColor(Color.rgb(5, 12, 25))
        }

        val title = TextView(this).apply {
            text = "G U L F A M"
            textSize = 30f
            setTextColor(Color.rgb(0, 220, 255))
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "PERSONAL AI ASSISTANT"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val orb = TextView(this).apply {
            text = "◉"
            textSize = 110f
            setTextColor(Color.rgb(0, 210, 255))
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "SYSTEM ONLINE"
            textSize = 16f
            setTextColor(Color.GREEN)
            gravity = Gravity.CENTER
        }

        val message = TextView(this).apply {
            text = "Hello! I am GULFAM.\nYour personal AI assistant."
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        speakButton = Button(this).apply {
            text = "START VOICE ASSISTANT"
            textSize = 14f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 130, 190))

            setOnClickListener {
                status.text = "VOICE SYSTEM NOT CONNECTED"
            }
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(orb)
        layout.addView(status)
        layout.addView(message)

        val space = android.view.View(this)
        layout.addView(space,
            LinearLayout.LayoutParams(1, 35))

        layout.addView(speakButton)

        setContentView(layout)
    }
}
