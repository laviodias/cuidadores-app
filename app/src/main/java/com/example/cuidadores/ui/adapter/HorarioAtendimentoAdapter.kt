package com.example.cuidadores.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuidadores.data.model.HorarioAtendimento
import com.example.cuidadores.databinding.ItemHorarioAtendimentoBinding

class HorarioAtendimentoAdapter(
    private val onDeleteClick: (HorarioAtendimento) -> Unit = {}
) : ListAdapter<HorarioAtendimento, HorarioAtendimentoAdapter.HorarioAtendimentoViewHolder>(HorarioAtendimentoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorarioAtendimentoViewHolder {
        val binding = ItemHorarioAtendimentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HorarioAtendimentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorarioAtendimentoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HorarioAtendimentoViewHolder(
        private val binding: ItemHorarioAtendimentoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(horarioAtendimento: HorarioAtendimento) {
            binding.apply {
                textDiaSemana.text = horarioAtendimento.diaSemana
                textHorario.text = "${horarioAtendimento.horarioInicio} - ${horarioAtendimento.horarioFim}"
                
                buttonDelete.setOnClickListener {
                    onDeleteClick(horarioAtendimento)
                }
            }
        }
    }

    private class HorarioAtendimentoDiffCallback : DiffUtil.ItemCallback<HorarioAtendimento>() {
        override fun areItemsTheSame(oldItem: HorarioAtendimento, newItem: HorarioAtendimento): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HorarioAtendimento, newItem: HorarioAtendimento): Boolean {
            return oldItem == newItem
        }
    }
} 