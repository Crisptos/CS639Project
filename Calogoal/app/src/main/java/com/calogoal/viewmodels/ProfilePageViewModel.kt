package com.calogoal.viewmodels

import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calogoal.interfaces.FirestoreService
import com.calogoal.models.accounts.User
import com.calogoal.models.dtos.ProfileDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
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
            val ageValue: String = data.age.toString()
            val weightValue: String = data.weight.toString()
            val heightValue: String = data.height.toString()
            _uiState.value = ProfileUiState(
                name = data.name,
                age = ageValue,
                weight = weightValue,
                height = heightValue
            )
        }
    }

    fun saveCurrentProfileToFirestore() {
        val ageValue: Int = _uiState.value.age.toIntOrNull() ?: 0
        val weightValue: Int = _uiState.value.weight.toIntOrNull() ?: 0
        val heightValue: Int = _uiState.value.height.toIntOrNull() ?: 0

        val dataToSave: ProfileDTO = ProfileDTO(
            name = _uiState.value.name,
            age = ageValue,
            weight = weightValue,
            height = heightValue
        )
        viewModelScope.launch {
            firestoreService.setProfile(dataToSave)
        }
    }

    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
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
}