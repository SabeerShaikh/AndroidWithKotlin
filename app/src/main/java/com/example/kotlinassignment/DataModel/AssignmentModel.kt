package com.example.kotlinassignment.DataModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Sabeer Shaikh on 12/09/2019.
 *
 */
@Entity(tableName = "KotlinAssignment")
data class AssignmentModel(
    @PrimaryKey
    @ColumnInfo(name = "Title")
    var title: String,
    @ColumnInfo(name = "Description")
    var description: String,
    @ColumnInfo(name = "ImageHref")
    var imageHref: String
)