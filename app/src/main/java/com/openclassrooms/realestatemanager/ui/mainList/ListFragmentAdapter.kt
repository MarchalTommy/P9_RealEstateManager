package com.openclassrooms.realestatemanager.ui.mainList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress

class ListFragmentAdapter(
    val dataSet: List<HouseAndAddress>,
    private val onClick: (Int) -> Unit
) :
    RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    // TODO : voir avec Virgile, compatibilité prix EUR ?
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.houseType.text = currentItem.house.type

        holder.houseLocation.text = currentItem.address.city

        holder.housePrice.text = currentItem.house.currencyFormatUS()

        Glide.with(holder.itemView)
            .load(currentItem.house.mainUri)
            .into(holder.housePic)

        if (!currentItem.house.stillAvailable) {
            holder.soldBanner.visibility = View.VISIBLE
            holder.soldText.visibility = View.VISIBLE
            holder.soldDate.visibility = View.VISIBLE
            holder.soldDate.text = "Sold the ${Utils.getTodayDate()}"
        } else {
            holder.soldBanner.visibility = View.GONE
            holder.soldText.visibility = View.GONE
            holder.soldDate.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
                onClick(currentItem.house.houseId)
        }
    }

    override fun getItemCount() = dataSet.size

    fun getItemAt(position: Int): HouseAndAddress {
        return dataSet[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val houseType: TextView = view.findViewById(R.id.house_type)
        val housePrice: TextView = view.findViewById(R.id.house_price)
        val houseLocation: TextView = view.findViewById(R.id.house_location)
        val housePic: ImageView = view.findViewById(R.id.house_pic)
        val soldText: TextView = view.findViewById(R.id.sold_text_view)
        val soldBanner: View = view.findViewById(R.id.sold_banner)
        val soldDate: TextView = view.findViewById(R.id.date_sold)
    }
}