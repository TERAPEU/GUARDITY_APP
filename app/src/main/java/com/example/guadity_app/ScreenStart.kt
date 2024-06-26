package com.example.guadity_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ScreenStart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val animacion: Animation = AnimationUtils.loadAnimation(this, R.anim.animacion_zoom)
        val logo: ImageView = findViewById(R.id.logo_guardity)
        logo.startAnimation(animacion)

        Handler().postDelayed({
            val intent = Intent(this@ScreenStart, ScreenLogin::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}