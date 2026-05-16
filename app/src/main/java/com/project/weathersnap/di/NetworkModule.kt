package com.project.weathersnap.di

import com.project.weathersnap.data.remote.GeocodingApiService
import com.project.weathersnap.data.remote.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

// 1. Define custom Qualifiers to distinguish between the two base URLs
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 2. Provide the OkHttpClient with the required Logging Interceptor
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Logs request and response lines and their respective headers and bodies
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // 3. Provide the Retrofit instance for Weather Data
    @Provides
    @Singleton
    @WeatherRetrofit
    fun provideWeatherRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 4. Provide the Retrofit instance for City Geocoding
    @Provides
    @Singleton
    @GeocodingRetrofit
    fun provideGeocodingRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://geocoding-api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 5. Provide the actual API Services that your Repositories will inject
    @Provides
    @Singleton
    fun provideWeatherApiService(@WeatherRetrofit retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingApiService(@GeocodingRetrofit retrofit: Retrofit): GeocodingApiService {
        return retrofit.create(GeocodingApiService::class.java)
    }
}