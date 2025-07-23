package com.example.cuidadores.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.data.repository.MedicamentoRepository
import kotlinx.coroutines.launch

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MedicamentoRepository
    val allMedicamentos: LiveData<List<Medicamento>>

    init {
        val medicamentoDao = AppDatabase.getDatabase(application).medicamentoDao()
        repository = MedicamentoRepository(medicamentoDao)
        allMedicamentos = repository.getAllMedicamentos()
    }

    fun getMedicamentosByClienteId(clienteId: Long): LiveData<List<Medicamento>> {
        return repository.getMedicamentosByClienteId(clienteId)
    }

    fun insert(medicamento: Medicamento) = viewModelScope.launch {
        repository.insert(medicamento)
    }

    fun update(medicamento: Medicamento) = viewModelScope.launch {
        repository.update(medicamento)
    }

    fun delete(medicamento: Medicamento) = viewModelScope.launch {
        repository.delete(medicamento)
    }

    fun deleteMedicamentosByClienteId(clienteId: Long) = viewModelScope.launch {
        repository.deleteMedicamentosByClienteId(clienteId)
    }

    suspend fun insertAndReturnId(medicamento: Medicamento): Long {
        return repository.insert(medicamento)
    }
} 