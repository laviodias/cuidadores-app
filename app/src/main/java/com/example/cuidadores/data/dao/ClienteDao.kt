package com.example.cuidadores.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cuidadores.data.model.Cliente

@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes ORDER BY nome ASC")
    fun getAllClientes(): LiveData<List<Cliente>>

    @Insert
    suspend fun insert(cliente: Cliente): Long

    @Update
    suspend fun update(cliente: Cliente)

    @Delete
    suspend fun delete(cliente: Cliente)

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getClienteById(id: Long): Cliente?
} 