package com.chirput.allwidgets

import android.content.Context
import android.graphics.drawable.Drawable
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
    private val items: List<WidgetModel>
) : RecyclerView.Adapter<WidgetAdapter.WidgetViewHolder>() {

    class WidgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val widgetPreview: ImageView = itemView.findViewById(R.id.widget_preview)
        val widgetName: TextView = itemView.findViewById(R.id.widget_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_widget_item, parent, false)
        return WidgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        val placeholder = ContextCompat.getDrawable(context, R.drawable.ic_widget_no_preview)

        Glide
            .with(context)
            .load(items[position].icon)
            .centerCrop()
            .placeholder(placeholder)
            .into(holder.widgetPreview)

        holder.widgetName.text = items[position].widgetName
    }

    override fun getItemCount(): Int = items.size
}
