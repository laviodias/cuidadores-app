package com.example.cuidadores.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "aplicacoes_receita",
    foreignKeys = [
        ForeignKey(
            entity = Medicamento::class,
            parentColumns = ["id"],
            childColumns = ["medicamento_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AplicacaoReceita(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "medicamento_id")
    val medicamentoId: Long,
    
    val horario: String, // Ex: "08:00" (formato HH:mm)
    
    val observacoes: String? // Ex: "Tomar com água, após o café"
) 