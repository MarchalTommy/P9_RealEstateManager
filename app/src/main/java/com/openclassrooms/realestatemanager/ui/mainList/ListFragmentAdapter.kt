package com.openclassrooms.realestatemanager.ui.mainList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress

class ListFragmentAdapter(private val dataSet: List<HouseAndAddress>,
                          private val twoPane: Boolean) :
        RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]
        holder.houseType.text = currentItem.house.type

        holder.houseLocation.text = currentItem.address.city

        holder.housePrice.text = "$${currentItem.house.price}"

        //Vérifier mais peut être ok, je sais pas trop...
        Glide.with(holder.itemView)
                .load(currentItem.house.pictureURL)
                .into(holder.housePic)

        // Kinda works, but is shit.
        holder.itemView.setOnClickListener {

            if (twoPane) {
//                holder.itemView.findNavController().navigate(actionTwoPane)
            } else {
//                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount() = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val houseType: TextView
        val housePrice: TextView
        val houseLocation: TextView
        val housePic: ImageView

        init {

            houseType = view.findViewById(R.id.house_type)
            housePrice = view.findViewById(R.id.house_price)
            houseLocation = view.findViewById(R.id.house_location)
            housePic = view.findViewById(R.id.house_pic)
        }

    }
}