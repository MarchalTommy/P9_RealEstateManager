package com.openclassrooms.realestatemanager.ui

import android.app.Fragment
import android.os.Bundle
import android.view.View
import com.openclassrooms.realestatemanager.database.EstateDatabase

open class BaseFragment : Fragment() {

    protected val mainActivity: MainActivity
        get() = activity as MainActivity

    protected val database: EstateDatabase
        get() = EstateDatabase.getInstance(mainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}