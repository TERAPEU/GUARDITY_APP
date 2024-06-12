package com.example.guadity_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ScreenPrincipal : AppCompatActivity() {

    private val firstFragment = FirstFragment()
    private val secondFragment = SecondFragment()
    private val thirdFragment = ThirdFragment()
    private val fourthFragment = FourthFragment()
    private val fifthFragment = FifthFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Considera actualizar esto a WindowCompat.setDecorFitsSystemWindows
        setContentView(R.layout.activity_screen_principal)

        // Ajusta los insets de la vista
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.frame_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.firstFragment -> {
                    loadFragment(firstFragment)
                    true
                }
                R.id.secondFragment -> {
                    loadFragment(secondFragment)
                    true
                }
                R.id.thirdFragment -> {
                    loadFragment(thirdFragment)
                    true
                }
                R.id.fourthFragment -> {
                    loadFragment(fourthFragment)
                    true
                }
                R.id.fifthFragment -> {
                    loadFragment(fifthFragment)
                    true
                }
                else -> false
            }
        }

        // Carga el fragmento predeterminado
        if (savedInstanceState == null) {
            loadFragment(firstFragment)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()
    }
}
