package com.example.cuidadores.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cuidadores.data.model.AplicacaoReceita

@Dao
interface AplicacaoReceitaDao {
    
    @Query("SELECT * FROM aplicacoes_receita ORDER BY horario ASC")
    fun getAllAplicacoes(): LiveData<List<AplicacaoReceita>>
    
    @Query("SELECT * FROM aplicacoes_receita WHERE medicamento_id = :medicamentoId ORDER BY horario ASC")
    fun getAplicacoesReceitaByMedicamento(medicamentoId: Long): LiveData<List<AplicacaoReceita>>
    
    @Insert
    suspend fun insert(aplicacao: AplicacaoReceita): Long
    
    @Insert
    suspend fun insertAll(aplicacoes: List<AplicacaoReceita>): List<Long>
    
    @Update
    suspend fun update(aplicacao: AplicacaoReceita)
    
    @Delete
    suspend fun delete(aplicacao: AplicacaoReceita)
} 