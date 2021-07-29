package com.openclassrooms.realestatemanager.epoxy

import android.content.Context
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.ModelDetailSizeBinding

class DetailDataListEpoxyController(val context: Context) : EpoxyController() {

    var house: House? = null
        set(value) {
            field = value
            if (field != null) {
                requestModelBuild()
            }
        }

    override fun buildModels() {
        if (house == null) {
            return
        }

        val test = listOf(
            DataCarouselModel(
                R.drawable.ic_surface,
                "${house!!.size} m²"
            ).id("size"),
            DataCarouselModel(
                R.drawable.ic_room,
                house!!.nbrRooms.toString()
            ).id("roomNbr"),
            DataCarouselModel(
                R.drawable.ic_bathroom,
                (house!!.nbrRooms / 4).toString()
            ).id("bathroomNbr"),
            DataCarouselModel(
                R.drawable.ic_bedroom,
                (house!!.nbrRooms / 3).toString()
            ).id("bedroomNbr"),
        )

        CarouselModel_()
            .models(test)
            .id("dataCarousel")
            .numViewsToShowOnScreen(2.75f)
            .addTo(this)

    }

    data class DataCarouselModel(
        val drawable: Int,
        val text: String
    ) : ViewBindingKotlinModel<ModelDetailSizeBinding>(R.layout.model_detail_size) {
        override fun ModelDetailSizeBinding.bind() {
            modelSize.text = text
            modelSize.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
        }
    }

}