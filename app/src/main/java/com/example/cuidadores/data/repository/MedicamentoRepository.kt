package com.example.cuidadores.data.repository

import androidx.lifecycle.LiveData
import com.example.cuidadores.data.dao.MedicamentoDao
import com.example.cuidadores.data.model.Medicamento

class MedicamentoRepository(private val medicamentoDao: MedicamentoDao) {
    
    fun getAllMedicamentos(): LiveData<List<Medicamento>> {
        return medicamentoDao.getAllMedicamentos()
    }
    
    fun getMedicamentosByCliente(clienteId: Long): LiveData<List<Medicamento>> {
        return medicamentoDao.getMedicamentosByCliente(clienteId)
    }
    
    suspend fun getMedicamentoById(id: Long): Medicamento? {
        return medicamentoDao.getMedicamentoById(id)
    }
    
    fun getMedicamentosAtivos(clienteId: Long, dataAtual: String): LiveData<List<Medicamento>> {
        return medicamentoDao.getMedicamentosAtivos(clienteId, dataAtual)
    }
    
    suspend fun insert(medicamento: Medicamento): Long {
        return medicamentoDao.insert(medicamento)
    }
    
    suspend fun update(medicamento: Medicamento) {
        medicamentoDao.update(medicamento)
    }
    
    suspend fun delete(medicamento: Medicamento) {
        medicamentoDao.delete(medicamento)
    }

    fun getMedicamentosByClienteId(clienteId: Long): LiveData<List<Medicamento>> {
        return medicamentoDao.getMedicamentosByClienteId(clienteId)
    }

    suspend fun deleteMedicamentosByClienteId(clienteId: Long) {
        medicamentoDao.deleteMedicamentosByClienteId(clienteId)
    }
} 