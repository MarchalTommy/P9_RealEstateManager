package com.openclassrooms.realestatemanager.database

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.realestatemanager.database.entities.Address
import com.openclassrooms.realestatemanager.database.entities.House

class FirestoreHelper {

    private val COLLECTION_HOUSE = "houses"
    private val COLLECTION_AGENT = "agents"
    private val COLLECTION_ADDRESS = "addresses"

    // COLLECTIONS
    private fun getHousesCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_HOUSE)
    }

    private fun getAgentsCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_AGENT)
    }

    private fun getAddressesCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(COLLECTION_ADDRESS)
    }

    // GET BY ID
    fun getHouseById(houseId: String): Task<DocumentSnapshot> {
        return getHousesCollection()
            .document(houseId)
            .get()
    }

    fun getAgentById(agentId: Int): Task<DocumentSnapshot> {
        return getAgentsCollection()
            .document(agentId.toString())
            .get()
    }

    fun getAddressById(addressId: Int): Task<DocumentSnapshot> {
        return getAddressesCollection()
            .document(addressId.toString())
            .get()
    }

    // GET ALL
    fun getAllHouses(): Task<QuerySnapshot> {
        return getHousesCollection()
            .orderBy("dateEntry")
            .get()
    }

    fun getAllAgents(): Task<QuerySnapshot> {
        return getAgentsCollection()
            .get()
    }

    fun getAllAddresses(): Task<QuerySnapshot> {
        return getAddressesCollection()
            .get()
    }

    // UPDATE
    fun updateDescription(houseId: String, description: String): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("description", description)
    }

    fun updatePrice(houseId: String, price: Int): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("price", price)
    }

    fun updateStillAvailable(houseId: String, available: Boolean): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("stillAvailable", available)
    }

    fun updateDateSold(houseId: String, dateSold: String): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("dateSold", dateSold)
    }

    fun updateType(houseId: String, type: String): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("type", type)
    }

    fun updateSize(houseId: String, size: Int): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("size", size)
    }

    fun updateNbrRooms(houseId: String, nbrRooms: Int): Task<Void> {
        return getHousesCollection()
            .document(houseId)
            .update("nbrRooms", nbrRooms)
    }

    //TODO : update Pictures (need to implement pictures before)

    // INSERT
    fun insertHouse(
        id: Int,
        price: Int,
        type: String,
        size: Int,
        nbrRooms: Int,
        nbrBedrooms: Int,
        nbrBathrooms: Int,
        description: String,
        stillAvailable: Boolean,
        dateEntry: String,
        dateSold: String,
        agentId: Int,
        addressId: Int
    ): Task<Void> {

        val house = House(
            id, price, type, size, nbrRooms, nbrBedrooms, nbrBathrooms, description,
            stillAvailable, dateEntry, dateSold, agentId, addressId
        )

        return getHousesCollection()
            .document(id.toString())
            .set(house)
    }

    fun insertAddress(
        id: Int,
        way: String,
        complement: String,
        zip: Int,
        city: String,
        houseId: Int
    )
            : Task<Void> {

        val address = Address(id, way, complement, zip, city, houseId)

        return getAddressesCollection()
            .document(id.toString())
            .set(address)
    }

}