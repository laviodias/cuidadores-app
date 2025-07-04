package com.example.cuidadores.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.data.repository.ClienteRepository
import kotlinx.coroutines.launch

class ClienteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ClienteRepository
    val allClientes: LiveData<List<Cliente>>

    init {
        val clienteDao = AppDatabase.getDatabase(application).clienteDao()
        repository = ClienteRepository(clienteDao)
        allClientes = repository.allClientes
    }

    fun insert(cliente: Cliente) = viewModelScope.launch {
        repository.insert(cliente)
    }

    fun update(cliente: Cliente) = viewModelScope.launch {
        repository.update(cliente)
    }

    fun delete(cliente: Cliente) = viewModelScope.launch {
        repository.delete(cliente)
    }

    suspend fun getClienteById(id: Long): Cliente? {
        return repository.getClienteById(id)
    }
} 