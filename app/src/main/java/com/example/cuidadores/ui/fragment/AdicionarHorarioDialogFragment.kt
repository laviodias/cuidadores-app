package com.example.cuidadores.ui.fragment

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.cuidadores.data.model.HorarioAtendimento
import com.example.cuidadores.databinding.FragmentAdicionarHorarioBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AdicionarHorarioDialogFragment : DialogFragment() {

    private var _binding: FragmentAdicionarHorarioBinding? = null
    private val binding get() = _binding!!
    private var clienteId: Long = 0
    private var onHorarioAdicionado: ((HorarioAtendimento) -> Unit)? = null

    companion object {
        private const val ARG_CLIENTE_ID = "clienteId"
        
        fun newInstance(clienteId: Long, onHorarioAdicionado: (HorarioAtendimento) -> Unit): AdicionarHorarioDialogFragment {
            return AdicionarHorarioDialogFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CLIENTE_ID, clienteId)
                }
                this.onHorarioAdicionado = onHorarioAdicionado
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdicionarHorarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        arguments?.let {
            clienteId = it.getLong(ARG_CLIENTE_ID, 0)
        }

        setupDiaSemanaSpinner()
        setupTimePickers()
        setupButtons()
    }

    private fun setupDiaSemanaSpinner() {
        val diasSemana = arrayOf(
            "Segunda-feira",
            "Terça-feira", 
            "Quarta-feira",
            "Quinta-feira",
            "Sexta-feira",
            "Sábado",
            "Domingo"
        )
        
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, diasSemana)
        binding.spinnerDiaSemana.setAdapter(adapter)
    }

    private fun setupTimePickers() {
        binding.editTextHorarioInicio.setOnClickListener {
            showTimePicker(binding.editTextHorarioInicio, "Horário de Início")
        }
        
        binding.editTextHorarioFim.setOnClickListener {
            showTimePicker(binding.editTextHorarioFim, "Horário de Fim")
        }
    }

    private fun showTimePicker(editText: com.google.android.material.textfield.TextInputEditText, title: String) {
        val calendar = java.util.Calendar.getInstance()
        
        // Se já tem um horário, usar ele como inicial
        val currentText = editText.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val time = LocalTime.parse(currentText, DateTimeFormatter.ofPattern("HH:mm"))
                calendar.set(java.util.Calendar.HOUR_OF_DAY, time.hour)
                calendar.set(java.util.Calendar.MINUTE, time.minute)
            } catch (e: Exception) {
                // Se não conseguir parsear, usar horário atual
            }
        }
        
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = LocalTime.of(hourOfDay, minute)
                val formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                editText.setText(formattedTime)
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            true
        )
        
        timePickerDialog.setTitle(title)
        timePickerDialog.show()
    }

    private fun setupButtons() {
        binding.buttonSalvar.setOnClickListener {
            salvarHorario()
        }
        
        binding.buttonCancelar.setOnClickListener {
            dismiss()
        }
    }

    private fun salvarHorario() {
        val diaSemana = binding.spinnerDiaSemana.text.toString()
        val horarioInicio = binding.editTextHorarioInicio.text.toString()
        val horarioFim = binding.editTextHorarioFim.text.toString()

        // Validação
        if (diaSemana.isEmpty()) {
            binding.spinnerDiaSemana.error = "Selecione um dia da semana"
            return
        }

        if (horarioInicio.isEmpty()) {
            binding.editTextHorarioInicio.error = "Selecione o horário de início"
            return
        }

        if (horarioFim.isEmpty()) {
            binding.editTextHorarioFim.error = "Selecione o horário de fim"
            return
        }

        // Validar se horário fim é depois do início
        try {
            val inicio = LocalTime.parse(horarioInicio, DateTimeFormatter.ofPattern("HH:mm"))
            val fim = LocalTime.parse(horarioFim, DateTimeFormatter.ofPattern("HH:mm"))
            
            if (fim.isBefore(inicio) || fim.equals(inicio)) {
                binding.editTextHorarioFim.error = "Horário de fim deve ser depois do início"
                return
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Formato de horário inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar horário de atendimento
        val horarioAtendimento = HorarioAtendimento(
            clienteId = clienteId,
            diaSemana = diaSemana,
            horarioInicio = horarioInicio,
            horarioFim = horarioFim
        )

        // Chamar callback
        onHorarioAdicionado?.invoke(horarioAtendimento)
        
        // Fechar dialog
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 