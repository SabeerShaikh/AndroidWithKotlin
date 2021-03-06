package com.example.kotlinassignment.Interface

import android.content.Context


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
interface MainPresenter {
    fun getDataForList(context: Context, isRestoring: Boolean)

    fun onDestroy()
}