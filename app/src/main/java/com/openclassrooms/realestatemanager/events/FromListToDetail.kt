package com.openclassrooms.realestatemanager.events

import com.openclassrooms.realestatemanager.entities.relations.HouseAndAddress

class FromListToDetail(houseAndAddress: HouseAndAddress) {

    var house: HouseAndAddress = houseAndAddress
}