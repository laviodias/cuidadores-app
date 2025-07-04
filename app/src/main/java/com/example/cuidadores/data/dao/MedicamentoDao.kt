package com.example.cuidadores.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cuidadores.data.model.Medicamento

@Dao
interface MedicamentoDao {
    @Query("SELECT * FROM medicamentos WHERE clienteId = :clienteId ORDER BY nome ASC")
    fun getMedicamentosByClienteId(clienteId: Long): LiveData<List<Medicamento>>

    @Query("SELECT * FROM medicamentos ORDER BY nome ASC")
    fun getAllMedicamentos(): LiveData<List<Medicamento>>

    @Insert
    suspend fun insert(medicamento: Medicamento): Long

    @Update
    suspend fun update(medicamento: Medicamento)

    @Delete
    suspend fun delete(medicamento: Medicamento)

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun getMedicamentoById(id: Long): Medicamento?

    @Query("DELETE FROM medicamentos WHERE clienteId = :clienteId")
    suspend fun deleteMedicamentosByClienteId(clienteId: Long)
} 