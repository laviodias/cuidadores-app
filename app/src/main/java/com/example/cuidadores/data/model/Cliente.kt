package com.example.cuidadores.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String,
    val telefone: String,
    val endereco: String,
    val horariosAtendimento: String // JSON para horários de atendimento
)

// Classe auxiliar para horários de atendimento
data class HorarioAtendimento(
    val diaSemana: String,
    val horarioInicio: String,
    val horarioFim: String
) 