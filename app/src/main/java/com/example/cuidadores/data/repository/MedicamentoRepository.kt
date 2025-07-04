package com.example.cuidadores.data.repository

import androidx.lifecycle.LiveData
import com.example.cuidadores.data.dao.MedicamentoDao
import com.example.cuidadores.data.model.Medicamento

class MedicamentoRepository(private val medicamentoDao: MedicamentoDao) {

    fun getMedicamentosByClienteId(clienteId: Long): LiveData<List<Medicamento>> {
        return medicamentoDao.getMedicamentosByClienteId(clienteId)
    }

    fun getAllMedicamentos(): LiveData<List<Medicamento>> {
        return medicamentoDao.getAllMedicamentos()
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

    suspend fun getMedicamentoById(id: Long): Medicamento? {
        return medicamentoDao.getMedicamentoById(id)
    }

    suspend fun deleteMedicamentosByClienteId(clienteId: Long) {
        medicamentoDao.deleteMedicamentosByClienteId(clienteId)
    }
} 