package com.example.kotlinassignment.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.kotlinassignment.DB.AppDatabase
import com.example.kotlinassignment.DataModel.AssignmentModel


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
class AssignmentViewModel(application: Application) : AndroidViewModel(application) {
    private val db: AppDatabase = AppDatabase.getInstance(application)
    internal val allAssignmentData: LiveData<List<AssignmentModel>> = db.assignmentDao().liveData()

    fun insert(assignmentModel: AssignmentModel) {
        db.assignmentDao().insert(assignmentModel)
    }
}