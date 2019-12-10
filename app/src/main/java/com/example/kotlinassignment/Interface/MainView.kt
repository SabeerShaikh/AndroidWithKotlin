package com.example.kotlinassignment.Interface

import android.os.Bundle
import com.example.kotlinassignment.DataModel.AssignmentModel


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
interface MainView {
    fun onGetDataSuccess(list: List<AssignmentModel>)

    fun onGetDataFailure(message: String)

    fun showProgress()

    fun hideProgress()

    fun setMainTitle()

    fun onRestoreInstanceState(savedInstanceState: Bundle)
}