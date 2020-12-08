package com.example.runtracker.common

import android.graphics.Color
import com.example.runtracker.R

object Constants {
    const val RUN_DATABASE_NAME = "run_db"
    const val LOCATION_PERMISSION_REQUEST_CODE = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"
    const val NOTIFICATION_CHANNEL_NAME = "notification_channel_name"
    const val NOTIFICATION_ID = 1

    const val TRACK_COLOR = R.color.red
    const val TRACK_WIDTH = 8f
    const val MAP_ZOOM = 18f
    const val TIMER_UPDATE_INTERVAL = 50L

    const val KEY_SHARED_PREFERNCES = "KEY_SHARED_PREFERNCES"
    const val KEY_NAME = "KEY_NAME"
    const val KEY_WEIGHT = "KEY_WEIGHT"
    const val KEY_FIRST_TIME = "KEY_FIRST_TIME"

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val LOCATION_FASTEST_INTERVAL = 2000L

    const val SONGS_LIST = "SONGS_LIST"
    const val SONG_NAME = "SONG_NAME"
    const val SONG_POSITION = "SONG_POSITION"
}