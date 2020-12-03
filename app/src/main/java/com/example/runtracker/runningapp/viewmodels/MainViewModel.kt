package com.example.runtracker.runningapp.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runtracker.common.SortType
import com.example.runtracker.runningapp.database.Run
import com.example.runtracker.runningapp.repositories.MainRepo
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val mainRepo:MainRepo):ViewModel() {

    private var runsSortedByDate = mainRepo.sortByDate()
    private var runsSortedByDistance = mainRepo.sortByDistance()
    private var runsSortedBySpeed = mainRepo.sortBySpeed()
    private var runsSortedByCalories = mainRepo.sortByCalories()
    private var runsSortedByTime = mainRepo.sortByTime()

    var sortType = SortType.DATE

    var runs=MediatorLiveData<List<Run>>()

    init {
        runs.addSource(runsSortedByDate){result->
            if(sortType==SortType.DATE){
                result?.let { runs.value=it }
            }
        }
        runs.addSource(runsSortedByTime){result->
            if(sortType==SortType.RUNNING_TIME){
                result?.let { runs.value=it }
            }
        }
        runs.addSource(runsSortedByCalories){result->
            if(sortType==SortType.CALORIES_BURNT){
                result?.let { runs.value=it }
            }
        }
        runs.addSource(runsSortedByDistance){result->
            if(sortType==SortType.DISTANCE){
                result?.let { runs.value=it }
            }
        }
        runs.addSource(runsSortedBySpeed){result->
            if(sortType==SortType.SPEED){
                result?.let { runs.value=it }
            }
        }
    }

    fun sortRuns(sortType: SortType)=when(sortType){
        SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTime.value?.let { runs.value = it }
        SortType.CALORIES_BURNT -> runsSortedByCalories.value?.let { runs.value = it }
        SortType.SPEED -> runsSortedBySpeed.value?.let { runs.value = it }
    }

    fun insert(run:Run) = viewModelScope.launch {
        mainRepo.insertRun(run)
    }
}