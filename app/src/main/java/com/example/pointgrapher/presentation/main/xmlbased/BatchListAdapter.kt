package com.example.pointgrapher.presentation.main.xmlbased

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pointgrapher.databinding.ItemBatchBinding
import com.example.pointgrapher.presentation.main.viewdata.BatchInfoViewData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BatchListAdapter(
    private val onBatchClick: (String) -> Unit,
    private val onBatchDelete: (String) -> Unit
) : ListAdapter<BatchInfoViewData, BatchListAdapter.BatchViewHolder>(BatchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchViewHolder {
        val binding = ItemBatchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BatchViewHolder(binding, onBatchClick)
    }

    override fun onBindViewHolder(holder: BatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BatchViewHolder(
        private val binding: ItemBatchBinding,
        private val onBatchClick: (String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(batch: BatchInfoViewData) {
            binding.apply {
                tvBatchTitle.text = "Batch of ${batch.numberOfPoints} items"
                tvBatchDate.text = "Created: ${formatDate(batch.time)}"
                tvBatchId.text = "ID: ${batch.id.take(8)}..."
                root.setOnClickListener {
                    onBatchClick(batch.id)
                }
            }
        }

        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return formatter.format(date)
        }
    }

    private class BatchDiffCallback : DiffUtil.ItemCallback<BatchInfoViewData>() {
        override fun areItemsTheSame(oldItem: BatchInfoViewData, newItem: BatchInfoViewData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BatchInfoViewData, newItem: BatchInfoViewData): Boolean {
            return oldItem == newItem
        }
    }

    fun attachSwipeToDelete(recyclerView: RecyclerView) {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val batch = getItem(position)
                    onBatchDelete(batch.id)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}