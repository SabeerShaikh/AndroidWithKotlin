package com.example.kotlinassignment.Interface

import com.example.kotlinassignment.DataModel.AssignmentModel


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
interface GetDataListener {
    fun onSuccess(message: String, list: List<AssignmentModel>)

    fun onFailure(message: String)
}