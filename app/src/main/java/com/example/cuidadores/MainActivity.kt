package com.example.cuidadores

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cuidadores.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isUpdatingBottomNav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, // ID do item "Início" no menu
                R.id.navigation_patients, // ID do item "Pacientes" no menu
            )
        )

        setupActionBarWithNavController(navController, configuration = appBarConfiguration)

        // 5. Encontrar a BottomNavigationView
        val bottomNavView: BottomNavigationView = binding.bottomNavBar // ID da sua BottomNavigationView

        // 6. Conectar a BottomNavigationView com o NavController
        bottomNavView.setupWithNavController(navController)
        
        // 7. Adicionar listener para tratar casos especiais de navegação
        setupCustomNavigationListener(navController, bottomNavView)
    }
    
    private fun setupCustomNavigationListener(navController: NavController, bottomNavView: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Evitar loop infinito
            if (isUpdatingBottomNav) return@addOnDestinationChangedListener
            
            when (destination.id) {
                R.id.clienteFragment -> {
                    // ClienteFragment é considerado parte da hierarquia Home
                    if (bottomNavView.selectedItemId != R.id.navigation_home) {
                        isUpdatingBottomNav = true
                        bottomNavView.menu.findItem(R.id.navigation_home).isChecked = true
                        isUpdatingBottomNav = false
                    }
                }
                // Outros casos especiais podem ser adicionados aqui
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}