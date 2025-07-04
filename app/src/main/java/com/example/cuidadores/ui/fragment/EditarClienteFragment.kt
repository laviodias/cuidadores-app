package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.databinding.FragmentCadastroClienteBinding
import com.example.cuidadores.ui.viewmodel.ClienteViewModel
import kotlinx.coroutines.launch

class EditarClienteFragment : Fragment() {

    private var _binding: FragmentCadastroClienteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClienteViewModel by viewModels()
    private var clienteId: Long = 0
    private var cliente: Cliente? = null

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
        arguments?.let {
            clienteId = it.getLong("clienteId", 0)
        }
        if (clienteId == 0L) {
            Toast.makeText(context, "Erro: ID do cliente não encontrado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }
        carregarCliente()
        setupClickListeners()
    }

    private fun carregarCliente() {
        lifecycleScope.launch {
            try {
                val clienteEncontrado = viewModel.getClienteById(clienteId)
                cliente = clienteEncontrado
                clienteEncontrado?.let { c ->
                    binding.editNome.setText(c.nome)
                    binding.editTelefone.setText(c.telefone)
                    binding.editEndereco.setText(c.endereco)
                    binding.editHorariosAtendimento.setText(c.horariosAtendimento)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao carregar cliente: ${e.message}", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonSalvar.setOnClickListener {
            if (validarCampos()) {
                salvarEdicaoCliente()
            }
        }
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

    private fun salvarEdicaoCliente() {
        val clienteEditado = cliente?.copy(
            nome = binding.editNome.text.toString(),
            telefone = binding.editTelefone.text.toString(),
            endereco = binding.editEndereco.text.toString(),
            horariosAtendimento = binding.editHorariosAtendimento.text.toString()
        )
        if (clienteEditado != null) {
            viewModel.update(clienteEditado)
            Toast.makeText(requireContext(), "Cliente atualizado com sucesso!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 