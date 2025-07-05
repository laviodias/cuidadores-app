package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cuidadores.databinding.FragmentHomeSeuDiaBinding
import com.example.cuidadores.ui.adapter.PacienteDiaAdapter
import com.example.cuidadores.ui.viewmodel.ClienteViewModel
import com.example.cuidadores.ui.model.AplicacaoComStatus

class HomeSeuDiaFragment : Fragment() {

    private var _binding: FragmentHomeSeuDiaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClienteViewModel by viewModels()
    private lateinit var adapter: PacienteDiaAdapter

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
        adapter = PacienteDiaAdapter { aplicacaoComStatus ->
            viewModel.marcarMedicamentoComoTomado(aplicacaoComStatus)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeSeuDiaFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.getPacientesComMedicacaoHoje().observe(viewLifecycleOwner) { pacientes ->
            adapter.submitList(pacientes)
        }
        
        // TODO: Remover quando tiver dados reais
        // Criar dados de exemplo na primeira vez
        viewModel.allClientes.observe(viewLifecycleOwner) { clientes ->
            if (clientes.isEmpty()) {
                viewModel.criarDadosDeExemplo()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 