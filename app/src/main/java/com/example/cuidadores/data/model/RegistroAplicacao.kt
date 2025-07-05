package com.example.cuidadores.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "registros_aplicacao",
    foreignKeys = [
        ForeignKey(
            entity = AplicacaoReceita::class,
            parentColumns = ["id"],
            childColumns = ["aplicacao_receita_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RegistroAplicacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "aplicacao_receita_id")
    val aplicacaoReceitaId: Long,
    
    @ColumnInfo(name = "data_aplicacao")
    val dataAplicacao: String, // Ex: "2024-01-15" (ISO date)
    
    val status: String, // "CONCLUIDO", "ATRASADO", "PERDIDO"
    
    @ColumnInfo(name = "horario_concluido")
    val horarioConcluido: String? // Ex: "2024-01-15T08:05:00" (ISO datetime, nullable)
) {
    companion object {
        const val STATUS_CONCLUIDO = "CONCLUIDO"
        const val STATUS_PERDIDO = "PERDIDO"
        const val STATUS_ATRASADO = "ATRASADO"
    }
} 