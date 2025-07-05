package com.example.cuidadores.ui.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cuidadores.R
import com.example.cuidadores.ui.model.PacienteDia
import com.example.cuidadores.ui.model.AplicacaoComStatus
import com.example.cuidadores.databinding.ItemPacienteDiaBinding

class PacienteDiaAdapter(
    private val onMedicamentoClick: (AplicacaoComStatus) -> Unit = {}
) : ListAdapter<PacienteDia, PacienteDiaAdapter.PacienteDiaViewHolder>(PacienteDiaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteDiaViewHolder {
        val binding = ItemPacienteDiaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ) // Definindo o componente de layout que sera utilizado em cada item gerenciado pelo viewHolder (item_paciente_dia.xml)
        return PacienteDiaViewHolder(binding) // Retornando o viewHolder com o binding
    }

    override fun onBindViewHolder(holder: PacienteDiaViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    } // Depois de criar o viewHolder, o onBindViewHolder é chamado para cada item da lista

    inner class PacienteDiaViewHolder(
        private val binding: ItemPacienteDiaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var medicamentosAdapter: MedicamentoDiaAdapter

        fun bind(pacienteDia: PacienteDia, position: Int) {
            binding.apply {
                // Configurar dados básicos do paciente
                textInicial.text = pacienteDia.inicialNome
                textNomePaciente.text = pacienteDia.cliente.nome
                textMedicacoesPara.text = pacienteDia.descricaoMedicamentos

                // Configurar RecyclerView de medicamentos
                setupMedicamentosRecyclerView(pacienteDia)

                // Configurar estado de expansão
                updateExpansionState(pacienteDia.isExpanded, animate = false) // Os recyclerViews internos de todos os cards de paciente são carregados no load da pagina

                // Configurar click listener para expansão
                layoutCabecalho.setOnClickListener { // Id do LinearLayout que contem o conteúdo do card colapsado
                    toggleExpansion(position)
                }
            }
        }

        private fun setupMedicamentosRecyclerView(pacienteDia: PacienteDia) {
            medicamentosAdapter = MedicamentoDiaAdapter(onMedicamentoClick)
            
            binding.recyclerMedicamentos.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = medicamentosAdapter
                // Desabilitar nested scrolling para melhor performance
                isNestedScrollingEnabled = false
            }

            medicamentosAdapter.submitList(pacienteDia.medicamentosHoje)
        }

        private fun toggleExpansion(position: Int) {
            val currentItem = getItem(position)
            val newList = currentList.toMutableList()
            
            // Colapsar todos os outros itens
            newList.forEachIndexed { index, item ->
                if (index != position && item.isExpanded) {
                    newList[index] = item.copy(isExpanded = false)
                }
            }
            
            // Toggle do item atual
            newList[position] = currentItem.copy(isExpanded = !currentItem.isExpanded)
            
            // Atualizar lista
            submitList(newList)
        }

        private fun updateExpansionState(isExpanded: Boolean, animate: Boolean = true) {
            binding.apply {
                if (isExpanded) {
                    // Estado expandido
                    imageChevron.setImageResource(R.drawable.ic_chevron_up_24dp)
                    viewDivisor.visibility = View.VISIBLE
                    
                    if (animate) {
                        animateRecyclerViewExpansion(recyclerMedicamentos, true)
                    } else {
                        recyclerMedicamentos.visibility = View.VISIBLE
                    }
                } else {
                    // Estado colapsado
                    imageChevron.setImageResource(R.drawable.ic_chevron_down_24dp)
                    viewDivisor.visibility = View.GONE
                    
                    if (animate) {
                        animateRecyclerViewExpansion(recyclerMedicamentos, false)
                    } else {
                        recyclerMedicamentos.visibility = View.GONE
                    }
                }
                
                // Animar rotação do chevron
                if (animate) {
                    val rotation = if (isExpanded) 180f else 0f
                    imageChevron.animate()
                        .rotation(rotation)
                        .setDuration(300)
                        .start()
                }
            }
        }

        private fun animateRecyclerViewExpansion(view: RecyclerView, expand: Boolean) {
            if (expand) {
                // Expandir
                view.visibility = View.VISIBLE
                view.measure(
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val targetHeight = view.measuredHeight

                view.layoutParams.height = 0
                val animator = ValueAnimator.ofInt(0, targetHeight)
                animator.addUpdateListener { animation ->
                    view.layoutParams.height = animation.animatedValue as Int
                    view.requestLayout()
                }
                animator.duration = 300
                animator.start()
            } else {
                // Colapsar
                val initialHeight = view.measuredHeight
                val animator = ValueAnimator.ofInt(initialHeight, 0)
                animator.addUpdateListener { animation ->
                    view.layoutParams.height = animation.animatedValue as Int
                    view.requestLayout()
                }
                animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        view.visibility = View.GONE
                        view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }
                })
                animator.duration = 300
                animator.start()
            }
        }
    }

    private class PacienteDiaDiffCallback : DiffUtil.ItemCallback<PacienteDia>() {
        override fun areItemsTheSame(oldItem: PacienteDia, newItem: PacienteDia): Boolean {
            return oldItem.cliente.id == newItem.cliente.id
        }

        override fun areContentsTheSame(oldItem: PacienteDia, newItem: PacienteDia): Boolean {
            return oldItem == newItem
        }
    }
} 