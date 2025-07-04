package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuidadores.R
import com.example.cuidadores.databinding.FragmentHomeSeuDiaBinding
import com.example.cuidadores.ui.adapter.ClienteAdapter
import com.example.cuidadores.ui.viewmodel.ClienteViewModel

class HomeSeuDiaFragment : Fragment() {

    private var _binding: FragmentHomeSeuDiaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClienteViewModel by viewModels()
    private lateinit var adapter: ClienteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeSeuDiaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter(
            onItemClick = { cliente ->
                // Navegar para o fragment de edição de cliente
                val bundle = Bundle().apply {
                    putLong("clienteId", cliente.id)
                }
                findNavController().navigate(R.id.action_navigation_patients_to_editarClienteFragment, bundle)
            },
            onMedicamentosClick = { cliente ->
                // Navegar para a lista de medicamentos do cliente
                val bundle = Bundle().apply {
                    putLong("clienteId", cliente.id)
                }
                // Usar o ID do fragment de destino diretamente
                findNavController().navigate(R.id.action_navigation_patients_to_listaMedicamentosFragment, bundle)
            },
            onDeleteClick = { cliente ->
                confirmarExclusao(cliente)
            }
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeSeuDiaFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.allClientes.observe(viewLifecycleOwner) { clientes ->
            adapter.submitList(clientes)
        }
    }

    private fun confirmarExclusao(cliente: com.example.cuidadores.data.model.Cliente) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja realmente excluir o cliente '${cliente.nome}'?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.delete(cliente)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 