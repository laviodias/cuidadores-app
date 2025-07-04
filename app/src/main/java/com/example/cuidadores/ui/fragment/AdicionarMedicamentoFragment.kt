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

class AdicionarMedicamentoFragment : Fragment() {

    private var _binding: FragmentAdicionarMedicamentoBinding? = null
    private val binding get() = _binding!!
    private val medicamentoViewModel: MedicamentoViewModel by activityViewModels()
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
    }

    private fun setupButtons() {
        binding.buttonSalvar.setOnClickListener {
            salvarMedicamento()
        }

        binding.buttonCancelar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun salvarMedicamento() {
        val nome = binding.editTextNomeMedicamento.text.toString().trim()
        val dosagem = binding.editTextDosagem.text.toString().trim()
        val horario = binding.editTextHorario.text.toString().trim()
        val observacoes = binding.editTextObservacoes.text.toString().trim()

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

        // Criar o medicamento
        val medicamento = Medicamento(
            clienteId = clienteId,
            nome = nome,
            dosagem = dosagem,
            horario = horario,
            observacoes = observacoes
        )

        // Salvar no banco de dados
        medicamentoViewModel.insert(medicamento)

        Toast.makeText(context, "Medicamento adicionado com sucesso!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


} 