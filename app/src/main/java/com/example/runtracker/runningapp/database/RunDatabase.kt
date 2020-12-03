package com.example.runtracker.runningapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunDatabase:RoomDatabase() {

    abstract fun getRunDao():RunDao
}