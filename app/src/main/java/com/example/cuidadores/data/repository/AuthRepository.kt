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