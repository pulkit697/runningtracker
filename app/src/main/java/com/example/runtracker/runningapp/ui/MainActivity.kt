package com.example.runtracker.runningapp.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runtracker.R
import com.example.runtracker.common.Constants
import com.example.runtracker.musicplayer.MusicMainActivity
import com.example.runtracker.runningapp.database.RunDao
import com.google.android.material.navigation.NavigationView
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    @Inject
    lateinit var runDao: RunDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(action_bar_main)
        navigateToTrackingFragmentIfNeeded(intent)
        Timber.d("run database dao : ${runDao.hashCode()}")
//
//        NavigationUI.setupWithNavController(navViewDrawer,findNavController(R.id.navViewDrawer))
        bottom_nav_main.setupWithNavController(navHostFragment.findNavController())

        bottom_nav_main.setOnNavigationItemReselectedListener { /** NO-OP **/ }

        navHostFragment.findNavController()
            .addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id)
                {
                    R.id.askDetailsFrag -> {
                        bottom_nav_main.visibility = View.GONE
                        action_bar_main.title = resources.getString(R.string.app_name)
                    }
                    R.id.accountFrag->{
                        bottom_nav_main.visibility = View.GONE
                        action_bar_main.title = "Account"
                    }
                    R.id.aboutFrag->{
                        bottom_nav_main.visibility = View.GONE
                        action_bar_main.title = ""
                    }
                    R.id.allRunsFragment->{
                        bottom_nav_main.visibility = View.GONE
                        action_bar_main.title = "All Runs"
                    }
                    else -> {
                        bottom_nav_main.visibility = View.VISIBLE
                        action_bar_main.title = resources.getString(R.string.app_name)
                    }
                }
            }

    }

    override fun onNewIntent(intent: Intent?) {
        navigateToTrackingFragmentIfNeeded(intent)
        super.onNewIntent(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?)
    {
        if(intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT)
        {
            navHostFragment.findNavController().navigate(R.id.action_global_tracking_fragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_bar,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {

        R.id.play_music_main -> {
//            MDToast.makeText(this,"Button clicked",MDToast.LENGTH_SHORT,MDToast.TYPE_INFO).show()
            Intent(this,MusicMainActivity::class.java).also { startActivity(it) }
            true
        }
        else-> {
//            MDToast.makeText(this,"function called",MDToast.LENGTH_SHORT,MDToast.TYPE_WARNING).show()
            super.onOptionsItemSelected(item)
        }
    }

}