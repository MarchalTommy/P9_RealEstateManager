package com.openclassrooms.realestatemanager.ui.mainList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress

class ListFragmentAdapter(
    var dataSet: List<HouseAndAddress>,
    private val onClick: (Int) -> Unit
) :
    RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    // TODO : voir avec Virgile, compatibilit√© prix EUR ?
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEstate = dataSet[position].house
        val currentAddress = dataSet[position].address
        holder.houseType.text = currentEstate.type

        holder.houseLocation.text = currentAddress.city

        holder.housePrice.text = currentEstate.currencyFormatUS()

        Glide.with(holder.itemView)
            .load(currentEstate.mainUri)
            .into(holder.housePic)

        if (!currentEstate.stillAvailable) {
            holder.soldBanner.visibility = View.VISIBLE
            holder.soldText.visibility = View.VISIBLE
            holder.soldDate.visibility = View.VISIBLE
            holder.soldDate.text = "Sold the ${currentEstate.dateSell}"
        } else {
            holder.soldBanner.visibility = View.GONE
            holder.soldText.visibility = View.GONE
            holder.soldDate.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClick(currentEstate.houseId)
        }
    }

    override fun getItemCount() = dataSet.size

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