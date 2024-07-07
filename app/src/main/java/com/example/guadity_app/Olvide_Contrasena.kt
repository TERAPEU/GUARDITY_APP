package com.example.guadity_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class Olvide_Contrasena : AppCompatActivity() {
    private lateinit var correorecuperar: EditText
    private lateinit var buttonrestablecer: MaterialButton
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_olvide_contrasena)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        correorecuperar = findViewById(R.id.correo_recuperar)
        buttonrestablecer= findViewById(R.id.btn_recuperar)
        auth = FirebaseAuth.getInstance()

        buttonrestablecer.setOnClickListener {
            val email = correorecuperar.text.toString().trim()
            if (email.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "ADVERTENCIA",
                    "Los campos no pueden estar vacios.",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null
                )
            }
            else
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            MotionToast.createColorToast(
                                this,
                                "RECUPERACION DE CONTRASEÑA",
                                "Revisa tu correo electronico y restablece tu contraseña",
                                MotionToastStyle.INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                null
                            )
                            val intent = Intent(this, ScreenLogin ::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            MotionToast.createColorToast(
                                this,
                                "ERROR",
                                "El correo ingresado no es valido.",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                null
                            )
                        }
                    }
        }
    }


}