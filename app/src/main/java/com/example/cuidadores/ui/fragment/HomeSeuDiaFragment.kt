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
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter { cliente ->
            // TODO: Implementar navegação para edição do cliente
        }
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

    private fun setupClickListeners() {
        binding.fabAdicionarCliente.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_patients_to_cadastroClienteFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 