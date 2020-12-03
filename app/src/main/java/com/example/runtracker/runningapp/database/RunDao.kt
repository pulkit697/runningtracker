package com.example.runtracker.runningapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: Run)

    @Delete
    suspend fun delete(run: Run)

    @Query("SELECT * FROM run_table ORDER BY datetimestamp DESC")
    fun sortByDate():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY datetimestamp")
    fun sortByDateAsc():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY timeMilliSec DESC")
    fun sortByTime():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY avgspeedkmph DESC")
    fun sortBySpeed():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY calories DESC")
    fun sortByCalories():LiveData<List<Run>>

    @Query("SELECT * FROM run_table ORDER BY distanceMeters DESC")
    fun sortByDistance():LiveData<List<Run>>

    @Query("SELECT SUM(timeMilliSec) FROM run_table")
    fun getTotalTime():LiveData<Long>

    @Query("SELECT SUM(distanceMeters) FROM run_table")
    fun getTotalDistance():LiveData<Long>

    @Query("SELECT AVG(avgspeedkmph) FROM run_table")
    fun getTotalAvgSpeed():LiveData<Long>

    @Query("SELECT SUM(calories) FROM run_table")
    fun getTotalCalories():LiveData<Long>

}