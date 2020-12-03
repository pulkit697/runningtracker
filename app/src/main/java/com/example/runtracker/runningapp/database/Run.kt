package com.example.runtracker.runningapp.database

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class Run(
    var image:Bitmap?=null,
    var datetimestamp: Long = 0L,
    var avgspeedkmph: Float = 0f,
    var distanceMeters : Int = 0,
    var timeMilliSec : Long = 0L,
    var calories : Int = 0
){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null
}