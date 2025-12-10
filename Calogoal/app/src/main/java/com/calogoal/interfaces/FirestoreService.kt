package com.calogoal.interfaces

import com.calogoal.viewmodels.ProfileUiState
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    fun setUserData(data: Map<String, Any>);
    fun updateUserData(data: Map<String, Any>);
    suspend fun getName(): String;
    suspend fun getProfile(): ProfileUiState
}