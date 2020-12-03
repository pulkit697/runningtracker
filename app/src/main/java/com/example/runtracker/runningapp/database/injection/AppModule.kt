package com.example.runtracker.runningapp.database.injection

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runtracker.common.Constants.KEY_FIRST_TIME
import com.example.runtracker.common.Constants.KEY_NAME
import com.example.runtracker.common.Constants.KEY_SHARED_PREFERNCES
import com.example.runtracker.common.Constants.KEY_WEIGHT
import com.example.runtracker.common.Constants.RUN_DATABASE_NAME
import com.example.runtracker.runningapp.database.RunDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context:Context
    ) = Room.databaseBuilder(context, RunDatabase::class.java,RUN_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDao(db : RunDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context:Context) = context.getSharedPreferences(
        KEY_SHARED_PREFERNCES,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref: SharedPreferences) = sharedPref.getString(KEY_NAME,"")?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref: SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref: SharedPreferences) = sharedPref.getBoolean(KEY_FIRST_TIME,true)
}