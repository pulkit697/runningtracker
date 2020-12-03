package com.example.runtracker.runningapp.repositories

import com.example.runtracker.runningapp.database.Run
import com.example.runtracker.runningapp.database.RunDao
import javax.inject.Inject

class MainRepo @Inject constructor(val runDao: RunDao) {

    suspend fun insertRun(run: Run) = runDao.insert(run)

    suspend fun deleteRun(run: Run) = runDao.delete(run)

    fun sortByDate() = runDao.sortByDate()

    fun sortByDateAsc() = runDao.sortByDateAsc()

    fun sortByCalories() = runDao.sortByCalories()

    fun sortByTime() = runDao.sortByTime()

    fun sortByDistance() = runDao.sortByDistance()

    fun sortBySpeed() = runDao.sortBySpeed()

    fun getTotalDistance() = runDao.getTotalDistance()

    fun getTotalTime() = runDao.getTotalTime()

    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()

    fun getTotalCalories() = runDao.getTotalCalories()

}
