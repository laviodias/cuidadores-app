package com.example.cuidadores.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.cuidadores.data.repository.AuthRepository
import com.example.cuidadores.data.model.Usuario
import com.example.cuidadores.util.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {
    
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState
    
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState
    
    private val _updateProfileState = MutableLiveData<UpdateProfileState>()
    val updateProfileState: LiveData<UpdateProfileState> = _updateProfileState
    
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    private val _currentUser = MutableLiveData<Usuario?>()
    val currentUser: LiveData<Usuario?> = _currentUser
    
    init {
        verificarSessaoAtiva()
    }
    
    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _loginState.value = LoginState.Error("Email e senha são obrigatórios")
            return
        }
        
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            try {
                android.util.Log.d("AuthViewModel", "Attempting login for email: $email")
                val usuario = authRepository.login(email.trim(), senha)
                if (usuario != null) {
                    android.util.Log.d("AuthViewModel", "Login successful, creating session for user: ${usuario.email}")
                    sessionManager.criarSessao(usuario.id, usuario.email, usuario.nome)
                    _currentUser.value = usuario
                    _isLoggedIn.value = true
                    android.util.Log.d("AuthViewModel", "isLoggedIn set to true")
                    _loginState.value = LoginState.Success(usuario)
                } else {
                    android.util.Log.d("AuthViewModel", "Login failed: invalid credentials")
                    _loginState.value = LoginState.Error("Email ou senha incorretos")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Login exception: ${e.message}")
                _loginState.value = LoginState.Error("Erro ao fazer login: ${e.message}")
            }
        }
    }
    
    fun cadastrar(nome: String, email: String, senha: String, confirmarSenha: String) {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            _registerState.value = RegisterState.Error("Todos os campos são obrigatórios")
            return
        }
        
        if (senha != confirmarSenha) {
            _registerState.value = RegisterState.Error("Senhas não coincidem")
            return
        }
        
        if (senha.length < 6) {
            _registerState.value = RegisterState.Error("Senha deve ter pelo menos 6 caracteres")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerState.value = RegisterState.Error("Email inválido")
            return
        }
        
        _registerState.value = RegisterState.Loading
        
        viewModelScope.launch {
            val resultado = authRepository.cadastrar(nome.trim(), email.trim(), senha)
            resultado.fold(
                onSuccess = { usuario ->
                    sessionManager.criarSessao(usuario.id, usuario.email, usuario.nome)
                    _currentUser.value = usuario
                    _isLoggedIn.value = true
                    _registerState.value = RegisterState.Success(usuario)
                },
                onFailure = { exception ->
                    _registerState.value = RegisterState.Error(exception.message ?: "Erro ao cadastrar")
                }
            )
        }
    }
    
    fun atualizarPerfil(nome: String, email: String, senhaAtual: String, novaSenha: String, confirmarNovaSenha: String) {
        if (nome.isBlank() || email.isBlank()) {
            _updateProfileState.value = UpdateProfileState.Error("Nome e email são obrigatórios")
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _updateProfileState.value = UpdateProfileState.Error("Email inválido")
            return
        }
        
        // Validar senha se estiver tentando alterar
        if (novaSenha.isNotBlank() || confirmarNovaSenha.isNotBlank()) {
            if (senhaAtual.isBlank()) {
                _updateProfileState.value = UpdateProfileState.Error("Senha atual é obrigatória para alterar a senha")
                return
            }
            
            if (novaSenha.isBlank()) {
                _updateProfileState.value = UpdateProfileState.Error("Nova senha é obrigatória")
                return
            }
            
            if (novaSenha != confirmarNovaSenha) {
                _updateProfileState.value = UpdateProfileState.Error("Senhas não coincidem")
                return
            }
            
            if (novaSenha.length < 6) {
                _updateProfileState.value = UpdateProfileState.Error("Nova senha deve ter pelo menos 6 caracteres")
                return
            }
        }
        
        val currentUser = _currentUser.value
        if (currentUser == null) {
            _updateProfileState.value = UpdateProfileState.Error("Usuário não encontrado")
            return
        }
        
        _updateProfileState.value = UpdateProfileState.Loading
        
        viewModelScope.launch {
            val usuarioAtualizado = currentUser.copy(
                nome = nome.trim(),
                email = email.trim()
            )
            
            val novaSenhaFinal = if (novaSenha.isBlank()) null else novaSenha
            val senhaAtualFinal = if (senhaAtual.isBlank()) null else senhaAtual
            
            val resultado = authRepository.atualizarPerfil(usuarioAtualizado, senhaAtualFinal, novaSenhaFinal)
            resultado.fold(
                onSuccess = { usuario ->
                    // Atualizar sessão com novos dados
                    sessionManager.criarSessao(usuario.id, usuario.email, usuario.nome)
                    _currentUser.value = usuario
                    _updateProfileState.value = UpdateProfileState.Success(usuario)
                },
                onFailure = { exception ->
                    _updateProfileState.value = UpdateProfileState.Error(exception.message ?: "Erro ao atualizar perfil")
                }
            )
        }
    }
    
    fun logout() {
        sessionManager.logout()
        _currentUser.value = null
        _isLoggedIn.value = false
        _loginState.value = LoginState.Idle
        _registerState.value = RegisterState.Idle
    }
    
    private fun verificarSessaoAtiva() {
        val loggedIn = sessionManager.isLoggedIn()
        android.util.Log.d("AuthViewModel", "Checking active session: isLoggedIn = $loggedIn")
        _isLoggedIn.value = loggedIn
        
        if (loggedIn) {
            val userId = sessionManager.getUserId()
            android.util.Log.d("AuthViewModel", "Found active session for userId: $userId")
            if (userId != -1L) {
                viewModelScope.launch {
                    try {
                        val usuario = authRepository.buscarUsuarioPorId(userId)
                        android.util.Log.d("AuthViewModel", "Retrieved user from database: ${usuario?.email}")
                        _currentUser.value = usuario
                    } catch (e: Exception) {
                        android.util.Log.e("AuthViewModel", "Error retrieving user: ${e.message}")
                        // Se houver erro ao buscar usuário, fazer logout
                        logout()
                    }
                }
            } else {
                android.util.Log.d("AuthViewModel", "Invalid userId, logging out")
                logout()
            }
        }
    }
    
    fun verificarSeExisteUsuario(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val existeUsuario = authRepository.verificarSeExisteUsuario()
            callback(existeUsuario)
        }
    }
    
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val usuario: Usuario) : LoginState()
        data class Error(val message: String) : LoginState()
    }
    
    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        data class Success(val usuario: Usuario) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }

    sealed class UpdateProfileState {
        object Idle : UpdateProfileState()
        object Loading : UpdateProfileState()
        data class Success(val usuario: Usuario) : UpdateProfileState()
        data class Error(val message: String) : UpdateProfileState()
    }
}

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 