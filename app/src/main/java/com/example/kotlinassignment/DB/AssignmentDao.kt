package com.example.kotlinassignment.DB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kotlinassignment.DataModel.AssignmentModel


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
@Dao
interface AssignmentDao {
    @Query("SELECT * FROM KotlinAssignment")
    fun liveData(): LiveData<List<AssignmentModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(assignmentModel: AssignmentModel)
}