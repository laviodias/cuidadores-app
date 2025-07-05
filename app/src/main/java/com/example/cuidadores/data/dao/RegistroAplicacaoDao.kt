package com.example.cuidadores.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cuidadores.data.model.RegistroAplicacao

@Dao
interface RegistroAplicacaoDao {
    
    @Query("SELECT * FROM registros_aplicacao ORDER BY data_aplicacao DESC, horario_concluido DESC")
    fun getAllRegistros(): LiveData<List<RegistroAplicacao>>
    
    @Query("SELECT * FROM registros_aplicacao WHERE aplicacao_receita_id = :aplicacaoId ORDER BY data_aplicacao DESC")
    fun getRegistrosByAplicacao(aplicacaoId: Long): LiveData<List<RegistroAplicacao>>
    
    @Query("""
        SELECT ra.* FROM registros_aplicacao ra
        INNER JOIN aplicacoes_receita ar ON ra.aplicacao_receita_id = ar.id
        INNER JOIN medicamentos m ON ar.medicamento_id = m.id
        WHERE m.cliente_id = :clienteId
        AND ra.data_aplicacao = :data
        ORDER BY ar.horario ASC
    """)
    fun getRegistrosByClienteEData(clienteId: Long, data: String): LiveData<List<RegistroAplicacao>>

    @Query("""
    SELECT ra.* FROM registros_aplicacao ra
    INNER JOIN aplicacoes_receita ar ON ra.aplicacao_receita_id = ar.id
    INNER JOIN medicamentos m ON ar.medicamento_id = m.id
    WHERE m.cliente_id = :clienteId
    AND ra.data_aplicacao = :data
    AND ra.status = :status
    ORDER BY ar.horario ASC
    """)
    fun getRegistrosByClienteEDataEStatus(clienteId: Long, data: String, status: String): LiveData<List<RegistroAplicacao>>
    
    @Insert
    suspend fun insert(registro: RegistroAplicacao): Long
    
    @Insert
    suspend fun insertAll(registros: List<RegistroAplicacao>): List<Long>
    
    @Update
    suspend fun update(registro: RegistroAplicacao)
    
    @Delete
    suspend fun delete(registro: RegistroAplicacao)
    
    @Query("UPDATE registros_aplicacao SET status = :novoStatus, horario_concluido = :horarioConcluido WHERE id = :id")
    suspend fun updateStatus(id: Long, novoStatus: String, horarioConcluido: String?)
} 