package com.example.cuidadores.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicamentos",
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
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "cliente_id")
    val clienteId: Long,
    
    val nome: String, // Ex: "Paracetamol"
    
    val dosagem: String, // Ex: "500mg"
    
    @ColumnInfo(name = "data_inicio")
    val dataInicio: String, // Ex: "2024-01-15" (ISO date)
    
    @ColumnInfo(name = "data_fim")
    val dataFim: String?, // Ex: "2024-01-22" (nullable - tratamento contínuo)
    
    @ColumnInfo(name = "observacoes_gerais")
    val observacoesGerais: String?, // Ex: "Tomar sempre com água"
    
    val horario: String // Ex: "08:00"
) 