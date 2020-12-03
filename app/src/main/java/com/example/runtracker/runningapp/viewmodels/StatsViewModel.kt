package com.example.runtracker.runningapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.runtracker.runningapp.repositories.MainRepo

class StatsViewModel @ViewModelInject constructor(val mainRepo:MainRepo):ViewModel() {

    val totalTimeInMillis = mainRepo.getTotalTime()
    val totalDistance = mainRepo.getTotalDistance()
    val totalAvgSpeed = mainRepo.getTotalAvgSpeed()
    val totalCaloriesBurnt = mainRepo.getTotalCalories()

    val runsSortedByDate = mainRepo.sortByDateAsc()


}