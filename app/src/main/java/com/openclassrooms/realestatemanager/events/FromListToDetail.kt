package com.openclassrooms.realestatemanager.events

import com.openclassrooms.realestatemanager.database.entities.relations.HouseAndAddress

class FromListToDetail(houseAndAddress: HouseAndAddress) {

    var house: HouseAndAddress = houseAndAddress
}