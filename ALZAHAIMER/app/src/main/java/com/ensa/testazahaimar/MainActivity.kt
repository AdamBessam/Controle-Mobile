package com.ensa.testazahaimar

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ensa.testazahaimar.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Trouver le NavController pour le NavHostFragment
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Configurer le BottomNavigationView avec le NavController
        navView.setupWithNavController(navController)

        // Enlever le setupActionBarWithNavController
        // Si vous souhaitez gérer la navigation arrière, faites-le manuellement si nécessaire
    }
}
