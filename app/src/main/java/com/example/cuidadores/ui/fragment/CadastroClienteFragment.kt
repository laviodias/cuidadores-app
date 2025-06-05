package com.example.cuidadores.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cuidadores.data.model.Cliente
import com.example.cuidadores.databinding.FragmentCadastroClienteBinding
import com.example.cuidadores.ui.viewmodel.ClienteViewModel

class CadastroClienteFragment : Fragment() {

    private var _binding: FragmentCadastroClienteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClienteViewModel by viewModels()

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
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.buttonSalvar.setOnClickListener {
            if (validarCampos()) {
                salvarCliente()
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

    private fun salvarCliente() {
        val cliente = Cliente(
            nome = binding.editNome.text.toString(),
            telefone = binding.editTelefone.text.toString(),
            endereco = binding.editEndereco.text.toString(),
            medicamentos = binding.editMedicamentos.text.toString(),
            horariosAtendimento = binding.editHorariosAtendimento.text.toString()
        )

        viewModel.insert(cliente)
        Toast.makeText(requireContext(), "Cliente salvo com sucesso!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 