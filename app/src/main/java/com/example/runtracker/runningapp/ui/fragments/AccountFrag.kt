package com.example.runtracker.runningapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.common.Constants
import com.example.runtracker.runningapp.viewmodels.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_ask_details.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class AccountFrag:Fragment(R.layout.fragment_account) {


    private val viewModel:StatsViewModel by viewModels()

    @Inject
    lateinit var sharedPref:SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataTV()
        observeValues()
        setUpClickListeners()

    }

    private fun observeValues()
    {
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val distance = it/1000f
                val distKM = round(distance*10f)/10f
                val s = "$distKM Km"
                tvDistanceVal.text = s
            }
        })
        viewModel.totalCaloriesBurnt.observe(viewLifecycleOwner, Observer {
            it?.let {
                val cal = "$it Kcal"
                tvCaloriesVal.text = cal
            }
        })
    }
    private fun loadDataTV()
    {
        val name = sharedPref.getString(Constants.KEY_NAME,"")
        val weight = sharedPref.getFloat(Constants.KEY_WEIGHT,80f)
        tvNameStr.text = name
        val w = "$weight Kg"
        tvWeightVal.text = w
    }
    private fun loadDataET()
    {
        val name = sharedPref.getString(Constants.KEY_NAME,"")
        val weight = sharedPref.getFloat(Constants.KEY_WEIGHT,80f)
        etNameStr.hint = name
        val w = "$weight"
        etWeightVal.hint = w
    }

    private fun setUpClickListeners()
    {
        ibEditName.setOnClickListener {
            clTextViewName.visibility = View.GONE
            clEditTextName.visibility = View.VISIBLE
            etNameStr.requestFocus()
            loadDataET()
        }
        ibEditNameOK.setOnClickListener {
            clTextViewName.visibility = View.VISIBLE
            clEditTextName.visibility = View.GONE
            changeName()
            loadDataTV()
        }
        ibEditWeight.setOnClickListener {
            clTextViewWeight.visibility = View.GONE
            clEditTextWeight.visibility = View.VISIBLE
            etWeightVal.requestFocus()
            loadDataET()
        }
        ibEditWeightOK.setOnClickListener {
            clTextViewWeight.visibility = View.VISIBLE
            clEditTextWeight.visibility = View.GONE
            changeWeight()
            loadDataTV()
        }
    }

    private fun changeName()
    {
        val name = etNameStr.text.toString()
        if(name.isEmpty()){
            Toast.makeText(activity,"Invalid name!",Toast.LENGTH_SHORT).show()

        }else{
            sharedPref.edit()
                .putString(Constants.KEY_NAME,name)
                .apply()

        }
    }

    private fun changeWeight()
    {
        val weight = etWeightVal.text.toString()
        if(weight.isEmpty()){
            Toast.makeText(activity,"Invalid weight!",Toast.LENGTH_SHORT).show()

        }else{
            sharedPref.edit()
                .putFloat(Constants.KEY_WEIGHT,weight.toFloat())
                .apply()

        }
    }
}