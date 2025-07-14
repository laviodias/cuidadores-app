package com.example.cuidadores

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.repository.AuthRepository
import com.example.cuidadores.databinding.ActivityMainBinding
import com.example.cuidadores.ui.viewmodel.AuthViewModel
import com.example.cuidadores.ui.viewmodel.AuthViewModelFactory
import com.example.cuidadores.util.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.NavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isUpdatingBottomNav = false
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupAuthViewModel()
        setupNavigation()
        observeAuthState()
    }
    
    private fun setupAuthViewModel() {
        val database = AppDatabase.getDatabase(this)
        val authRepository = AuthRepository(database.usuarioDao())
        val sessionManager = SessionManager(this)
        val factory = AuthViewModelFactory(authRepository, sessionManager)
        
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, // ID do item "Início" no menu
                R.id.navigation_patients, // ID do item "Pacientes" no menu
                R.id.loginFragment, // Incluir login para não mostrar botão de voltar
                R.id.registerFragment, // Incluir cadastro para não mostrar botão de voltar
                R.id.perfilFragment // Incluir perfil para não mostrar botão de voltar
            )
        )

        setupActionBarWithNavController(navController, configuration = appBarConfiguration)

        // Encontrar a BottomNavigationView
        val bottomNavView: BottomNavigationView = binding.bottomNavBar

        // Conectar a BottomNavigationView com o NavController
        bottomNavView.setupWithNavController(navController)
        
        // Adicionar listener para tratar casos especiais de navegação
        setupCustomNavigationListener(navController, bottomNavView)
    }
    
    private fun observeAuthState() {
        authViewModel.isLoggedIn.observe(this) { isLoggedIn ->
            android.util.Log.d("MainActivity", "Auth state changed: isLoggedIn = $isLoggedIn")
            
            updateUIBasedOnAuth(isLoggedIn)
            
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val currentDestination = navController.currentDestination?.id
            
            android.util.Log.d("MainActivity", "Current destination: $currentDestination")
            
            if (isLoggedIn) {
                // Se estiver logado e na tela de login/cadastro, navegar para home
                if (currentDestination == R.id.loginFragment || currentDestination == R.id.registerFragment) {
                    android.util.Log.d("MainActivity", "Navigating to home screen")
                    try {
                        navController.navigate(R.id.action_global_to_home)
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Navigation error: ${e.message}")
                        // Fallback: tentar navegar diretamente
                        try {
                            navController.navigate(R.id.navigation_home)
                        } catch (e2: Exception) {
                            android.util.Log.e("MainActivity", "Fallback navigation error: ${e2.message}")
                        }
                    }
                }
            } else {
                // Se não estiver logado e não estiver nas telas de auth, navegar para login
                if (currentDestination != R.id.loginFragment && currentDestination != R.id.registerFragment) {
                    android.util.Log.d("MainActivity", "Navigating to login screen")
                    try {
                        navController.navigate(R.id.action_global_to_login)
                    } catch (e: Exception) {
                        android.util.Log.e("MainActivity", "Navigation to login error: ${e.message}")
                        // Fallback: tentar navegar diretamente
                        try {
                            navController.navigate(R.id.loginFragment)
                        } catch (e2: Exception) {
                            android.util.Log.e("MainActivity", "Fallback navigation to login error: ${e2.message}")
                        }
                    }
                }
            }
        }
    }
    
    private fun updateUIBasedOnAuth(isLoggedIn: Boolean) {
        // Controlar visibilidade da bottom navigation
        binding.bottomNavBar.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        
        // Invalidar menu para mostrar/esconder logout
        invalidateOptionsMenu()
    }
    
    private fun setupCustomNavigationListener(navController: NavController, bottomNavView: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Evitar loop infinito
            if (isUpdatingBottomNav) return@addOnDestinationChangedListener
            
            // Controlar visibilidade da bottom navigation baseado na tela atual
            val showBottomNav = when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> false
                R.id.perfilFragment -> true // Mostrar bottom nav na tela de perfil
                else -> authViewModel.isLoggedIn.value ?: false
            }
            binding.bottomNavBar.visibility = if (showBottomNav) View.VISIBLE else View.GONE
            
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
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Mostrar menu apenas se estiver logado
        if (authViewModel.isLoggedIn.value == true) {
            menuInflater.inflate(R.menu.main, menu)
            return true
        }
        return false
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_perfil -> {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.perfilFragment)
                true
            }
            R.id.action_logout -> {
                authViewModel.logout()
                // A navegação será feita automaticamente pelo observer em AuthViewModel
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}