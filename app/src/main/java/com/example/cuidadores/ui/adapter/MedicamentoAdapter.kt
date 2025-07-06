package com.example.cuidadores.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuidadores.data.model.Medicamento
import com.example.cuidadores.databinding.ItemMedicamentoBinding

class MedicamentoAdapter(
    private val onDeleteClick: (Medicamento) -> Unit
) : ListAdapter<Medicamento, MedicamentoAdapter.MedicamentoViewHolder>(MedicamentoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val binding = ItemMedicamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MedicamentoViewHolder(
        private val binding: ItemMedicamentoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicamento: Medicamento) {
            binding.textViewNomeMedicamento.text = medicamento.nome
            binding.textViewDosagem.text = "Dosagem: ${medicamento.dosagem}"
            binding.textViewHorario.text = "Horário: ${medicamento.horario}"
            
            if (!medicamento.observacoesGerais.isNullOrEmpty()) {
                binding.textViewObservacoes.text = "Observações: ${medicamento.observacoesGerais}"
                binding.textViewObservacoes.visibility = android.view.View.VISIBLE
            } else {
                binding.textViewObservacoes.visibility = android.view.View.GONE
            }

            binding.buttonExcluir.setOnClickListener {
                onDeleteClick(medicamento)
            }
        }
    }

    private class MedicamentoDiffCallback : DiffUtil.ItemCallback<Medicamento>() {
        override fun areItemsTheSame(oldItem: Medicamento, newItem: Medicamento): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Medicamento, newItem: Medicamento): Boolean {
            return oldItem == newItem
        }
    }
} 