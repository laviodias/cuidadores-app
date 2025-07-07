package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.data.model.HorarioAtendimento
import com.example.cuidadores.databinding.FragmentCadastroClienteBinding
import com.example.cuidadores.ui.adapter.HorarioAtendimentoAdapter
import com.example.cuidadores.ui.viewmodel.ClienteViewModel
import com.example.cuidadores.ui.fragment.AdicionarHorarioDialogFragment

class CadastroClienteFragment : Fragment() {

    private var _binding: FragmentCadastroClienteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClienteViewModel by viewModels()
    private lateinit var horariosAdapter: HorarioAtendimentoAdapter
    private val horariosList = mutableListOf<HorarioAtendimento>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCadastroClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        updateEmptyState() // Garantir estado inicial correto
    }

    private fun setupRecyclerView() {
        horariosAdapter = HorarioAtendimentoAdapter { horario ->
            // Remover horário da lista
            horariosList.remove(horario)
            horariosAdapter.submitList(horariosList.toList())
            updateEmptyState()
        }
        
        binding.recyclerViewHorarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = horariosAdapter
        }
    }
    
    private fun updateEmptyState() {
        if (horariosList.isEmpty()) {
            binding.recyclerViewHorarios.visibility = View.GONE
            binding.textEmptyHorarios.visibility = View.VISIBLE
        } else {
            binding.recyclerViewHorarios.visibility = View.VISIBLE
            binding.textEmptyHorarios.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.buttonSalvar.setOnClickListener {
            if (validarCampos()) {
                salvarCliente()
            }
        }
        
        binding.buttonAdicionarHorario.setOnClickListener {
            abrirAdicionarHorario()
        }
    }
    
    private fun abrirAdicionarHorario() {
        val dialogFragment = AdicionarHorarioDialogFragment.newInstance(0) { horario ->
            // Adicionar horário à lista
            horariosList.add(horario)
            horariosAdapter.submitList(horariosList.toList())
            updateEmptyState()
        }
        
        dialogFragment.show(childFragmentManager, "AdicionarHorarioDialog")
    }

    private fun validarCampos(): Boolean {
        var isValid = true

        with(binding) {
            if (editNome.text.isNullOrBlank()) {
                layoutNome.error = "Nome é obrigatório"
                isValid = false
            } else {
                layoutNome.error = null
            }

            if (editTelefone.text.isNullOrBlank()) {
                layoutTelefone.error = "Telefone é obrigatório"
                isValid = false
            } else {
                layoutTelefone.error = null
            }

            if (editEndereco.text.isNullOrBlank()) {
                layoutEndereco.error = "Endereço é obrigatório"
                isValid = false
            } else {
                layoutEndereco.error = null
            }
        }

        return isValid
    }

    private fun salvarCliente() {
        val cliente = Cliente(
            nome = binding.editNome.text.toString(),
            telefone = binding.editTelefone.text.toString(),
            endereco = binding.editEndereco.text.toString()
        )

        // Salvar cliente e horários juntos
        viewModel.salvarClienteComHorarios(cliente, horariosList.toList())
        
        Toast.makeText(requireContext(), "Cliente salvo com sucesso!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 