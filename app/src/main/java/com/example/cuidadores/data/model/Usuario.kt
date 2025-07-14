package com.example.cuidadores.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val senha: String, // Em produção, use hash da senha
    val nome: String,
    val dataCriacao: Date = Date(),
    val ativo: Boolean = true
) 