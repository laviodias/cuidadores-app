package com.example.cuidadores.data.repository

import com.example.cuidadores.data.dao.UsuarioDao
import com.example.cuidadores.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val usuarioDao: UsuarioDao) {
    
    suspend fun login(email: String, senha: String): Usuario? {
        return withContext(Dispatchers.IO) {
            usuarioDao.autenticar(email, senha)
        }
    }
    
    suspend fun cadastrar(nome: String, email: String, senha: String): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                // Verificar se o email já existe
                val usuarioExistente = usuarioDao.buscarPorEmail(email)
                if (usuarioExistente != null) {
                    return@withContext Result.failure(Exception("Email já cadastrado"))
                }
                
                // Criar novo usuário
                val novoUsuario = Usuario(
                    email = email,
                    senha = senha, // Em produção, use hash da senha
                    nome = nome
                )
                
                val usuarioId = usuarioDao.inserir(novoUsuario)
                val usuarioInserido = usuarioDao.buscarPorId(usuarioId)
                
                if (usuarioInserido != null) {
                    Result.success(usuarioInserido)
                } else {
                    Result.failure(Exception("Erro ao criar usuário"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun atualizarPerfil(usuario: Usuario, senhaAtual: String?, novaSenha: String?): Result<Usuario> {
        return withContext(Dispatchers.IO) {
            try {
                // Verificar se o email já existe para outro usuário
                val usuarioComEmail = usuarioDao.buscarPorEmail(usuario.email)
                if (usuarioComEmail != null && usuarioComEmail.id != usuario.id) {
                    return@withContext Result.failure(Exception("Email já está sendo usado por outro usuário"))
                }
                
                var usuarioAtualizado = usuario
                
                // Se está alterando senha, verificar a senha atual
                if (!novaSenha.isNullOrBlank()) {
                    if (senhaAtual.isNullOrBlank()) {
                        return@withContext Result.failure(Exception("Senha atual é obrigatória para alterar a senha"))
                    }
                    
                    // Verificar se a senha atual está correta
                    val usuarioAutenticado = usuarioDao.autenticar(usuario.email, senhaAtual)
                    if (usuarioAutenticado == null) {
                        return@withContext Result.failure(Exception("Senha atual incorreta"))
                    }
                    
                    // Atualizar com nova senha
                    usuarioAtualizado = usuario.copy(senha = novaSenha)
                }
                
                usuarioDao.atualizar(usuarioAtualizado)
                
                // Buscar usuário atualizado
                val usuarioSalvo = usuarioDao.buscarPorId(usuarioAtualizado.id)
                if (usuarioSalvo != null) {
                    Result.success(usuarioSalvo)
                } else {
                    Result.failure(Exception("Erro ao atualizar perfil"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun verificarSenha(email: String, senha: String): Boolean {
        return withContext(Dispatchers.IO) {
            usuarioDao.autenticar(email, senha) != null
        }
    }
    
    suspend fun buscarUsuarioPorId(id: Long): Usuario? {
        return withContext(Dispatchers.IO) {
            usuarioDao.buscarPorId(id)
        }
    }
    
    suspend fun atualizarUsuario(usuario: Usuario) {
        withContext(Dispatchers.IO) {
            usuarioDao.atualizar(usuario)
        }
    }
    
    suspend fun desativarUsuario(id: Long) {
        withContext(Dispatchers.IO) {
            usuarioDao.desativar(id)
        }
    }
    
    suspend fun contarUsuarios(): Int {
        return withContext(Dispatchers.IO) {
            usuarioDao.contarUsuarios()
        }
    }
    
    suspend fun verificarSeExisteUsuario(): Boolean {
        return contarUsuarios() > 0
    }
} 