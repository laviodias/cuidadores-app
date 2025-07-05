package com.example.cuidadores.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cuidadores.data.model.Medicamento

@Dao
interface MedicamentoDao {
    
    @Query("SELECT * FROM medicamentos ORDER BY data_inicio DESC")
    fun getAllMedicamentos(): LiveData<List<Medicamento>>
    
    @Query("SELECT * FROM medicamentos WHERE cliente_id = :clienteId ORDER BY data_inicio DESC")
    fun getMedicamentosByCliente(clienteId: Long): LiveData<List<Medicamento>>
    
    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun getMedicamentoById(id: Long): Medicamento?
    
    @Query("""
        SELECT * FROM medicamentos 
        WHERE cliente_id = :clienteId 
        AND (data_fim IS NULL OR data_fim >= :dataAtual)
        ORDER BY data_inicio DESC
    """)
    fun getMedicamentosAtivos(clienteId: Long, dataAtual: String): LiveData<List<Medicamento>>
    
    @Insert
    suspend fun insert(medicamento: Medicamento): Long
    
    @Update
    suspend fun update(medicamento: Medicamento)
    
    @Delete
    suspend fun delete(medicamento: Medicamento)
} 