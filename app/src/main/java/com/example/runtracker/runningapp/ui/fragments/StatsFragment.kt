package com.example.runtracker.runningapp.ui.fragments


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.utils.HyperSpline
import androidx.core.content.ContextCompat
import androidx.core.graphics.alpha
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.common.Utility
import com.example.runtracker.runningapp.viewmodels.StatsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlin.math.round

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_statistics) {

    private val viewModel: StatsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBarGraph()
        observeValues()
    }

    private fun setUpBarGraph()
    {
        bcBarChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = R.color.dark_gray
            textColor = R.color.black
            setDrawGridLines(false)
        }
        bcBarChart.axisLeft.apply {
            axisLineColor = R.color.dark_gray
            textColor = R.color.black
            setDrawGridLines(false)
        }
        bcBarChart.apply {
            description.text = "Distance Run Over Time"
            legend.isEnabled = false

        }
    }

    fun observeValues()
    {
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val dist = it/1000f
                val distKm = round(dist*10f)/10f
                val dis = "${dist}km"
                tvTotalDist.text = dis
            }
        })
        viewModel.totalTimeInMillis.observe(viewLifecycleOwner, Observer {
            it?.let {
                val time = Utility.formattedString(it)
                tvTotalTime.text = time
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val speed = round(it*10f)/10f
                val speedStr = "${speed}Km/hr"
                tvAvgSpeed.text = speedStr
            }
        })
        viewModel.totalCaloriesBurnt.observe(viewLifecycleOwner, Observer {
            it?.let {
                val cal = "${it}Kcal"
                tvTotalCal.text = cal
            }
        })
        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allDistances = it.indices.map { i-> BarEntry(i.toFloat(),it[i].distanceMeters.toFloat()) }
                val barDataSet = LineDataSet(allDistances,"Distance Run Over Time").apply {
                    valueTextColor = R.color.dark_gray
                    color = R.color.light_gray
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setCircleColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
                }
                bcBarChart.data = LineData(barDataSet)
                bcBarChart.invalidate()
            }
        })
    }

}