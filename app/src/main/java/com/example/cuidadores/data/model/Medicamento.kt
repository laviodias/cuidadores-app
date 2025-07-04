package com.example.cuidadores.data.model

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
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clienteId")]
)
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clienteId: Long,
    val nome: String,
    val dosagem: String,
    val horario: String,
    val observacoes: String = ""
) 