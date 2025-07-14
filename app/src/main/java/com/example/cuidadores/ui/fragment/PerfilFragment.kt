package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.repository.AuthRepository
import com.example.cuidadores.databinding.FragmentPerfilBinding
import com.example.cuidadores.ui.viewmodel.AuthViewModel
import com.example.cuidadores.ui.viewmodel.AuthViewModelFactory
import com.example.cuidadores.util.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class PerfilFragment : Fragment() {
    
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var authViewModel: AuthViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupObservers()
        setupClickListeners()
        loadUserData()
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
        authViewModel.updateProfileState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.UpdateProfileState.Loading -> {
                    showLoading(true)
                }
                is AuthViewModel.UpdateProfileState.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    clearPasswordFields()
                }
                is AuthViewModel.UpdateProfileState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                is AuthViewModel.UpdateProfileState.Idle -> {
                    showLoading(false)
                }
            }
        }
        
        authViewModel.currentUser.observe(viewLifecycleOwner) { usuario ->
            usuario?.let {
                loadUserDataIntoFields(it)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSalvar.setOnClickListener {
            salvarAlteracoes()
        }
        
        binding.btnCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun loadUserData() {
        // Os dados serão carregados automaticamente pelo observer do currentUser
    }
    
    private fun loadUserDataIntoFields(usuario: com.example.cuidadores.data.model.Usuario) {
        binding.etNome.setText(usuario.nome)
        binding.etEmail.setText(usuario.email)
        
        // Formatar e mostrar data de criação
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataFormatada = dateFormat.format(usuario.dataCriacao)
        binding.tvDataCriacao.text = "Conta criada em: $dataFormatada"
    }
    
    private fun salvarAlteracoes() {
        val nome = binding.etNome.text.toString()
        val email = binding.etEmail.text.toString()
        val senhaAtual = binding.etSenhaAtual.text.toString()
        val novaSenha = binding.etNovaSenha.text.toString()
        val confirmarNovaSenha = binding.etConfirmarNovaSenha.text.toString()
        
        clearErrors()
        authViewModel.atualizarPerfil(nome, email, senhaAtual, novaSenha, confirmarNovaSenha)
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSalvar.isEnabled = !isLoading
        binding.btnSalvar.text = if (isLoading) "" else "Salvar Alterações"
    }
    
    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        
        // Mostrar erros específicos nos campos
        when {
            message.contains("Email", ignoreCase = true) -> {
                binding.tilEmail.error = message
            }
            message.contains("senha", ignoreCase = true) -> {
                if (message.contains("atual", ignoreCase = true)) {
                    binding.tilSenhaAtual.error = message
                } else {
                    binding.tilNovaSenha.error = message
                    binding.tilConfirmarNovaSenha.error = message
                }
            }
            message.contains("nome", ignoreCase = true) -> {
                binding.tilNome.error = message
            }
        }
    }
    
    private fun clearErrors() {
        binding.tilNome.error = null
        binding.tilEmail.error = null
        binding.tilSenhaAtual.error = null
        binding.tilNovaSenha.error = null
        binding.tilConfirmarNovaSenha.error = null
    }
    
    private fun clearPasswordFields() {
        binding.etSenhaAtual.setText("")
        binding.etNovaSenha.setText("")
        binding.etConfirmarNovaSenha.setText("")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 