package com.example.kotlinassignment.DataModel

import java.util.*


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
class AssignmentDataManager {
    private var latestData: List<AssignmentModel>? = null

    init {
        latestData = ArrayList()
    }

    fun getLatestData(): List<AssignmentModel>? {
        return latestData
    }

    fun setLatestData(latestData: List<AssignmentModel>) {

        this.latestData = latestData
    }
}