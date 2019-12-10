package com.example.kotlinassignment.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kotlinassignment.DataModel.AssignmentModel


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
@Database(entities = arrayOf(AssignmentModel::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "Example"
                ).build()
            }

            return INSTANCE as AppDatabase
        }
    }
}