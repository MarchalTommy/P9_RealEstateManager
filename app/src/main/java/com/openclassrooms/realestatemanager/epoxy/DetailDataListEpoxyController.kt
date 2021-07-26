package com.openclassrooms.realestatemanager.epoxy

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.database.entities.House
import com.openclassrooms.realestatemanager.databinding.ModelDetailBathroomsBinding
import com.openclassrooms.realestatemanager.databinding.ModelDetailBedroomBinding
import com.openclassrooms.realestatemanager.databinding.ModelDetailRoomBinding
import com.openclassrooms.realestatemanager.databinding.ModelDetailSizeBinding

class DetailDataListEpoxyController : EpoxyController() {

    var house: House? = null
        set(value) {
            field = value
            if (field != null) {
                requestModelBuild()
            }
        }

    override fun buildModels() {
        if(house == null) {
            return
        }

        SizeEpoxyModel(
                size = house!!.size.toString()
        )

        RoomsEpoxyModel(
                numbers = house!!.nbrRooms.toString()
        )

        BathroomsEpoxyModel(
                numbers = (house!!.nbrRooms/4).toString()
        )

        BedroomsEpoxyModel(
                numbers = (house!!.nbrRooms/3).toString()
        )


    }

    data class SizeEpoxyModel(
            val size: String
    ) : ViewBindingKotlinModel<ModelDetailSizeBinding>(R.layout.model_detail_size) {
        override fun ModelDetailSizeBinding.bind() {
            modelSize.text = size
        }
    }

    data class BathroomsEpoxyModel(
            val numbers: String
    ) : ViewBindingKotlinModel<ModelDetailBathroomsBinding>(R.layout.model_detail_bathrooms) {
        override fun ModelDetailBathroomsBinding.bind() {
            modelBathroom.text = numbers
        }
    }

    data class RoomsEpoxyModel(
            val numbers: String
    ) : ViewBindingKotlinModel<ModelDetailRoomBinding>(R.layout.model_detail_room) {
        override fun ModelDetailRoomBinding.bind() {
            modelRoom.text = numbers
        }
    }

    data class BedroomsEpoxyModel(
            val numbers: String
    ) : ViewBindingKotlinModel<ModelDetailBedroomBinding>(R.layout.model_detail_bedroom) {
        override fun ModelDetailBedroomBinding.bind() {
            modelBedroom.text = numbers
        }
    }

}