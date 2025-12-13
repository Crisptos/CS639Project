package com.calogoal.services

import com.calogoal.interfaces.FirestoreService
import com.calogoal.models.dtos.MealDTO
import com.calogoal.models.dtos.ProfileDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    override suspend fun setProfile(profile: ProfileDTO) {
        val userProfileRef = db
            .collection("users").document(userId)

        val dataToSave = mapOf(
            "name" to profile.name,
            "dateOfBirth" to profile.dateOfBirth,
            "age" to profile.age,
            "weight" to profile.weight,
            "height" to profile.height,
            "sex" to profile.sex
        )

        userProfileRef.set(dataToSave).await()
    }

    override suspend fun getMeals(): List<MealDTO> {
        val mealsCollectionRef = db
            .collection("users").document(userId)
            .collection("meals")

        val snapshot = mealsCollectionRef
            .get()
            .await()

        val mealsList = snapshot.documents.mapNotNull { documentSnapshot ->
            documentSnapshot.toObject<MealDTO>()
        }

        return mealsList
    }

    override suspend fun addMeals(meals: List<MealDTO>) {
        val batch = db.batch()

        val userMealsRef = db
            .collection("users").document(userId)
            .collection("meals")

        meals.forEach { meal ->
            val docRef = userMealsRef.document()

            val dataToSave = mapOf(
                "calories" to meal.calories,
                "carbs" to meal.carbs,
                "fat" to meal.fat,
                "label" to meal.label,
                "protein" to meal.protein,
                "mealType" to meal.mealType,
                "createdAt" to FieldValue.serverTimestamp()
            )

            batch.set(docRef, dataToSave)
        }

        batch.commit().await()
    }

    override suspend fun addMeal(meal: MealDTO) {
        val userMealsRef = db
            .collection("users").document(userId)
            .collection("meals")

        val dataToSave = mapOf(
            "calories" to meal.calories,
            "carbs" to meal.carbs,
            "fat" to meal.fat,
            "label" to meal.label,
            "protein" to meal.protein,
            "mealType" to meal.mealType,
            "createdAt" to FieldValue.serverTimestamp()
        )

        userMealsRef.add(dataToSave).await()
    }

}