package com.example.guadity_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class ScreenRegister : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(this@ScreenRegister)
                builder.setMessage("¿Seguro que quieres Regresar a Inicio?")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar") { dialog, id ->
                        startActivity(Intent(this@ScreenRegister, ScreenLogin::class.java))
                        finish()
                    }
                    .setNegativeButton("Cancelar") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        })

        val button: MaterialButton = findViewById(R.id.btn_registrar)
        val botonAtras: ImageButton = findViewById(R.id.btn_atras)
        val inicio: TextView = findViewById(R.id.btn_iniciar_sesion)

        button.setOnClickListener {
            val intent = Intent(this, ScreenLogin::class.java)
            startActivity(intent)
        }

        botonAtras.setOnClickListener {
            showExitConfirmationDialog()
        }

        inicio.setOnClickListener {
            showExitConfirmationDialog()
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Ya tienes cuenta?")
            .setCancelable(false)
            .setPositiveButton("Sí") { dialog, id ->
                startActivity(Intent(this, ScreenLogin::class.java))
                finish()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
