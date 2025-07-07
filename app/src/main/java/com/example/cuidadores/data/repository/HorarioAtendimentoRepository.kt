package com.example.cuidadores.data.repository

import androidx.lifecycle.LiveData
import com.example.cuidadores.data.dao.HorarioAtendimentoDao
import com.example.cuidadores.data.model.HorarioAtendimento

class HorarioAtendimentoRepository(private val horarioAtendimentoDao: HorarioAtendimentoDao) {
    
    suspend fun insert(horarioAtendimento: HorarioAtendimento): Long {
        return horarioAtendimentoDao.insert(horarioAtendimento)
    }
    
    suspend fun insertAll(horariosAtendimento: List<HorarioAtendimento>) {
        horarioAtendimentoDao.insertAll(horariosAtendimento)
    }
    
    suspend fun update(horarioAtendimento: HorarioAtendimento) {
        horarioAtendimentoDao.update(horarioAtendimento)
    }
    
    suspend fun delete(horarioAtendimento: HorarioAtendimento) {
        horarioAtendimentoDao.delete(horarioAtendimento)
    }
    
    fun getHorariosByCliente(clienteId: Long): LiveData<List<HorarioAtendimento>> {
        return horarioAtendimentoDao.getHorariosByCliente(clienteId)
    }
    
    suspend fun getHorariosByClienteSync(clienteId: Long): List<HorarioAtendimento> {
        return horarioAtendimentoDao.getHorariosByClienteSync(clienteId)
    }
    
    suspend fun deleteHorariosByCliente(clienteId: Long) {
        horarioAtendimentoDao.deleteHorariosByCliente(clienteId)
    }
} 