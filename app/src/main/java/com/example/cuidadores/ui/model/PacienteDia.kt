package com.example.cuidadores.ui.model

import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.data.model.AplicacaoReceita
import com.example.cuidadores.data.model.RegistroAplicacao

/**
 * Modelo para representar um paciente com medicações para o dia atual
 * Usado especificamente na tela HomeSeuDiaFragment para controlar estado de expansão
 */
data class PacienteDia(
    val cliente: Cliente,
    val medicamentosHoje: List<MedicamentoAplicacao>,
    val isExpanded: Boolean = false
) {
    /**
     * Retorna a primeira letra do nome do cliente em maiúsculo
     * Usado para exibir no círculo inicial
     */
    val inicialNome: String 
        get() = cliente.nome.firstOrNull()?.toString()?.uppercase() ?: "?"
    
    /**
     * Verifica se o paciente tem medicamentos para hoje
     */
    val temMedicamentos: Boolean 
        get() = medicamentosHoje.isNotEmpty()
    
    /**
     * Retorna texto descritivo da quantidade de aplicações
     */
    val descricaoMedicamentos: String
        get() = when (medicamentosHoje.size) {
            0 -> "Nenhuma medicação para hoje"
            1 -> "1 aplicação para hoje"
            else -> "${medicamentosHoje.size} aplicações para hoje"
        }
}

/**
 * Modelo simplificado que representa um medicamento com UMA aplicação específica
 * Cada horário do medicamento vira um card separado
 */
data class MedicamentoAplicacao(
    val medicamento: Medicamento,
    val aplicacaoComStatus: AplicacaoComStatus
) {
    /**
     * Retorna texto formatado da dose com horário específico
     */
    val doseEHorario: String
        get() = "${medicamento.dosagem} às ${aplicacaoComStatus.horarioFormatado}"
    
    /**
     * Retorna as instruções da aplicação
     */
    val instrucoes: String
        get() = aplicacaoComStatus.observacoes ?: ""
}

/**
 * Representa uma aplicação da receita com seu status do dia
 */
data class AplicacaoComStatus(
    val aplicacaoReceita: AplicacaoReceita,
    val registro: RegistroAplicacao?,
    val dataReferencia: String // Data de referência para consulta (yyyy-MM-dd)
) {
    companion object {
        // Status específico para UI - aplicações de hoje sem registro
        const val STATUS_PENDENTE_UI = "PENDENTE"
        // Status específico para UI - aplicações futuras sem registro
        const val STATUS_FUTURO_UI = "FUTURO"
    }
    
    /**
     * Status da aplicação baseado na data de referência e registro
     * - Se tem registro: usa o status do registro 
     * - Se não tem registro E é hoje: PENDENTE (clicável)
     * - Se não tem registro E é dia passado: PERDIDO (clicável)
     * - Se não tem registro E é dia futuro: FUTURO (não clicável)
     */
    val status: String
        get() = if (registro != null) {
            registro.status
        } else {
            val hoje = java.time.LocalDate.now()
            val dataRef = java.time.LocalDate.parse(dataReferencia)
            
            when {
                dataRef.isEqual(hoje) -> STATUS_PENDENTE_UI // Hoje sem registro = PENDENTE
                dataRef.isBefore(hoje) -> RegistroAplicacao.STATUS_PERDIDO // Passado sem registro = PERDIDO
                else -> STATUS_FUTURO_UI // Futuro sem registro = FUTURO
            }
        }
    
    /**
     * Horário formatado para exibição
     */
    val horarioFormatado: String
        get() = aplicacaoReceita.horario
    
    /**
     * Observações da aplicação
     */
    val observacoes: String?
        get() = aplicacaoReceita.observacoes
    
    /**
     * Verifica se a aplicação está concluída
     */
    val isConcluida: Boolean
        get() = status == RegistroAplicacao.STATUS_CONCLUIDO
    
    /**
     * Verifica se a aplicação está atrasada
     */
    val isAtrasada: Boolean
        get() = status == RegistroAplicacao.STATUS_ATRASADO

    /**
     * Verifica se a aplicação foi perdida
     */
    val isPerdida: Boolean
        get() = status == RegistroAplicacao.STATUS_PERDIDO
    
    /**
     * Verifica se a aplicação está pendente (sem registro no dia atual)
     */
    val isPendente: Boolean
        get() = status == STATUS_PENDENTE_UI
    
    /**
     * Verifica se a aplicação é futura (sem registro em data futura)
     */
    val isFutura: Boolean
        get() = status == STATUS_FUTURO_UI
} 