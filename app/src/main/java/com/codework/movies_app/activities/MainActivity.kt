package com.codework.movies_app.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.codework.movies_app.R
import com.codework.movies_app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val navController = findNavController(R.id.mainNavigationHost)
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
            when (destination.id) {
                R.id.homeFragment -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    bottomNavigationView?.visibility = View.VISIBLE
                }
                R.id.filmDetailFragment -> {
                    bottomNavigationView?.visibility = View.GONE
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                }

                else -> {
                    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    bottomNavigationView?.visibility = View.VISIBLE
                }
            }
        }
    }
}