package com.example.runtracker.runningapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.runtracker.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment:Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvOpenAbout.setOnClickListener{
            navHostFragment.findNavController().navigate(R.id.action_settingsFragment_to_aboutFrag)
        }
        tvOpenAccount.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.action_settingsFragment_to_accountFrag)
        }
        tvOpenRuns.setOnClickListener {
            navHostFragment.findNavController().navigate(R.id.action_settingsFragment_to_allRunsFragment)
        }

    }
}