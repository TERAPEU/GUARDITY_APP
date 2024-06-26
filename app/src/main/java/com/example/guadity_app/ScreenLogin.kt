package com.example.guadity_app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton


class ScreenLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_login)

        val registro= findViewById<TextView>(R.id.registro)
        val iniciosesion= findViewById<MaterialButton>(R.id.btn_iniciar_sesion)

        iniciosesion.setOnClickListener {
            val intent = Intent(this, ScreenPrincipal::class.java)
            startActivity(intent)
            finish()
        }
        registro.setOnClickListener {
            val intent = Intent(this, ScreenRegister::class.java)
            startActivity(intent)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(this@ScreenLogin)
                builder.setMessage("¿Seguro que quieres salir de esta aplicación?")
                    .setCancelable(false)
                    .setPositiveButton("ACEPTAR") { dialog, id ->
                        finish()
                    }
                    .setNegativeButton("CANCELAR") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        })
        }

    }
