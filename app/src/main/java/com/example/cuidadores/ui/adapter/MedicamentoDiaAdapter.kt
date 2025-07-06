package com.example.cuidadores.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuidadores.databinding.ItemMedicamentoDiaBinding
import com.example.cuidadores.ui.model.AplicacaoComStatus
import com.example.cuidadores.ui.model.MedicamentoAplicacao
import com.example.cuidadores.ui.view.TagView
import com.example.cuidadores.data.model.RegistroAplicacao

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
                
                // Configurar TagView baseado no status
                val (statusText, statusType) = when (aplicacaoComStatus.status) {
                    RegistroAplicacao.STATUS_CONCLUIDO -> "Concluído" to TagView.TagType.GREEN
                    RegistroAplicacao.STATUS_ATRASADO -> "Atrasado" to TagView.TagType.ORANGE
                    RegistroAplicacao.STATUS_PERDIDO -> "Perdido" to TagView.TagType.RED
                    AplicacaoComStatus.STATUS_PENDENTE_UI -> "Pendente" to TagView.TagType.GRAY
                    AplicacaoComStatus.STATUS_FUTURO_UI -> "Agendado" to TagView.TagType.BLUE
                    else -> "Pendente" to TagView.TagType.GRAY
                }
                
                tagStatus.setText(statusText, statusType)
                
                // Click listener para marcar como tomado
                // Permitir clicar em medicamentos perdidos e pendentes
                when (aplicacaoComStatus.status) {
                    RegistroAplicacao.STATUS_PERDIDO,
                    AplicacaoComStatus.STATUS_PENDENTE_UI -> {
                        itemView.setOnClickListener { 
                            showConfirmationDialog(medicamentoAplicacao, aplicacaoComStatus)
                        }
                    }
                    else -> {
                        itemView.setOnClickListener(null)
                    }
                }
            }
        }
        
        /**
         * Exibe dialog de confirmação antes de marcar medicamento como tomado
         */
        private fun showConfirmationDialog(medicamentoAplicacao: MedicamentoAplicacao, aplicacaoComStatus: AplicacaoComStatus) {
            val context = binding.root.context
            val medicamento = medicamentoAplicacao.medicamento
            
            val titulo = "Confirmar aplicação"
            val mensagem = buildString {
                append("Confirma que tomou o medicamento?\n\n")
                append("• ${medicamento.nome}\n")
                append("• ${medicamentoAplicacao.doseEHorario}\n")
                if (medicamentoAplicacao.instrucoes.isNotEmpty()) {
                    append("• ${medicamentoAplicacao.instrucoes}")
                }
            }
            
            MaterialAlertDialogBuilder(context)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("Confirmar") { _, _ ->
                    onMedicamentoClick(aplicacaoComStatus)
                }
                .setNegativeButton("Cancelar", null)
                .show()
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