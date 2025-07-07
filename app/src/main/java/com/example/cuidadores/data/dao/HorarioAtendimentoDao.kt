package com.example.cuidadores.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cuidadores.data.model.HorarioAtendimento

@Dao
interface HorarioAtendimentoDao {
    
    @Insert
    suspend fun insert(horarioAtendimento: HorarioAtendimento): Long
    
    @Insert
    suspend fun insertAll(horariosAtendimento: List<HorarioAtendimento>)
    
    @Update
    suspend fun update(horarioAtendimento: HorarioAtendimento)
    
    @Delete
    suspend fun delete(horarioAtendimento: HorarioAtendimento)
    
    @Query("SELECT * FROM horarios_atendimento WHERE cliente_id = :clienteId ORDER BY dia_semana, horario_inicio")
    fun getHorariosByCliente(clienteId: Long): LiveData<List<HorarioAtendimento>>
    
    @Query("SELECT * FROM horarios_atendimento WHERE cliente_id = :clienteId")
    suspend fun getHorariosByClienteSync(clienteId: Long): List<HorarioAtendimento>
    
    @Query("DELETE FROM horarios_atendimento WHERE cliente_id = :clienteId")
    suspend fun deleteHorariosByCliente(clienteId: Long)
} 