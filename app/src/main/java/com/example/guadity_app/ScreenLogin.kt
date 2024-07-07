package com.example.guadity_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ScreenLogin : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
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

        val registrarUsuario: TextView = findViewById(R.id.registro)
        val contrasenaEditText = findViewById<EditText>(R.id.Contrasena)
        val olvideContrasena = findViewById<TextView>(R.id.olvide_contrasena)
        val nombreUsuario = findViewById<EditText>(R.id.nombre_usuario)
        val recordarme = findViewById<CheckBox>(R.id.recordarme)
        val button: MaterialButton = findViewById(R.id.btn_iniciar_sesion)

        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val rememberedEmail = sharedPreferences.getString("Email", "")
        val rememberMeChecked = sharedPreferences.getBoolean("RememberMe", false)

        if (rememberMeChecked) {
            nombreUsuario.setText(rememberedEmail)
            recordarme.isChecked = true
        } else {
            nombreUsuario.setText("")
            recordarme.isChecked = false
        }

        registrarUsuario.setOnClickListener {
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

        button.setOnClickListener {
            val emailOrUsername = nombreUsuario.text.toString().trim()
            val password = contrasenaEditText.text.toString().trim()
            saveLoginData(nombreUsuario, recordarme)
            if (emailOrUsername.isEmpty() || password.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "ADVERTENCIA",
                    "Los campos no pueden estar vacíos.",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null
                )
            } else {
                loginUser(emailOrUsername, password)
            }
        }

        olvideContrasena.setOnClickListener {
            val intent = Intent(this, Olvide_Contrasena::class.java)
            startActivity(intent)
        }
    }

    private fun saveLoginData(nombreUsuario: EditText, recordarme: CheckBox) {
        val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val isChecked = recordarme.isChecked
        val email = nombreUsuario.text.toString()

        if (isChecked) {
            editor.putString("Email", email)
            editor.putBoolean("RememberMe", true)
        } else {
            editor.remove("Email")
            editor.putBoolean("RememberMe", false)
        }
        editor.apply()
    }

    private fun loginUser(emailOrUsername: String, password: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) { // Es un correo electrónico
            signInWithEmail(emailOrUsername, password)
        } else {
            db.collection("usuarios")
                .whereEqualTo("nombre", emailOrUsername)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val email = documents.documents[0].getString("email")
                        if (email != null) {
                            signInWithEmail(email, password)
                        } else {
                            showErrorToast("Error al obtener el correo electrónico.")
                        }
                    } else {
                        showErrorToast("El nombre de usuario no existe.")
                    }
                }
                .addOnFailureListener {
                    showErrorToast("Error al obtener el nombre de usuario.")
                }
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        db.collection("usuarios").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    val nombre = document.getString("nombre")
                                    val email = document.getString("email")
                                    if (nombre != null && email != null) {
                                        saveUserData(nombre, email)
                                    }
                                    MotionToast.createColorToast(
                                        this,
                                        "BIENVENIDO",
                                        "Inicio de sesión exitoso.",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        null
                                    )
                                    val intent = Intent(this, ScreenPrincipal::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }
                } else {
                    MotionToast.createColorToast(
                        this,
                        "Error!",
                        "Correo o contraseña incorrectos, inténtalo nuevamente.",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null
                    )
                }
            }
    }

    private fun saveUserData(nombre: String, email: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Nombre", nombre)
        editor.putString("Email", email)
        editor.apply()
    }

    private fun showErrorToast(message: String) {
        MotionToast.createColorToast(
            this,
            "Error!",
            message,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            null
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun togglePasswordVisibility(editText: EditText) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            editText.setCompoundDrawablesWithIntrinsicBounds(
                editText.compoundDrawables[0],
                editText.compoundDrawables[1],
                getDrawable(R.drawable.ocultar_contrase),
                editText.compoundDrawables[3]
            )
        } else {
            // Hide password
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
}
