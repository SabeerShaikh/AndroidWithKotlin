package com.example.kotlinassignment

import com.example.kotlinassignment.DataModel.AssignmentModel
import com.example.kotlinassignment.Interface.MainView
import com.example.kotlinassignment.Presenter.MainPresenterImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.ArrayList
import org.junit.Assert.assertNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
@RunWith(MockitoJUnitRunner::class)
class MainPresenterImplTest {
    @Mock
    internal var mainView: MainView? = null

    private var mainPresenter: MainPresenterImpl? = null

    @Before
    fun setUp() {
        mainPresenter = MainPresenterImpl(mainView!!)
    }

    @Test
    fun checkIfEQArePassedToView() {
        val test1 = mock<AssignmentModel>(AssignmentModel::class.java)
        val test2 = mock<AssignmentModel>(AssignmentModel::class.java)

        val list = ArrayList<AssignmentModel>(2)
        list.add(test1)
        list.add(test2)

        mainPresenter!!.onSuccess("success", list)
       verify<MainView>(mainView, times(1)).onGetDataSuccess(list)
        verify<MainView>(mainView, times(1)).hideProgress()

    }

    @Test
    fun checkIfViewIsReleasedOnStop() {
        mainPresenter!!.onDestroy()
        assertNull(mainPresenter!!.getMainView())
    }


}