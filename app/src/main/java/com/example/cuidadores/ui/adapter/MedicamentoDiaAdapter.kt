package com.example.cuidadores.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuidadores.databinding.ItemMedicamentoDiaBinding
import com.example.cuidadores.ui.model.AplicacaoComStatus
import com.example.cuidadores.ui.model.MedicamentoAplicacao

class MedicamentoDiaAdapter(
    private val onMedicamentoClick: (AplicacaoComStatus) -> Unit = {}
) : ListAdapter<MedicamentoAplicacao, MedicamentoDiaAdapter.MedicamentoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val binding = ItemMedicamentoDiaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ) // Definindo o componente de layout que sera utilizado em cada item gerenciado pelo viewHolder (item_medicamento_dia.xml)
        return MedicamentoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MedicamentoViewHolder(
        private val binding: ItemMedicamentoDiaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medicamentoAplicacao: MedicamentoAplicacao) {
            binding.apply {
                // Nome do medicamento
                textNomeMedicamento.text = medicamentoAplicacao.medicamento.nome
                
                // Dose e horário formatados
                textDoseHorario.text = medicamentoAplicacao.doseEHorario
                
                // Instruções (se houver)
                val instrucoes = medicamentoAplicacao.instrucoes
                if (instrucoes.isNotEmpty()) {
                    textInstrucoes.text = instrucoes
                } else {
                    textInstrucoes.text = "Sem instruções específicas"
                }
                
                // Uma aplicação por card
                val aplicacaoComStatus = medicamentoAplicacao.aplicacaoComStatus
                
                viewStatusIndicator.visibility = android.view.View.VISIBLE
                
                // Configurar cor do indicador baseado no status
                when {
                    aplicacaoComStatus.isConcluida -> {
                        viewStatusIndicator.setBackgroundColor(
                            binding.root.context.getColor(android.R.color.holo_green_light)
                        )
                    }
                    aplicacaoComStatus.isAtrasada -> {
                        viewStatusIndicator.setBackgroundColor(
                            binding.root.context.getColor(android.R.color.holo_red_light)
                        )
                    }
                    aplicacaoComStatus.isPerdida -> {
                        viewStatusIndicator.setBackgroundColor(
                            binding.root.context.getColor(android.R.color.darker_gray)
                        )
                    }
                    aplicacaoComStatus.isPendente -> {
                        viewStatusIndicator.setBackgroundColor(
                            binding.root.context.getColor(android.R.color.holo_orange_light)
                        )
                    }
                    else -> {
                        viewStatusIndicator.setBackgroundColor(
                            binding.root.context.getColor(android.R.color.holo_orange_light)
                        )
                    }
                }
                
                // Click listener para marcar como tomado
                // Permitir clicar em medicamentos perdidos e pendentes
                if (aplicacaoComStatus.isPerdida || aplicacaoComStatus.isPendente) {
                    itemView.setOnClickListener { 
                        onMedicamentoClick(aplicacaoComStatus)
                    }
                } else {
                    itemView.setOnClickListener(null)
                }
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<MedicamentoAplicacao>() {
        override fun areItemsTheSame(oldItem: MedicamentoAplicacao, newItem: MedicamentoAplicacao): Boolean {
            return oldItem.medicamento.id == newItem.medicamento.id && 
                   oldItem.aplicacaoComStatus.aplicacaoReceita.id == newItem.aplicacaoComStatus.aplicacaoReceita.id
        }

        override fun areContentsTheSame(oldItem: MedicamentoAplicacao, newItem: MedicamentoAplicacao): Boolean {
            return oldItem == newItem
        }
    }
} 