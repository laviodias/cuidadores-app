package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuidadores.R
import com.example.cuidadores.databinding.FragmentClienteBinding
import com.example.cuidadores.databinding.ItemCalendarioSemanalBinding
import com.example.cuidadores.ui.viewmodel.ClienteViewModel
import com.example.cuidadores.ui.adapter.MedicamentoDiaAdapter
import com.example.cuidadores.ui.model.MedicamentoAplicacao
import com.example.cuidadores.data.model.HorarioAtendimento
import com.example.cuidadores.ui.view.TagView
import org.json.JSONArray
import org.json.JSONException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ClienteFragment : Fragment() {

    private var _binding: FragmentClienteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClienteViewModel by viewModels()
    private lateinit var medicamentosAdapter: MedicamentoDiaAdapter
    
    private var clienteId: Long = -1
    private var currentMedicamentosObserver: Observer<List<MedicamentoAplicacao>>? = null
    
    // Variáveis para controle do calendário
    private var currentStartDate: LocalDate = LocalDate.now()
    private var selectedDayIndex: Int = 0 // Índice visual (0-4), -1 = nenhum selecionado
    private var selectedDate: LocalDate = LocalDate.now() // Data absoluta selecionada
    private var todayDate: LocalDate = LocalDate.now()
    
    // Lista de views dos dias para fácil manipulação
    private val diasViews by lazy {
        listOf(binding.dia1.root, binding.dia2.root, binding.dia3.root, binding.dia4.root, binding.dia5.root)
    }
    
    // Lista de bindings dos dias para acesso aos TextViews
    private val diasBindings by lazy {
        listOf(binding.dia1, binding.dia2, binding.dia3, binding.dia4, binding.dia5)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            clienteId = it.getLong("clienteId", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendario()
        setupRecyclerViews()
        setupObservers()
        setupClickListeners()
    }

    private fun setupCalendario() {
        initializeCalendarDates()
        updateAllDaysDisplay()
        setupDayClickListeners()
        selectDay(selectedDayIndex)
    }
    
    /**
     * Inicializa as datas do calendário baseado no dia atual
     * Segunda a Sexta: Mostra semana útil com dia atual selecionado
     * Sábado/Domingo: Começa a partir do dia atual
     */
    private fun initializeCalendarDates() {
        val dayOfWeek = todayDate.dayOfWeek.value // 1=Segunda, 7=Domingo
        
        when (dayOfWeek) {
            1, 2, 3, 4, 5 -> { // Segunda a Sexta
                // Calcular a segunda-feira da semana atual
                val daysToSubtract = dayOfWeek - 1 // 0 para segunda, 1 para terça, etc.
                currentStartDate = todayDate.minusDays(daysToSubtract.toLong())
                selectedDayIndex = daysToSubtract // Posição na semana útil (0-4)
            }
            6, 7 -> { // Sábado ou Domingo
                // Começar a partir do dia atual
                currentStartDate = todayDate
                selectedDayIndex = 0 // Sempre o primeiro dia mostrado
            }
        }
    }
    
    private fun setupRecyclerViews() {
        // Setup RecyclerView dos medicamentos do dia
        medicamentosAdapter = MedicamentoDiaAdapter(
            onMedicamentoClick = { aplicacaoComStatus ->
                viewModel.marcarMedicamentoComoTomado(aplicacaoComStatus)
            }
        )
        
        binding.recyclerViewMedicamentosDia.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = medicamentosAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupObservers() {
        if (clienteId != -1L) {
            // Observar dados do cliente específico
            viewModel.allClientes.observe(viewLifecycleOwner) { clientes ->
                val cliente = clientes.find { it.id == clienteId }
                cliente?.let {
                    updateClienteInfo(it)
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Navegação do calendário semanal
        binding.iconChevronLeft.setOnClickListener {
            navigatePrevious()
        }
        
        binding.iconChevronRight.setOnClickListener {
            navigateNext()
        }
    }

    /**
     * Função helper para atualizar a UI com dados do cliente
     */
    private fun updateClienteInfo(cliente: com.example.cuidadores.data.model.Cliente) {
        binding.apply {
            textNomePaciente.text = cliente.nome
            textTelefone.text = cliente.telefone
            textHorariosAtendimento.setText(
                formatHorariosAtendimento(cliente.horariosAtendimento),
                TagView.TagType.BLUE
            )
            textEndereco.text = cliente.endereco
            textInicial.text = cliente.nome.firstOrNull()?.toString()?.uppercase() ?: "?"
        }
    }

    /**
     * Converte o JSON de horários de atendimento para formato legível
     * Agrupa horários iguais de forma inteligente
     */
    private fun formatHorariosAtendimento(horariosJson: String): String {
        try {
            val jsonArray = JSONArray(horariosJson)
            val horarios = mutableListOf<HorarioAtendimento>()
            
            // Parsear JSON para objetos HorarioAtendimento
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                horarios.add(
                    HorarioAtendimento(
                        diaSemana = jsonObject.getString("diaSemana"),
                        horarioInicio = jsonObject.getString("horarioInicio"),
                        horarioFim = jsonObject.getString("horarioFim")
                    )
                )
            }
            
            // Agrupar horários iguais
            val gruposHorarios = horarios.groupBy { "${it.horarioInicio}-${it.horarioFim}" }
            
            // Formatar cada grupo, ordenando por horário
            val grupos = gruposHorarios.toList().sortedBy { (horario, _) ->
                val inicio = horario.split("-")[0]
                inicio.replace(":", "").toIntOrNull() ?: 0
            }.map { (horario, dias) ->
                val diasOrdenados = ordenarDiasSemana(dias.map { it.diaSemana })
                val diasFormatados = formatarDiasConsecutivos(diasOrdenados)
                val horarioFormatado = formatarHorario(horario)
                "$diasFormatados $horarioFormatado"
            }
            
            return grupos.joinToString("\n")
            
        } catch (e: JSONException) {
            return getString(R.string.horarios_nao_definidos)
        }
    }

    /**
     * Ordena os dias da semana na ordem correta
     */
    private fun ordenarDiasSemana(dias: List<String>): List<String> {
        val ordemDias = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo")
        return dias.sortedBy { ordemDias.indexOf(it) }
    }

    /**
     * Formata dias consecutivos de forma inteligente
     * Ex: [Segunda, Terça, Quarta] -> "Segunda a Quarta"
     * Ex: [Segunda, Quarta] -> "Segunda e Quarta"
     */
    private fun formatarDiasConsecutivos(dias: List<String>): String {
        if (dias.isEmpty()) return ""
        if (dias.size == 1) return dias.first()
        
        val ordemDias = listOf("Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo")
        val indices = dias.map { ordemDias.indexOf(it) }.sorted()
        
        val grupos = mutableListOf<List<Int>>()
        var grupoAtual = mutableListOf<Int>()
        
        for (i in indices) {
            if (grupoAtual.isEmpty() || i == grupoAtual.last() + 1) {
                grupoAtual.add(i)
            } else {
                grupos.add(grupoAtual.toList())
                grupoAtual = mutableListOf(i)
            }
        }
        if (grupoAtual.isNotEmpty()) {
            grupos.add(grupoAtual.toList())
        }
        
        val textosGrupos = grupos.map { grupo ->
            if (grupo.size == 1) {
                ordemDias[grupo.first()]
            } else if (grupo.size == 2) {
                "${ordemDias[grupo.first()]} e ${ordemDias[grupo.last()]}"
            } else {
                "${ordemDias[grupo.first()]} a ${ordemDias[grupo.last()]}"
            }
        }
        
        return when (textosGrupos.size) {
            1 -> textosGrupos.first()
            2 -> "${textosGrupos[0]} e ${textosGrupos[1]}"
            else -> textosGrupos.joinToString(", ")
        }
    }

    /**
     * Formata o horário de HH:mm para HHh ou HHhMM
     * Ex: 08:00 → 08h, 08:30 → 08h30
     */
    private fun formatarHorario(horario: String): String {
        val partes = horario.split("-")
        return if (partes.size == 2) {
            val inicio = formatarHorarioIndividual(partes[0])
            val fim = formatarHorarioIndividual(partes[1])
            "$inicio às $fim"
        } else {
            horario
        }
    }
    
    /**
     * Formata um horário individual (HH:mm)
     * Se minutos = 00, mostra apenas HHh
     * Se minutos ≠ 00, mostra HHhMM
     */
    private fun formatarHorarioIndividual(horario: String): String {
        val partes = horario.split(":")
        return if (partes.size == 2) {
            val hora = partes[0]
            val minuto = partes[1]
            if (minuto == "00") {
                "${hora}h"
            } else {
                "${hora}h${minuto}"
            }
        } else {
            horario
        }
    }
    
    // === Métodos do Calendário ===
    
    private fun updateAllDaysDisplay() {
        diasBindings.forEachIndexed { index, dayBinding ->
            val currentDate = currentStartDate.plusDays(index.toLong())
            updateDayView(dayBinding, currentDate)
        }
        
        // Atualizar o texto do mês
        val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
        binding.textMesCalendarioSemanal.text = currentStartDate.format(monthFormatter)
    }
    
    private fun updateDayView(dayBinding: ItemCalendarioSemanalBinding, date: LocalDate) {
        // Primeira letra do dia da semana
        val dayOfWeek = when (date.dayOfWeek.value) {
            1 -> "S" // Segunda
            2 -> "T" // Terça
            3 -> "Q" // Quarta
            4 -> "Q" // Quinta
            5 -> "S" // Sexta
            6 -> "S" // Sábado
            7 -> "D" // Domingo
            else -> "?"
        }
        
        dayBinding.textInicialDiaSemana.text = dayOfWeek
        dayBinding.textDia.text = date.dayOfMonth.toString()
    }
    
    private fun setupDayClickListeners() {
        diasViews.forEachIndexed { index, dayView ->
            dayView.setOnClickListener {
                selectDay(index)
            }
        }
    }
    
    private fun selectDay(dayIndex: Int) {
        // Validar índice
        if (dayIndex !in 0..4) return
        
        // Desselecionar o dia anteriormente selecionado
        clearSelection()
        
        // Selecionar o dia clicado
        diasViews[dayIndex].isSelected = true
        selectedDayIndex = dayIndex
        
        // Atualizar a data selecionada
        selectedDate = currentStartDate.plusDays(dayIndex.toLong())
        
        // Atualizar dados do dia selecionado
        loadMedicamentosForDate(selectedDate)
    }

    
    private fun navigateNext() {
        currentStartDate = currentStartDate.plusDays(5)
        updateAllDaysDisplay()
        updateSelectionAfterNavigation()
    }
    
    private fun navigatePrevious() {
        currentStartDate = currentStartDate.minusDays(5)
        updateAllDaysDisplay()
        updateSelectionAfterNavigation()
    }
    
    /**
     * Atualiza a seleção após navegação
     * Mantém o dia selecionado se ele estiver visível na nova janela
     */
    private fun updateSelectionAfterNavigation() {
        // Verificar se a data selecionada está na janela atual (5 dias)
        if (selectedDate >= currentStartDate && selectedDate <= currentStartDate.plusDays(4)) {
            selectDay(selectedDayIndex)
        } else {
            clearSelection()
        }
    }

    /**
     * Função auxiliar para desselecionar o dia atualmente selecionado
     */
    private fun clearSelection() {
        if (selectedDayIndex in 0..4) {
            diasViews[selectedDayIndex].isSelected = false
        }
    }
    
    private fun loadMedicamentosForDate(date: LocalDate) {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        
        // Remover observação anterior se existir
        currentMedicamentosObserver?.let { observer ->
            val previousDateString = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            viewModel.getMedicamentosClientePorData(clienteId, previousDateString).removeObserver(observer)
        }
        
        // Criar nova observação
        currentMedicamentosObserver = Observer { medicamentos ->
            medicamentosAdapter.submitList(medicamentos)
            updateEmptyState(medicamentos)
            
            // Garantir que a seleção visual seja mantida após atualizações dos dados
            binding.root.post {
                if (selectedDayIndex in 0..4) {
                    diasViews[selectedDayIndex].isSelected = true
                }
            }
        }
        
        viewModel.getMedicamentosClientePorData(clienteId, dateString).observe(viewLifecycleOwner, currentMedicamentosObserver!!)
    }
    
    /**
     * Atualiza a visibilidade da mensagem de empty state
     */
    private fun updateEmptyState(medicamentos: List<MedicamentoAplicacao>) {
        if (medicamentos.isEmpty()) {
            binding.recyclerViewMedicamentosDia.visibility = View.GONE
            binding.textEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewMedicamentosDia.visibility = View.VISIBLE
            binding.textEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(clienteId: Long) = ClienteFragment().apply {
            arguments = Bundle().apply {
                putLong("clienteId", clienteId)
            }
        }
    }
} 