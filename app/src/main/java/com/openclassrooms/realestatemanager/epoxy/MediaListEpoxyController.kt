package com.openclassrooms.realestatemanager.epoxy

import android.view.View.GONE
import android.view.View.VISIBLE
import com.airbnb.epoxy.EpoxyController
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.Utils
import com.openclassrooms.realestatemanager.databinding.ModelDetailMediaBinding

class MediaListEpoxyController : EpoxyController() {

    var photos: List<String>? = null
        set(value) {
            field = value
            if (field != null) {
                requestModelBuild()
            }
        }


    override fun buildModels() {
        TODO("Not yet implemented")
    }

    data class PhotoEpoxyModel(
            val name: String,
            val imageUrl: String
    ) : ViewBindingKotlinModel<ModelDetailMediaBinding>(R.layout.model_detail_media) {
        override fun ModelDetailMediaBinding.bind() {
            if (Utils.isLandscape(root.context)) {
                mediaPortraitTxt.visibility = GONE
                mediaPicPortrait.visibility = GONE

                mediaLandTxt.visibility = VISIBLE
                mediaPicLandscape.visibility = VISIBLE

                mediaLandTxt.text = name
                Glide.with(root)
                        .load(imageUrl)
                        .into(mediaPicPortrait)
            } else {
                mediaLandTxt.visibility = GONE
                mediaPicLandscape.visibility = GONE

                mediaPortraitTxt.visibility = VISIBLE
                mediaPicPortrait.visibility = VISIBLE

                mediaPortraitTxt.text = name
                Glide.with(root)
                        .load(imageUrl)
                        .into(mediaPicPortrait)
            }
        }
    }

}