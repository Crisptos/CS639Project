package com.calogoal.hiltmod

import com.calogoal.interfaces.FirestoreService
import com.calogoal.services.FirestoreServiceImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestoreService(): FirestoreService {
        return FirestoreServiceImpl(
            db = FirebaseFirestore.getInstance(),
            auth = FirebaseAuth.getInstance()
        )
    }
}