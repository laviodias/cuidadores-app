package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.databinding.FragmentAdicionarMedicamentoBinding
import com.example.cuidadores.ui.viewmodel.MedicamentoViewModel
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.R
import com.example.cuidadores.data.model.AplicacaoReceita
import com.example.cuidadores.ui.viewmodel.ClienteViewModel
import androidx.fragment.app.viewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdicionarMedicamentoFragment : Fragment() {

    private var _binding: FragmentAdicionarMedicamentoBinding? = null
    private val binding get() = _binding!!
    private val medicamentoViewModel: MedicamentoViewModel by activityViewModels()
    private val clienteViewModel: ClienteViewModel by viewModels()
    private var clienteId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdicionarMedicamentoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Receber o ID do cliente dos argumentos
        arguments?.let {
            clienteId = it.getLong("clienteId", 0)
        }

        if (clienteId == 0L) {
            Toast.makeText(context, "Erro: ID do cliente não encontrado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupButtons()
        setupDefaultValues()
    }

    private fun setupButtons() {
        binding.buttonSalvar.setOnClickListener {
            salvarMedicamento()
        }

        binding.buttonCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupDefaultValues() {
        // Preencher data de início com a data atual
        val hoje = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
        binding.editTextDataInicio.setText(hoje)
        
        // Configurar DatePickers
        setupDatePickers()
    }
    
    private fun setupDatePickers() {
        binding.editTextDataInicio.setOnClickListener {
            showDatePicker(binding.editTextDataInicio, "Data de Início")
        }
        
        binding.editTextDataFim.setOnClickListener {
            showDatePicker(binding.editTextDataFim, "Data de Fim")
        }
    }
    
    private fun showDatePicker(editText: com.google.android.material.textfield.TextInputEditText, title: String) {
        val calendar = java.util.Calendar.getInstance()
        
        // Se já tem uma data, usar ela como inicial
        val currentText = editText.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val date = java.time.LocalDate.parse(currentText)
                calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
            } catch (e: Exception) {
                // Se não conseguir parsear, usar data atual
            }
        }
        
        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = java.time.LocalDate.of(year, month + 1, dayOfMonth)
                val formattedDate = selectedDate.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE)
                editText.setText(formattedDate)
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
        
        datePickerDialog.setTitle(title)
        datePickerDialog.show()
    }

    private fun salvarMedicamento() {
        val nome = binding.editTextNomeMedicamento.text.toString().trim()
        val dosagem = binding.editTextDosagem.text.toString().trim()
        val horario = binding.editTextHorario.text.toString().trim()
        val dataInicio = binding.editTextDataInicio.text.toString().trim()
        val dataFim = binding.editTextDataFim.text.toString().trim()
        val observacoesGerais = binding.editTextObservacoes.text.toString().trim()

        // Validação dos campos obrigatórios
        if (nome.isEmpty()) {
            binding.editTextNomeMedicamento.error = "Nome do medicamento é obrigatório"
            return
        }

        if (dosagem.isEmpty()) {
            binding.editTextDosagem.error = "Dosagem é obrigatória"
            return
        }

        if (horario.isEmpty()) {
            binding.editTextHorario.error = "Horário é obrigatório"
            return
        }

        if (dataInicio.isEmpty()) {
            binding.editTextDataInicio.error = "Data de início é obrigatória"
            return
        }

        // Criar o medicamento
        val medicamento = Medicamento(
            clienteId = clienteId,
            nome = nome,
            dosagem = dosagem,
            dataInicio = dataInicio,
            dataFim = if (dataFim.isEmpty()) null else dataFim,
            observacoesGerais = observacoesGerais,
            horario = horario
        )

        // Salvar no banco de dados e criar aplicação de receita
        CoroutineScope(Dispatchers.IO).launch {
            val medicamentoId = medicamentoViewModel.insertAndReturnId(medicamento)
            val aplicacao = AplicacaoReceita(
                medicamentoId = medicamentoId,
                horario = horario,
                observacoes = observacoesGerais
            )
            clienteViewModel.insertAplicacaoReceita(aplicacao)
            launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Medicamento adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


} 