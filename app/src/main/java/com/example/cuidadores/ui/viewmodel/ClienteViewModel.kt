package com.example.cuidadores.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.cuidadores.data.AppDatabase
import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.data.model.AplicacaoReceita
import com.example.cuidadores.data.model.RegistroAplicacao
import com.example.cuidadores.ui.model.PacienteDia
import com.example.cuidadores.ui.model.AplicacaoComStatus
import com.example.cuidadores.ui.model.MedicamentoAplicacao
import com.example.cuidadores.data.repository.ClienteRepository
import com.example.cuidadores.data.repository.MedicamentoRepository
import com.example.cuidadores.data.repository.AplicacaoRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ClienteViewModel(application: Application) : AndroidViewModel(application) {
    private val clienteRepository: ClienteRepository
    private val medicamentoRepository: MedicamentoRepository
    private val aplicacaoRepository: AplicacaoRepository
    
    val allClientes: LiveData<List<Cliente>>

    init {
        val database = AppDatabase.getDatabase(application)
        clienteRepository = ClienteRepository(database.clienteDao())
        medicamentoRepository = MedicamentoRepository(database.medicamentoDao())
        aplicacaoRepository = AplicacaoRepository(
            database.aplicacaoReceitaDao(),
            database.registroAplicacaoDao()
        )
        
        allClientes = clienteRepository.allClientes
    }

    fun insert(cliente: Cliente) = viewModelScope.launch {
        clienteRepository.insert(cliente)
    }

    fun update(cliente: Cliente) = viewModelScope.launch {
        clienteRepository.update(cliente)
    }

    fun delete(cliente: Cliente) = viewModelScope.launch {
        clienteRepository.delete(cliente)
    }

    suspend fun getClienteById(id: Long): Cliente? {
        return clienteRepository.getClienteById(id)
    }

    /**
     * Retorna LiveData com pacientes que têm medicação programada para uma data específica
     * Medicamentos sem registro = PERDIDOS
     */
    fun getPacientesComMedicacaoPorData(data: String): LiveData<List<PacienteDia>> = MediatorLiveData<List<PacienteDia>>().apply {
        var clientes: List<Cliente>? = null
        var medicamentos: List<Medicamento>? = null
        var aplicacoes: List<AplicacaoReceita>? = null
        var registros: List<RegistroAplicacao>? = null

        fun update() {
            val clientesData = clientes
            val medicamentosData = medicamentos
            val aplicacoesData = aplicacoes
            val registrosData = registros
            
            if (clientesData != null && medicamentosData != null && 
                aplicacoesData != null && registrosData != null) {
                
                val pacientesComMedicacao = combinarDadosParaData(
                    clientesData, medicamentosData, aplicacoesData, registrosData, data
                )
                value = pacientesComMedicacao
            }
        }

        addSource(allClientes) { 
            clientes = it
            update()
        }
        
        addSource(medicamentoRepository.getAllMedicamentos()) { 
            medicamentos = it
            update()
        }
        
        addSource(aplicacaoRepository.getAllAplicacoes()) { 
            aplicacoes = it
            update()
        }
        
        addSource(aplicacaoRepository.getAllRegistros()) { 
            registros = it
            update()
        }
    }

    /**
     * Método auxiliar para buscar medicamentos de hoje
     */
    fun getPacientesComMedicacaoHoje(): LiveData<List<PacienteDia>> {
        val hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        return getPacientesComMedicacaoPorData(hoje)
    }

    /**
     * Combina dados de múltiplas tabelas para criar PacienteDia para uma data específica
     * Cada aplicação vira um card separado
     */
    private fun combinarDadosParaData(
        clientes: List<Cliente>,
        medicamentos: List<Medicamento>,
        aplicacoes: List<AplicacaoReceita>,
        registros: List<RegistroAplicacao>,
        data: String
    ): List<PacienteDia> {
        return clientes.mapNotNull { cliente ->
            val medicamentosDoCliente = medicamentos.filter { 
                it.clienteId == cliente.id &&
                // Verificar se medicamento está ativo nesta data
                (it.dataInicio <= data) && 
                (it.dataFim == null || it.dataFim >= data)
            }
            
            // Achatar dados - cada aplicação vira um item
            val medicamentosAchatados = medicamentosDoCliente.flatMap { medicamento ->
                criarMedicamentosAchatadosPorData(medicamento, aplicacoes, registros, data)
            } // Flatmap sobre aplicações do medicamento pois o retorno de criarMedicamentosAchatadosPorData é uma lista de MedicamentoAplicacao (tipo da UI criado em PacienteDia) 
            
            if (medicamentosAchatados.isNotEmpty()) {
                PacienteDia(
                    cliente = cliente,
                    medicamentosHoje = medicamentosAchatados,
                    isExpanded = false
                )
            } else null
        }
    }

    /**
     * Cria lista de MedicamentoAplicacao onde cada aplicação vira um item separado
     */
    private fun criarMedicamentosAchatadosPorData(
        medicamento: Medicamento,
        todasAplicacoes: List<AplicacaoReceita>,
        todosRegistros: List<RegistroAplicacao>,
        data: String
    ): List<MedicamentoAplicacao> {
        val aplicacoesDoMedicamento = todasAplicacoes.filter { it.medicamentoId == medicamento.id }
        
        // Transformar cada aplicação em um item separado
        return aplicacoesDoMedicamento.map { aplicacao ->
            val registro = todosRegistros.find { 
                it.aplicacaoReceitaId == aplicacao.id && it.dataAplicacao == data 
            }
            
            val aplicacaoComStatus = AplicacaoComStatus(
                aplicacaoReceita = aplicacao,
                registro = registro,
                dataReferencia = data
            )
            
            MedicamentoAplicacao(
                medicamento = medicamento,
                aplicacaoComStatus = aplicacaoComStatus
            )
        }
    }

    // === Métodos para gerenciar medicamentos ===
    
    fun insertMedicamento(medicamento: Medicamento) = viewModelScope.launch {
        medicamentoRepository.insert(medicamento)
    }
    
    fun insertAplicacaoReceita(aplicacao: AplicacaoReceita) = viewModelScope.launch {
        aplicacaoRepository.insertAplicacaoReceita(aplicacao)
    }
    
    fun marcarMedicamentoComoConcluido(registroId: Long) = viewModelScope.launch {
        aplicacaoRepository.marcarComoConcluido(registroId)
    }
    
    fun marcarMedicamentoComoAtrasado(registroId: Long) = viewModelScope.launch {
        aplicacaoRepository.marcarComoAtrasado(registroId)
    }
    
    /**
     * Marca medicamento como tomado, verificando se está atrasado (>4h)
     * Se não existe registro, cria um novo com horário atual
     * Se existe registro, apenas atualiza status (mantém horário null - correção histórica)
     */
    fun marcarMedicamentoComoTomado(aplicacaoComStatus: AplicacaoComStatus) = viewModelScope.launch {
        val agora = LocalDateTime.now()
        val hoje = agora.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val horarioAtual = agora.format(DateTimeFormatter.ofPattern("HH:mm"))
        
        // Verificar se está atrasado (>4h após horário programado)
        val horarioProgramado = aplicacaoComStatus.aplicacaoReceita.horario
        val isAtrasado = verificarSeEstaAtrasado(horarioProgramado, horarioAtual)
        
        val status = if (isAtrasado) {
            RegistroAplicacao.STATUS_ATRASADO
        } else {
            RegistroAplicacao.STATUS_CONCLUIDO
        }
        
        if (aplicacaoComStatus.registro != null) {
            // Atualizar registro existente - APENAS status, horário permanece null (correção histórica)
            aplicacaoRepository.atualizarStatus(aplicacaoComStatus.registro.id, status, null)
        } else {
            // Criar novo registro - com horário atual (usuário está marcando agora)
            aplicacaoRepository.insertRegistro(
                RegistroAplicacao(
                    aplicacaoReceitaId = aplicacaoComStatus.aplicacaoReceita.id,
                    dataAplicacao = hoje,
                    status = status,
                    horarioConcluido = agora.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            )
        }
    }
    
    /**
     * Verifica se medicamento está sendo tomado com atraso (>4h)
     */
    private fun verificarSeEstaAtrasado(horarioProgramado: String, horarioAtual: String): Boolean {
        try {
            val programado = LocalTime.parse(horarioProgramado, DateTimeFormatter.ofPattern("HH:mm"))
            val atual = LocalTime.parse(horarioAtual, DateTimeFormatter.ofPattern("HH:mm"))
            
            // Calcular diferença em horas
            val diferencaHoras = java.time.Duration.between(programado, atual).toHours()
            
            // Considerar atrasado se > 4 horas
            return diferencaHoras > 4
        } catch (e: Exception) {
            // Em caso de erro, considerar não atrasado
            return false
        }
    }
    
    fun getMedicamentosByCliente(clienteId: Long): LiveData<List<Medicamento>> {
        return medicamentoRepository.getMedicamentosByCliente(clienteId)
    }
    
    /**
     * Retorna medicamentos de um cliente específico para uma data específica
     * Usado na tela individual do cliente
     */
    fun getMedicamentosClientePorData(clienteId: Long, data: String): LiveData<List<MedicamentoAplicacao>> = 
        MediatorLiveData<List<MedicamentoAplicacao>>().apply {
            var medicamentos: List<Medicamento>? = null
            var aplicacoes: List<AplicacaoReceita>? = null
            var registros: List<RegistroAplicacao>? = null

            fun update() {
                val medicamentosData = medicamentos
                val aplicacoesData = aplicacoes
                val registrosData = registros
                
                if (medicamentosData != null && aplicacoesData != null && registrosData != null) {
                    val medicamentosDoCliente = medicamentosData.filter { 
                        it.clienteId == clienteId &&
                        // Verificar se medicamento está ativo nesta data
                        (it.dataInicio <= data) && 
                        (it.dataFim == null || it.dataFim >= data)
                    }
                    
                    // Achatar dados - cada aplicação vira um item
                    val medicamentosAchatados = medicamentosDoCliente.flatMap { medicamento ->
                        criarMedicamentosAchatadosPorData(medicamento, aplicacoesData, registrosData, data)
                    }
                    
                    value = medicamentosAchatados
                }
            }

            addSource(medicamentoRepository.getAllMedicamentos()) { 
                medicamentos = it
                update()
            }
            
            addSource(aplicacaoRepository.getAllAplicacoes()) { 
                aplicacoes = it
                update()
            }
            
            addSource(aplicacaoRepository.getAllRegistros()) { 
                registros = it
                update()
            }
        }
    
    /**
     * Método para criar dados de exemplo para teste
     * TODO: Remover quando tiver formulários de cadastro
     */
    fun criarDadosDeExemplo() = viewModelScope.launch {
        try {
            val hoje = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            
            // 1. Criar cliente de exemplo - Maria Silva
            // Resultado formatado: "Segunda a Quinta 08h às 17h30\nSexta 08h30 às 12h\nSábado 09h15 às 15h45"
            val clienteId = clienteRepository.insert(
                Cliente(
                    nome = "Maria Silva",
                    telefone = "(11) 98765-4321",
                    endereco = "Rua das Flores, 123",
                    horariosAtendimento = """[
                        {"diaSemana": "Segunda", "horarioInicio": "08:00", "horarioFim": "17:30"},
                        {"diaSemana": "Terça", "horarioInicio": "08:00", "horarioFim": "17:30"},
                        {"diaSemana": "Quarta", "horarioInicio": "08:00", "horarioFim": "17:30"},
                        {"diaSemana": "Quinta", "horarioInicio": "08:00", "horarioFim": "17:30"},
                        {"diaSemana": "Sexta", "horarioInicio": "08:30", "horarioFim": "12:00"},
                        {"diaSemana": "Sábado", "horarioInicio": "09:15", "horarioFim": "15:45"}
                    ]"""
                )
            )
            
            // 2. Criar segundo cliente de exemplo - João Santos
            // Resultado formatado: "Segunda e Quinta 07h30 às 11h30\nSegunda e Quinta 13h às 17h\nDomingo 08h às 12h"
            val clienteId2 = clienteRepository.insert(
                Cliente(
                    nome = "João Santos",
                    telefone = "(11) 98888-7777",
                    endereco = "Av. Paulista, 456",
                    horariosAtendimento = """[
                        {"diaSemana": "Segunda", "horarioInicio": "07:30", "horarioFim": "11:30"},
                        {"diaSemana": "Segunda", "horarioInicio": "13:00", "horarioFim": "17:00"},
                        {"diaSemana": "Quinta", "horarioInicio": "07:30", "horarioFim": "11:30"},
                        {"diaSemana": "Quinta", "horarioInicio": "13:00", "horarioFim": "17:00"},
                        {"diaSemana": "Domingo", "horarioInicio": "08:00", "horarioFim": "12:00"}
                    ]"""
                )
            )
            
            // 3. Criar medicamentos de exemplo para Maria Silva
            val medicamentoId1 = medicamentoRepository.insert(
                Medicamento(
                    clienteId = clienteId,
                    nome = "Paracetamol",
                    dosagem = "500mg",
                    dataInicio = hoje,
                    dataFim = null,
                    observacoesGerais = "Tomar com água",
                    horario = "08:00"
                )
            )
            
            val medicamentoId2 = medicamentoRepository.insert(
                Medicamento(
                    clienteId = clienteId,
                    nome = "Vitamina D",
                    dosagem = "1000 UI",
                    dataInicio = hoje,
                    dataFim = null,
                    observacoesGerais = "Tomar após o café da manhã",
                    horario = "12:00"
                )
            )
            
            // 4. Criar medicamento de exemplo para João Santos
            val medicamentoId3 = medicamentoRepository.insert(
                Medicamento(
                    clienteId = clienteId2,
                    nome = "Omeprazol",
                    dosagem = "20mg",
                    dataInicio = hoje,
                    dataFim = null,
                    observacoesGerais = "Tomar em jejum",
                    horario = "07:00"
                )
            )
            
            // 5. Criar aplicações de receita para os medicamentos
            val aplicacao1 = AplicacaoReceita(
                medicamentoId = medicamentoId1,
                horario = "08:00",
                observacoes = "Tomar com água"
            )
            
            val aplicacao2 = AplicacaoReceita(
                medicamentoId = medicamentoId2,
                horario = "12:00",
                observacoes = "Tomar após o café da manhã"
            )
            
            val aplicacao3 = AplicacaoReceita(
                medicamentoId = medicamentoId3,
                horario = "07:00",
                observacoes = "Tomar em jejum"
            )
            
            aplicacaoRepository.insertAplicacaoReceita(aplicacao1)
            aplicacaoRepository.insertAplicacaoReceita(aplicacao2)
            aplicacaoRepository.insertAplicacaoReceita(aplicacao3)
            
        } catch (e: Exception) {
            // Log erro se necessário
        }
    }
} 