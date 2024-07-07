package com.example.guadity_app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.*

class ScreenRegister : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var nombreUsuario: EditText
    private lateinit var correoElectronico: EditText
    private lateinit var contrasena: EditText
    private lateinit var contrasena2: EditText
    private lateinit var fechaNacimiento: EditText
    private lateinit var button: MaterialButton

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

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        nombreUsuario = findViewById(R.id.nombre_usuario)
        correoElectronico = findViewById(R.id.correo_electronico)
        contrasena = findViewById(R.id.Contraseña)
        contrasena2 = findViewById(R.id.Contraseña2)
        fechaNacimiento = findViewById(R.id.FcehaNacimiento)
        button = findViewById(R.id.btn_registrar)

        // Configuración del DatePickerDialog
        val calendar = Calendar.getInstance()
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateFechaNacimiento(calendar)
        }

        // Mostrar DatePickerDialog al hacer clic en el campo de fecha de nacimiento
        fechaNacimiento.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                datePickerListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        button.setOnClickListener {
            val nombre = nombreUsuario.text.toString().trim()
            val email = correoElectronico.text.toString().trim()
            val password = contrasena.text.toString().trim()
            val password2 = contrasena2.text.toString().trim()
            val fecha = fechaNacimiento.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || fecha.isEmpty()) {
                MotionToast.createColorToast(
                    this,
                    "ADVERTENCIA",
                    "Por favor, completa todos los campos",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null)
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                MotionToast.createColorToast(
                    this,
                    "ADVERTENCIA",
                    "Por favor, ingresa un correo electrónico válido",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null)
                return@setOnClickListener
            }

            if (password != password2) {
                MotionToast.createColorToast(
                    this,
                    "ADVERTENCIA",
                    "Las contraseñas no coinciden",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    null)
                return@setOnClickListener
            }

            registerUser(nombre, email, password, fecha)
        }

        val inicio_login: TextView = findViewById(R.id.btn_iniciar_sesion)

        inicio_login.setOnClickListener {
            val intent = Intent(this, ScreenLogin::class.java)
            startActivity(intent)
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ScreenRegister, ScreenLogin::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun updateFechaNacimiento(calendar: Calendar) {
        val dateFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        fechaNacimiento.setText(sdf.format(calendar.time))
    }

    private fun registerUser(nombre: String, email: String, password: String, fecha: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    val userMap = hashMapOf(
                        "nombre" to nombre,
                        "email" to email,
                        "fechaNacimiento" to fecha
                    )

                    if (userId != null) {
                        db.collection("usuarios")
                            .document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                MotionToast.createColorToast(
                                    this,
                                    "REGISTRO EXITOSO",
                                    "Registro exitoso",
                                    MotionToastStyle.SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    null)
                                val intent = Intent(this, ScreenLogin::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                MotionToast.createColorToast(
                                    this,
                                    "ERROR",
                                    "Error al registrar el usuario: ${e.message}",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    null)
                            }
                    }
                } else {
                    MotionToast.createColorToast(
                        this,
                        "ERROR",
                        "Error al registrar el usuario: ${task.exception?.message}",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        null)
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
