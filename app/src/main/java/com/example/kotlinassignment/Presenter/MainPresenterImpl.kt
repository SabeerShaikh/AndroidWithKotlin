package com.example.kotlinassignment.Presenter

import android.content.Context
import com.example.kotlinassignment.DataModel.AssignmentDataManager
import com.example.kotlinassignment.DataModel.AssignmentModel
import com.example.kotlinassignment.Interface.GetDataListener
import com.example.kotlinassignment.Interface.MainInteractor
import com.example.kotlinassignment.Interface.MainPresenter
import com.example.kotlinassignment.Interface.MainView


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
class MainPresenterImpl(mMainView: MainView) : MainPresenter, GetDataListener {

    private var mMainView: MainView? = null
    private var mInteractor: MainInteractor? = null

    // Initializer Block
    init {
        this.mMainView = mMainView
        this.mInteractor = MainInteractorImpl(this)
    }


    fun getMainView(): MainView? {
        return mMainView
    }

    override fun getDataForList(context: Context, isRestoring: Boolean) {

        // get this done by the interactor
        mMainView!!.showProgress()
        mInteractor!!.provideData(context, isRestoring)

    }

    override fun onDestroy() {

        mInteractor!!.onDestroy()
        if (mMainView != null) {
            mMainView!!.hideProgress()
            mMainView = null
        }
    }

    override fun onSuccess(message: String, list: List<AssignmentModel>) {

        // updating cache copy of data for restoring purpose
        AssignmentDataManager().setLatestData(list)


        if (mMainView != null) {
            mMainView!!.setMainTitle()
            mMainView!!.hideProgress()
            mMainView!!.onGetDataSuccess(list)
        }
    }

    override fun onFailure(message: String) {
        if (mMainView != null) {
            mMainView!!.hideProgress()
            mMainView!!.onGetDataFailure(message)
        }

    }
}