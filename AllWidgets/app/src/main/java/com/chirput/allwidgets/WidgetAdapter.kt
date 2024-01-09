package com.chirput.allwidgets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WidgetAdapter(
    private val context: Context,
    private val items: List<WidgetModel>,
    private val listener: Listener
) : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    class WidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val widgetPreview: ImageView = itemView.findViewById(R.id.widget_preview)
        val widgetName: TextView = itemView.findViewById(R.id.widget_name)
        val widgetIcon: ImageView = itemView.findViewById(R.id.widget_icon)
        val widgetDescription: TextView = itemView.findViewById(R.id.widget_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_widget_item, parent, false)

        val holder = WidgetViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition

            if (position != RecyclerView.NO_POSITION) {
                listener.onWidgetSelected(getItem(position).info)
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        val placeholder = ContextCompat.getDrawable(context, R.drawable.ic_widget_no_preview)

        Glide
            .with(context)
            .load(items[position].previewImageUri)
            .placeholder(placeholder)
            .into(holder.widgetPreview)

        Glide
            .with(context)
            .load(items[position].icon)
            .centerCrop()
            .placeholder(placeholder)
            .into(holder.widgetIcon)

        holder.widgetName.text = items[position].widgetName
        holder.widgetDescription.text = items[position].description
    }

    override fun getItemCount(): Int = items.size

    private fun getItem(position: Int): WidgetModel {
        return items[position]
    }


}
