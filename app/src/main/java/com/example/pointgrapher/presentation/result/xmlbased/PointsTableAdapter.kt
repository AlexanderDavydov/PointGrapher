package com.example.pointgrapher.presentation.result.xmlbased

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pointgrapher.databinding.ItemPointRowBinding
import com.example.pointgrapher.presentation.result.model.PointViewData
import java.text.DecimalFormat

data class PointTableItem(
    val x: Double,
    val y: Double
)

class PointsTableAdapter :
    ListAdapter<PointTableItem, PointsTableAdapter.PointViewHolder>(PointDiffCallback()) {

    private val decimalFormat = DecimalFormat("#.###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val binding = ItemPointRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PointViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position), decimalFormat)
    }

    class PointViewHolder(
        private val binding: ItemPointRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(point: PointTableItem, formatter: DecimalFormat) {
            binding.apply {
                tvXCoordinate.text = formatter.format(point.x)
                tvYCoordinate.text = formatter.format(point.y)
            }
        }
    }

    private class PointDiffCallback : DiffUtil.ItemCallback<PointTableItem>() {
        override fun areItemsTheSame(oldItem: PointTableItem, newItem: PointTableItem): Boolean {
            return oldItem.x == newItem.x && oldItem.y == newItem.y
        }

        override fun areContentsTheSame(oldItem: PointTableItem, newItem: PointTableItem): Boolean {
            return oldItem == newItem
        }
    }

    fun updatePoints(points: PointViewData) {
        val items = points.x.zip(points.y) { x, y ->
            PointTableItem(x.toDouble(), y.toDouble())
        }
        submitList(items)
    }
}