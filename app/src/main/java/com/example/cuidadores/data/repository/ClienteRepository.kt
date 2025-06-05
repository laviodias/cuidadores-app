package com.example.cuidadores.data.repository

import androidx.lifecycle.LiveData
import com.example.cuidadores.data.dao.ClienteDao
import com.example.cuidadores.data.model.Cliente

class ClienteRepository(private val clienteDao: ClienteDao) {
    val allClientes: LiveData<List<Cliente>> = clienteDao.getAllClientes()

    suspend fun insert(cliente: Cliente): Long {
        return clienteDao.insert(cliente)
    }

    suspend fun update(cliente: Cliente) {
        clienteDao.update(cliente)
    }

    suspend fun delete(cliente: Cliente) {
        clienteDao.delete(cliente)
    }

    suspend fun getClienteById(id: Long): Cliente? {
        return clienteDao.getClienteById(id)
    }
} 