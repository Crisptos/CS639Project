package com.calogoal.viewmodels

import android.util.Log
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calogoal.enums.Sex
import com.calogoal.interfaces.FirestoreService
import com.calogoal.models.accounts.User
import com.calogoal.models.dtos.ProfileDTO
import com.calogoal.util.toEpochMillis
import com.calogoal.util.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val dateOfBirth: LocalDate? = null,
    val sex: Sex = Sex.Male,
    val age: String = "",
    val weight: String = "",
    val height: String = ""
)

@HiltViewModel
class ProfilePageViewModel @Inject constructor(
    private val firestoreService: FirestoreService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val data: ProfileDTO = firestoreService.getProfile()
            val dobValueLocalDate: LocalDate = data.dateOfBirth.toLocalDate()
            val ageValue: String = data.age.toString()
            val weightValue: String = data.weight.toString()
            val heightValue: String = data.height.toString()
            val sexValue: Sex = Sex.fromInt(data.sex)
            _uiState.value = ProfileUiState(
                name = data.name,
                dateOfBirth = dobValueLocalDate,
                age = ageValue,
                weight = weightValue,
                height = heightValue,
                sex = sexValue
            )
        }
    }

    fun saveCurrentProfileToFirestore() {
        val ageValue: Int = _uiState.value.age.toIntOrNull() ?: 0
        val dobMillisValue = uiState.value.dateOfBirth?.toEpochMillis() ?: 0L
        val weightValue: Int = _uiState.value.weight.toIntOrNull() ?: 0
        val heightValue: Int = _uiState.value.height.toIntOrNull() ?: 0

        val dataToSave: ProfileDTO = ProfileDTO(
            name = _uiState.value.name,
            dateOfBirth = dobMillisValue,
            age = ageValue,
            weight = weightValue,
            height = heightValue,
            sex = _uiState.value.sex.ordinal
        )
        viewModelScope.launch {
            firestoreService.setProfile(dataToSave)
        }

        Log.d("Calogoal App", "Saved profile to Firestore")
    }

    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun updateDateOfBirth(dob: LocalDate) {
        _uiState.value = _uiState.value.copy(dateOfBirth = dob)
    }

    fun updateAge(newAge: String) {
        _uiState.value = _uiState.value.copy(age = newAge)
    }

    fun updateHeight(newHeight: String) {
        _uiState.value = _uiState.value.copy(height = newHeight)
    }

    fun updateWeight(newWeight: String) {
        _uiState.value = _uiState.value.copy(weight = newWeight)
    }

    fun updateSex(newSex: Sex) {
        _uiState.value = _uiState.value.copy(sex = newSex)
    }
}