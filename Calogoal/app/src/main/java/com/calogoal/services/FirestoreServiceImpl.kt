package com.calogoal.services

import com.calogoal.interfaces.FirestoreService
import com.calogoal.models.dtos.ProfileDTO
import com.calogoal.viewmodels.ProfileUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FirestoreService {
    private val userId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

    override fun setUserData(data: Map<String, Any>) {
        db.collection("users").document(userId)
            .set(data)
    }

    override fun updateUserData(data: Map<String, Any>) {
        db.collection("users").document(userId)
            .update(data)
    }

    override suspend fun getName(): String {
        val snapshot = db.collection("users").document(userId).get().await()
        return snapshot.getString("name") ?: "Unknown User"
    }

    override suspend fun getProfile(): ProfileDTO {
        val snapshot = db.collection("users").document(userId).get().await()
        return snapshot.toObject<ProfileDTO>()?: ProfileDTO()
    }

}