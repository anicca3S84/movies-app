package com.codework.movies_app.di

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.codework.movies_app.firebase.FirebaseCommon
import com.codework.movies_app.utils.Constants
import com.codework.movies_app.utils.Constants.INTRODUCTION_SP
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFireStoreDatabase(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideIntroductionSP(application: Application): SharedPreferences {
        return application.getSharedPreferences(Constants.INTRODUCTION_SP, Context.MODE_PRIVATE)
    }


    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseCommon = FirebaseCommon(firebaseAuth, firestore)
}
