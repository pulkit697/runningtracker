package com.example.runtracker.runningapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.runtracker.R
import com.example.runtracker.common.Constants.KEY_FIRST_TIME
import com.example.runtracker.common.Constants.KEY_NAME
import com.example.runtracker.common.Constants.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_ask_details.*
import javax.inject.Inject

@AndroidEntryPoint
class AskDetailsFrag : Fragment(R.layout.fragment_ask_details) {

    @Inject
    lateinit var sharedPref : SharedPreferences

    @set:Inject
    var isFirstTimeAppOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if(!isFirstTimeAppOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.askDetailsFrag,true)
                .build()
            findNavController().navigate(R.id.action_askDetailsFrag_to_trackingFragment,savedInstanceState,navOptions)
        }

        btGetStarted.setOnClickListener {
            val success = saveInSharedPreferences()
            if(success) {
                view.findNavController().navigate(R.id.action_askDetailsFrag_to_trackingFragment)
            }else{
                Snackbar.make(requireActivity().rootView,"Please enter correct data",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun saveInSharedPreferences():Boolean
    {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()

        if(name.isEmpty() || weight.isEmpty())
            return false

        sharedPref.edit()
            .putString(KEY_NAME,name)
            .putFloat(KEY_WEIGHT,weight.toFloat())
            .putBoolean(KEY_FIRST_TIME,false)
            .apply()

        return true
    }

}