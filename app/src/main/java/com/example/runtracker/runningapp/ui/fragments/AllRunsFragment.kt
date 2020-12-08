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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_run.*
import javax.annotation.meta.When

@AndroidEntryPoint
class AllRunsFragment : Fragment(R.layout.fragment_all_runs) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter : RunsRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        viewModel.sortRuns(SortType.ALL)
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