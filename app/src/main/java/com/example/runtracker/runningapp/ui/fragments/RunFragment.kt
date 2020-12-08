package com.example.runtracker.runningapp.ui.fragments


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.common.SortType
import com.example.runtracker.runningapp.adapter.RunsRVAdapter
import com.example.runtracker.runningapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import javax.annotation.meta.When

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter : RunsRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        when(viewModel.sortType){
            SortType.ALL -> spSort.setSelection(0)
            SortType.DATE -> spSort.setSelection(0)
            SortType.RUNNING_TIME -> spSort.setSelection(1)
            SortType.DISTANCE -> spSort.setSelection(2)
            SortType.SPEED -> spSort.setSelection(3)
            SortType.CALORIES_BURNT -> spSort.setSelection(4)
        }

        spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNT)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

    }

    fun setUpRecyclerView()
    {
        adapter = RunsRVAdapter()
        rvRuns.adapter = adapter
    }
}