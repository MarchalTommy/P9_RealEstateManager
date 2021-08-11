package com.openclassrooms.realestatemanager.ui.detail

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R

class DataDetailAdapter(
    private val dataSet: List<Int>,
    private val drawables: List<Drawable>
) :
    RecyclerView.Adapter<DataDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.model_detail_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.dataText.text = "${dataSet[position]} sq.m"
                holder.dataText.setCompoundDrawablesWithIntrinsicBounds(drawables[position], null, null, null)
            }
            1 -> {
                holder.dataText.text = "${dataSet[position]} rooms"
                holder.dataText.setCompoundDrawablesWithIntrinsicBounds(drawables[position], null, null, null)
            }
            2 -> {
                holder.dataText.text = "${dataSet[position]} bedrooms"
                holder.dataText.setCompoundDrawablesWithIntrinsicBounds(drawables[position], null, null, null)
            }
            3 -> {
                holder.dataText.text = "${dataSet[position]} bathrooms"
                holder.dataText.setCompoundDrawablesWithIntrinsicBounds(drawables[position], null, null, null)
            }
        }
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dataText: TextView = view.findViewById(R.id.data_textview)
    }
}