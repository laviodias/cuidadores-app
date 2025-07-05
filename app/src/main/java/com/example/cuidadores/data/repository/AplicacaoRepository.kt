package com.example.cuidadores.data.repository

import androidx.lifecycle.LiveData
import com.example.cuidadores.data.dao.AplicacaoReceitaDao
import com.example.cuidadores.data.dao.RegistroAplicacaoDao
import com.example.cuidadores.data.model.AplicacaoReceita
import com.example.cuidadores.data.model.RegistroAplicacao
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AplicacaoRepository(
    private val aplicacaoReceitaDao: AplicacaoReceitaDao,
    private val registroAplicacaoDao: RegistroAplicacaoDao
) {
    
    // Aplicações por dia de uso segundo a receita do medicamento
    
    fun getAllAplicacoes(): LiveData<List<AplicacaoReceita>> {
        return aplicacaoReceitaDao.getAllAplicacoes()
    }
    
    fun getAplicacoesReceitaByMedicamento(medicamentoId: Long): LiveData<List<AplicacaoReceita>> {
        return aplicacaoReceitaDao.getAplicacoesReceitaByMedicamento(medicamentoId)
    }
    
    suspend fun insertAplicacaoReceita(aplicacao: AplicacaoReceita): Long {
        return aplicacaoReceitaDao.insert(aplicacao)
    }
    
    suspend fun insertAllAplicacoesReceita(aplicacoes: List<AplicacaoReceita>): List<Long> {
        return aplicacaoReceitaDao.insertAll(aplicacoes)
    }
    
    suspend fun updateAplicacaoReceita(aplicacao: AplicacaoReceita) {
        aplicacaoReceitaDao.update(aplicacao)
    }
    
    suspend fun deleteAplicacaoReceita(aplicacao: AplicacaoReceita) {
        aplicacaoReceitaDao.delete(aplicacao)
    }
    
    // Registros de Aplicação
    
    fun getAllRegistros(): LiveData<List<RegistroAplicacao>> {
        return registroAplicacaoDao.getAllRegistros()
    }
    
    fun getRegistrosByAplicacao(aplicacaoId: Long): LiveData<List<RegistroAplicacao>> {
        return registroAplicacaoDao.getRegistrosByAplicacao(aplicacaoId)
    }
    
    fun getRegistrosByClienteEData(clienteId: Long, data: String): LiveData<List<RegistroAplicacao>> {
        return registroAplicacaoDao.getRegistrosByClienteEData(clienteId, data)
    }

    fun getRegistrosByClienteEDataEStatus(clienteId: Long, data: String, status: String): LiveData<List<RegistroAplicacao>> {
        return registroAplicacaoDao.getRegistrosByClienteEDataEStatus(clienteId, data, status)
    }
    
    suspend fun insertRegistro(registro: RegistroAplicacao): Long {
        return registroAplicacaoDao.insert(registro)
    }
    
    suspend fun insertAllRegistros(registros: List<RegistroAplicacao>): List<Long> {
        return registroAplicacaoDao.insertAll(registros)
    }
    
    suspend fun updateRegistro(registro: RegistroAplicacao) {
        registroAplicacaoDao.update(registro)
    }
    
    suspend fun deleteRegistro(registro: RegistroAplicacao) {
        registroAplicacaoDao.delete(registro)
    }
    
    suspend fun marcarComoConcluido(registroId: Long) {
        val agora = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        registroAplicacaoDao.updateStatus(registroId, RegistroAplicacao.STATUS_CONCLUIDO, agora)
    }
    
    suspend fun marcarComoAtrasado(registroId: Long) {
        registroAplicacaoDao.updateStatus(registroId, RegistroAplicacao.STATUS_ATRASADO, null)
    }

    suspend fun marcarComoPerdido(registroId: Long) {
        registroAplicacaoDao.updateStatus(registroId, RegistroAplicacao.STATUS_PERDIDO, null)
    }
    
    suspend fun atualizarStatus(registroId: Long, status: String, horarioConcluido: String?) {
        registroAplicacaoDao.updateStatus(registroId, status, horarioConcluido)
    }
} 