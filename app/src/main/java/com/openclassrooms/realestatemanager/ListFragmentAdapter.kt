package com.openclassrooms.realestatemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.entities.relations.HouseAndAddress
import com.openclassrooms.realestatemanager.events.FromListToDetail
import org.greenrobot.eventbus.EventBus

class ListFragmentAdapter(private val dataSet: List<HouseAndAddress>) : RecyclerView.Adapter<ListFragmentAdapter.ViewHolder>() {

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

        holder.itemView.setOnClickListener(View.OnClickListener {
        EventBus.getDefault().post(FromListToDetail(dataSet[position]))
        })
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