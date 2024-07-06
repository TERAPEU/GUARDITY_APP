package com.example.guadity_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class ScreenLogin : AppCompatActivity() {
    private val auth:FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val registrarusuario: TextView = findViewById(R.id.registro)
        val contrasenaEditText = findViewById<EditText>(R.id.Contrasena)
        registrarusuario.setOnClickListener {
            val intent = Intent(this, ScreenRegister::class.java)
            startActivity(intent)
            finish()
        }


        contrasenaEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val DRAWABLE_END = 2
                if (event.rawX >= (contrasenaEditText.right - contrasenaEditText.compoundDrawables[DRAWABLE_END].bounds.width())) {
                    togglePasswordVisibility(contrasenaEditText)
                    v.performClick()
                    return@setOnTouchListener true
                }
            }
            false
        }
        val button: MaterialButton = findViewById(R.id.btn_iniciar_sesion)
        button.setOnClickListener {
            val email = findViewById<EditText>(R.id.nombre_usuario).text.toString().trim()
            val password = findViewById<EditText>(R.id.Contrasena).text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Los campos no pueden estar vacios", Toast.LENGTH_SHORT).show()
            }
            else
                loginUser(email, password)
        }

    }
    private fun loginUser(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ScreenPrincipal::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this, "Correo o Contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }

                }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun togglePasswordVisibility(editText: EditText) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            // Show password
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                editText.compoundDrawables[0],
                editText.compoundDrawables[1],
                getDrawable(R.drawable.ocultar_contrase),
                editText.compoundDrawables[3]
            )
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                editText.compoundDrawables[0],
                editText.compoundDrawables[1],
                getDrawable(R.drawable.ver_contrase),
                editText.compoundDrawables[3]
            )
        }
        editText.setSelection(editText.text.length)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

}

   //     fun InicioSesionconCorreoyContrasena(email: String, password: String, ){
//        val iniciosesion= findViewById<MaterialButton>(R.id.btn_iniciar_sesion)
//
//        iniciosesion.setOnClickListener {
//            val intent = Intent(this, ScreenPrincipal::class.java)
//            startActivity(intent)
//            finish()
//        }

