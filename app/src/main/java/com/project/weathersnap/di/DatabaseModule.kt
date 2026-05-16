package com.project.weathersnap.di

import android.content.Context
import androidx.room.Room
import com.project.weathersnap.data.local.AppDatabase
import com.project.weathersnap.data.local.ReportDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weathersnap_database"
        ).build()
    }

    @Provides
    fun provideReportDao(database: AppDatabase): ReportDao {
        return database.reportDao()
    }
}