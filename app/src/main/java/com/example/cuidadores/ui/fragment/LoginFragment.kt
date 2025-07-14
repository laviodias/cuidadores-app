package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.R
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.repository.AuthRepository
import com.example.cuidadores.databinding.FragmentLoginBinding
import com.example.cuidadores.ui.viewmodel.AuthViewModel
import com.example.cuidadores.ui.viewmodel.AuthViewModelFactory
import com.example.cuidadores.util.SessionManager

class LoginFragment : Fragment() {
    
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authViewModel: AuthViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val authRepository = AuthRepository(database.usuarioDao())
        val sessionManager = SessionManager(requireContext())
        val factory = AuthViewModelFactory(authRepository, sessionManager)
        
        // Usar ActivityScope para compartilhar ViewModel com MainActivity
        authViewModel = ViewModelProvider(requireActivity(), factory)[AuthViewModel::class.java]
    }
    
    private fun setupObservers() {
        authViewModel.loginState.observe(viewLifecycleOwner) { state ->
            android.util.Log.d("LoginFragment", "Login state changed: $state")
            when (state) {
                is AuthViewModel.LoginState.Loading -> {
                    showLoading(true)
                }
                is AuthViewModel.LoginState.Success -> {
                    showLoading(false)
                    android.util.Log.d("LoginFragment", "Login successful for user: ${state.usuario.email}")
                    Toast.makeText(requireContext(), "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Navegação será feita automaticamente pelo MainActivity baseada no estado de autenticação
                }
                is AuthViewModel.LoginState.Error -> {
                    showLoading(false)
                    android.util.Log.e("LoginFragment", "Login error: ${state.message}")
                    showError(state.message)
                }
                is AuthViewModel.LoginState.Idle -> {
                    showLoading(false)
                }
            }
        }
        
        // Remover observer de isLoggedIn pois o MainActivity já gerencia a navegação
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()
            
            clearErrors()
            authViewModel.login(email, senha)
        }
        
        binding.tvCadastro.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "" else "Entrar"
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
    
    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilSenha.error = null
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 