package com.example.cuidadores.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "horarios_atendimento",
    foreignKeys = [
        ForeignKey(
            entity = Cliente::class,
            parentColumns = ["id"],
            childColumns = ["cliente_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cliente_id")]
)
data class HorarioAtendimento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "cliente_id")
    val clienteId: Long,
    
    @ColumnInfo(name = "dia_semana")
    val diaSemana: String, // Ex: "Segunda", "Terça", etc.
    
    @ColumnInfo(name = "horario_inicio")
    val horarioInicio: String, // Ex: "08:00" (formato HH:mm)
    
    @ColumnInfo(name = "horario_fim")
    val horarioFim: String // Ex: "17:30" (formato HH:mm)
) 