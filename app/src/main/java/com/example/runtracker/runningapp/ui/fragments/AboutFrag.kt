package com.example.runtracker.runningapp.ui.fragments

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.fragment.app.Fragment
import com.example.runtracker.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFrag:Fragment(R.layout.fragment_about) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAbout.movementMethod = ScrollingMovementMethod()
    }
}