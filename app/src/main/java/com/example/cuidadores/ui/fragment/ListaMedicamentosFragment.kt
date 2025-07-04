package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.databinding.FragmentListaMedicamentosBinding
import com.example.cuidadores.ui.adapter.MedicamentoAdapter
import com.example.cuidadores.ui.viewmodel.MedicamentoViewModel
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.R

class ListaMedicamentosFragment : Fragment() {

    private var _binding: FragmentListaMedicamentosBinding? = null
    private val binding get() = _binding!!
    private val medicamentoViewModel: MedicamentoViewModel by activityViewModels()
    private var clienteId: Long = 0
    private lateinit var medicamentoAdapter: MedicamentoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaMedicamentosBinding.inflate(inflater, container, false)
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

        setupRecyclerView()
        setupObservers()
        setupButtons()
    }

    private fun setupRecyclerView() {
        medicamentoAdapter = MedicamentoAdapter { medicamento ->
            confirmarExclusao(medicamento)
        }

        binding.recyclerViewMedicamentos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = medicamentoAdapter
        }
    }

    private fun setupObservers() {
        medicamentoViewModel.getMedicamentosByClienteId(clienteId).observe(viewLifecycleOwner) { medicamentos ->
            medicamentoAdapter.submitList(medicamentos)
            
            if (medicamentos.isEmpty()) {
                binding.textViewVazio.visibility = View.VISIBLE
                binding.recyclerViewMedicamentos.visibility = View.GONE
            } else {
                binding.textViewVazio.visibility = View.GONE
                binding.recyclerViewMedicamentos.visibility = View.VISIBLE
            }
        }
    }

    private fun setupButtons() {
        binding.fabAdicionarMedicamento.setOnClickListener {
            // Navegar para o fragment de adicionar medicamento
            val bundle = Bundle().apply {
                putLong("clienteId", clienteId)
            }
            // Usar o ID do fragment de destino diretamente
            findNavController().navigate(R.id.action_listaMedicamentosFragment_to_adicionarMedicamentoFragment, bundle)
        }
    }

    private fun confirmarExclusao(medicamento: Medicamento) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja realmente excluir o medicamento '${medicamento.nome}'?")
            .setPositiveButton("Sim") { _, _ ->
                medicamentoViewModel.delete(medicamento)
                Toast.makeText(context, "Medicamento excluído com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


} 