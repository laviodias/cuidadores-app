package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.R
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.repository.AuthRepository
import com.example.cuidadores.databinding.FragmentRegisterBinding
import com.example.cuidadores.ui.viewmodel.AuthViewModel
import com.example.cuidadores.ui.viewmodel.AuthViewModelFactory
import com.example.cuidadores.util.SessionManager

class RegisterFragment : Fragment() {
    
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authViewModel: AuthViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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
        authViewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.RegisterState.Loading -> {
                    showLoading(true)
                }
                is AuthViewModel.RegisterState.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Navegação será feita automaticamente pelo MainActivity baseada no estado de autenticação
                }
                is AuthViewModel.RegisterState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                is AuthViewModel.RegisterState.Idle -> {
                    showLoading(false)
                }
            }
        }
        
        // Remover observer de isLoggedIn pois o MainActivity já gerencia a navegação
    }
    
    private fun setupClickListeners() {
        binding.btnCadastrar.setOnClickListener {
            val nome = binding.etNome.text.toString()
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()
            val confirmarSenha = binding.etConfirmarSenha.text.toString()
            
            clearErrors()
            authViewModel.cadastrar(nome, email, senha, confirmarSenha)
        }
        
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnCadastrar.isEnabled = !isLoading
        binding.btnCadastrar.text = if (isLoading) "" else "Cadastrar"
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        
        // Mostrar erros específicos nos campos
        when {
            message.contains("Email", ignoreCase = true) -> {
                binding.tilEmail.error = message
            }
            message.contains("senha", ignoreCase = true) -> {
                binding.tilSenha.error = message
                binding.tilConfirmarSenha.error = message
            }
            message.contains("nome", ignoreCase = true) -> {
                binding.tilNome.error = message
            }
        }
    }
    
    private fun clearErrors() {
        binding.tilNome.error = null
        binding.tilEmail.error = null
        binding.tilSenha.error = null
        binding.tilConfirmarSenha.error = null
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 