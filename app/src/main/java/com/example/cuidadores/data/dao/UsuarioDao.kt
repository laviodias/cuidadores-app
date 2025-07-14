package com.example.cuidadores.data.dao

import androidx.room.*
import com.example.cuidadores.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    
    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha AND ativo = 1")
    suspend fun autenticar(email: String, senha: String): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun buscarPorEmail(email: String): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Long): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE ativo = 1")
    fun obterTodosUsuarios(): Flow<List<Usuario>>
    
    @Insert
    suspend fun inserir(usuario: Usuario): Long
    
    @Update
    suspend fun atualizar(usuario: Usuario)
    
    @Query("UPDATE usuarios SET ativo = 0 WHERE id = :id")
    suspend fun desativar(id: Long)
    
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
} 