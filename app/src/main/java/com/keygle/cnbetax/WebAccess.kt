package com.keygle.cnbetax

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object WebAccess{
    val api : ApiClient by lazy{
        Log.d("WebAccess", "Creating retrofit client")
        val retrofit = Retrofit.Builder()
            // The 10.0.2.2 address routes request from the Android emulator
            // to the localhost / 127.0.0.1 of the host PC
            .baseUrl("https://ex.gressx.com/")
            // Moshi maps JSON to classes
            .addConverterFactory(MoshiConverterFactory.create())
            // The call adapter handles threads
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        // Create Retrofit client
        return@lazy retrofit.create(ApiClient::class.java)
    }

}